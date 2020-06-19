package com.primedia.primedia_sample_app.triton

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.primedia.primedia_sample_app.models.*
import com.primedia.primedia_sample_app.rest.RetrofitService
import com.primedia.primedia_sample_app.room.MediaDbHelper
import com.primedia.primedia_sample_app.room.repositories.MediaRepository
import com.primedia.primedia_sample_app.util.DateTimeUtils
import com.primedia.primedia_sample_app.util.DateTimeUtils.convertStringToLong
import com.primedia.primedia_sample_app.util.ModelConversionUtils.lastPlayedToMediaEntity
import com.tritondigital.player.MediaPlayer
import com.tritondigital.player.TritonPlayer
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber

class Player(
    private val context: Context,
    private val retrofitService: RetrofitService,
    private val mediaRepository: MediaRepository
) {

    companion object {
        private const val STATION_BROADCASTER = "Primedia Broadcasting"
        private const val PLAYER_SERVICES_REGION = "EU"
    }

    private val mainJob = SupervisorJob()
    private val mainScope = CoroutineScope(Dispatchers.Main + mainJob)

    private val mediaDbHelper = MediaDbHelper()

    var playDisplayModelData = PlayerDisplayModelData()

    val hasCurrentStreamDetail: Boolean
        get() = playDisplayModelData.currentStreamDetails != null || !playDisplayModelData.currentStreamDetails?.identifier.isNullOrEmpty()
    val playerState: Int
        get() = tritonPlayer?.state ?: MediaPlayer.STATE_RELEASED
    val currentStreamIdentifier: String
        get() = playDisplayModelData.currentStreamDetails?.identifier ?: ""
    val currentStreamImage: String
        get() = playDisplayModelData.currentStreamDetails?.image_url ?: ""

    val currentStationType: StreamItemDataModelType
        get() = playDisplayModelData.currentStreamDetails?.type ?: StreamItemDataModelType.UNKNOWN

    private var tritonPlayer: TritonPlayer? = null
    val isSeekable: Boolean
        get() = tritonPlayer?.isSeekable ?: run { false }
    val tritonPlayerPosition: Int
        get() = tritonPlayer?.position ?: run { -1 }
    val tritonPlayerDuration: Int
        get() = tritonPlayer?.duration ?: run { -1 }


    private val handler = Handler(Looper.getMainLooper())
    private var runnableIsRunning = false

    var appClosing = false

    val playerDisplayModelObservable: Observable<Any>
        get() = playerDisplayModelPublisher.hide()
    private val playerDisplayModelPublisher: PublishSubject<Any> by lazy {
        PublishSubject.create<Any>()
    }

    val playerStateObservable: Observable<Int>
        get() = playerStatePublisher.hide()
    private val playerStatePublisher: PublishSubject<Int> by lazy {
        PublishSubject.create<Int>()
    }

    private var hasReceivedFirstCuePointForStation: Boolean = false

    init {
        setUpMediaPlayerListeners()
    }

    private fun setUpMediaPlayerListeners() {
        tritonPlayer?.let { player ->

            player.setOnStateChangedListener { mediaPlayer, i ->
                if (MediaPlayer.STATE_PLAYING == i) {
                    if (!runnableIsRunning) handler.post(playerRunningRunnable)
                } else {
                    if (runnableIsRunning) {
                        handler.removeCallbacks(playerRunningRunnable)
                        runnableIsRunning = false
                    }
                }
                playerStatePublisher.onNext(i)
            }

            player.setOnCuePointReceivedListener { mediaPlayer, bundle ->
                resetLastCuePoint(false)
                if (bundle != null) {
                    val metaDataBundle = CuePointMetaDataBundle(
                        bundle.getLong("cue_time_start", System.currentTimeMillis()),
                        bundle.getString("cue_type", ""),
                        bundle.getInt("cue_time_duration", 0),
                        bundle.getString("cue_title", ""),
                        bundle.getString("track_artist_name", ""),
                        bundle.getString("program_id", ""),
                        ""
                    )
                    onCuePointReceived(metaDataBundle)
                }
            }
        }
    }


    private fun getStationPlayerSettings(): Bundle {
        val settings = Bundle()
        playDisplayModelData.currentStreamDetails?.let { stationDetail ->
            settings.putString(TritonPlayer.SETTINGS_STATION_BROADCASTER, STATION_BROADCASTER)
            settings.putString(TritonPlayer.SETTINGS_STATION_NAME, stationDetail.title)
            settings.putString(TritonPlayer.SETTINGS_STATION_MOUNT, stationDetail.identifier)
            settings.putString(
                TritonPlayer.SETTINGS_PLAYER_SERVICES_REGION, PLAYER_SERVICES_REGION
            ) // AP = Asia | EU = Europe | Omit this configuration option for North America
            settings.putBoolean(TritonPlayer.SETTINGS_FORCE_DISABLE_EXOPLAYER, true)
        }
        return settings
    }

    private fun getOnDemandStreamPlayerSettings(url: String): Bundle {
        val settings = Bundle()
        settings.putString(TritonPlayer.SETTINGS_STREAM_URL, url)
        settings.putBoolean(TritonPlayer.SETTINGS_FORCE_DISABLE_EXOPLAYER, true)
        settings.putString(
            TritonPlayer.SETTINGS_PLAYER_SERVICES_REGION, PLAYER_SERVICES_REGION
        ) // AP = Asia | EU = Europe | Omit this configuration option for North America
        return settings
    }

    /*
    initialMediaItemSelection = select a (initial) track/station. i.e. from home screen or search result. Therefore triggering pulling the rest of the random playlist
     */
    fun playStream(tritonStationInfo: StreamItemDataModel = playDisplayModelData.currentStreamDetails ?: StreamItemDataModel(), initialMediaItemSelection: Boolean = false) {
        tritonStationInfo.identifier?.let { identifier ->
            mediaRepository.getPlayEntityWithId(identifier) { playHistoryEntry ->
                mainScope.launch {
                    val storedProgress = playHistoryEntry?.progress ?: kotlin.run { 0 }
                    val storedListenToCompletion = playHistoryEntry?.listenedToCompletion ?: kotlin.run { false }
                    updatePlayerSettings(tritonStationInfo, storedProgress, storedListenToCompletion)

                    tritonPlayer?.play()
                }
            }
        } ?: kotlin.run {
            //TODO - error handling
            Timber.e("Stream identifier is null and therefore cannot play stream/clip.")
        }
    }

    fun resumeStream() {
        tritonPlayer?.play()
    }

    private fun restartStream() {
        tritonPlayer?.seekTo(0)
        triggerSystemPlayerUpdate()
    }

    fun stop() {
        tritonPlayer?.let { player ->
            player.stop()
            resetLastCuePoint()
        }
    }

    fun stopLiveStream() {
        resetSeekbar()
        resetLastCuePoint()
        stop()
    }

    fun pause() {
        tritonPlayer?.let { player ->
            playDisplayModelData = playDisplayModelData.copy(historicalProgress = tritonPlayerPosition)
            player.pause()
        }
    }

    fun isPausable(): Boolean {
        return tritonPlayer?.isPausable ?: false
    }

    fun release() {
        tritonPlayer?.release()
    }

    fun seekTo(pos: Int) {
        tritonPlayer?.seekTo(pos)
        triggerSystemPlayerUpdate()
    }

    private fun shouldRestartTrack(progress: Int, duration: Int, listenedToCompletion: Boolean): Boolean {
        return if (duration > 0) {
            (progress / duration > 0.98) && listenedToCompletion
        } else {
            true // 0 duration means it is a stream
        }
    }

    private fun updatePlayerSettings(tritonStationInfo: StreamItemDataModel, historicalProgress: Int, listenedToCompletion: Boolean) {
        playDisplayModelData = if (shouldRestartTrack(historicalProgress, tritonStationInfo.durationInMillis, listenedToCompletion)) {
            mediaDbHelper.updateMediaItem(lastPlayedToMediaEntity(tritonStationInfo, 0, listenedToCompletion)) {}
            playDisplayModelData.copy(currentStreamDetails = tritonStationInfo, historicalProgress = 0, listenedToCompletion = listenedToCompletion)
        } else {
            mediaDbHelper.updateMediaItem(lastPlayedToMediaEntity(tritonStationInfo, historicalProgress, listenedToCompletion)) {}
            playDisplayModelData.copy(currentStreamDetails = tritonStationInfo, historicalProgress = historicalProgress, listenedToCompletion = listenedToCompletion)
        }


        resetCurrentShowDetail()
        resetLastCuePoint()
        resetSeekbar()
        hasReceivedFirstCuePointForStation = false

        release()

        playDisplayModelData.currentStreamDetails?.let { streamDetail ->
            if (streamDetail.type == StreamItemDataModelType.STREAM) {
                tritonPlayer = TritonPlayer(context, getStationPlayerSettings())
                getStationCurrentShow(streamDetail.identifier)
            } else {
                if (streamDetail.media.isNullOrEmpty()) {
                    //TODO - ERROR... temp for now
                    playerStatePublisher.onNext(MediaPlayer.STATE_ERROR)
                } else {
                    tritonPlayer = TritonPlayer(context, getOnDemandStreamPlayerSettings(streamDetail.media))
                }
            }
        }

        setUpMediaPlayerListeners()
    }

    private fun resetLastCuePoint(publishDisplayDataChange: Boolean = true) {
        playDisplayModelData = playDisplayModelData.copy(lastCuePointDetails = null)
        if (publishDisplayDataChange) playerDisplayModelPublisher.onNext(true)
    }

    private fun resetCurrentShowDetail() {
        playDisplayModelData = playDisplayModelData.copy(currentStationShowDetails = null)
        playerDisplayModelPublisher.onNext(true)
    }

    private fun resetSeekbar() {
        playDisplayModelData = playDisplayModelData.copy(seekbarInfo = SeekbarInfo())
        playerDisplayModelPublisher.onNext(true)
    }
    private fun onCuePointReceived(cuePoint: CuePointMetaDataBundle) {
        if (hasReceivedFirstCuePointForStation) {
            val cuePointReceivedTime = System.currentTimeMillis()

            playDisplayModelData.copy(lastCuePointDetails = cuePoint.copy(startTime = cuePointReceivedTime))
            playerDisplayModelPublisher.onNext(true)
        } else {
            hasReceivedFirstCuePointForStation = true
        }
    }

    private fun getStationCurrentShow(stationIdentifier: String?) {
        retrofitService.getStationCurrentShow(stationIdentifier) { success, stationShow ->
            if (success && stationShow != null) {
                playDisplayModelData = playDisplayModelData.copy(currentStationShowDetails = stationShow)
                playerDisplayModelPublisher.onNext(true)
            } else {
                // TODO -- error handling
                Timber.e("Failed to get current show details.")
            }
        }
    }

    private val playerRunningRunnable = object : Runnable {
        override fun run() {
            try {
                Timber.d("playerRunningRunnable is running")
                if (!runnableIsRunning) { // first round running
                    val seekbarInfo = SeekbarInfo(playDisplayModelData.historicalProgress, playDisplayModelData.currentStreamDetails?.durationInMillis ?: 0)
                    playDisplayModelData = playDisplayModelData.copy(seekbarInfo = seekbarInfo)
                    playerDisplayModelPublisher.onNext(true)
                    runnableIsRunning = true
                }
                else {
                    updateSeekbarInfo()
                }
                handler.postDelayed(this, 1000)
            } catch (e: Exception) {
                Timber.w("playerRunningRunnable not running: $e")
                runnableIsRunning = false
            }
        }
    }

    private fun updateSeekbarInfo() {
        var playerDuration = 0
        var playerPosition = 0

        if (currentStationType == StreamItemDataModelType.STREAM) {

            playDisplayModelData.lastCuePointDetails?.let { cuePoint ->
                if (cuePoint.isCuePointComplete) {
                    resetLastCuePoint(false)
                } else {
                    val startTime = cuePoint.startTime
                    playerDuration = cuePoint.durationInMillis
                    playerPosition = DateTimeUtils.getMillisecondsIntoTrack(startTime).toInt()
                }
            } ?: kotlin.run {
                playDisplayModelData.currentStationShowDetails?.let { currentShow ->
                    val startTime = currentShow.startTime?.let { convertStringToLong(it) } ?: 0
                    val endTime = currentShow.endTime?.let { convertStringToLong(it) } ?: 0
                    playerDuration = (currentShow.duration * 1000).toInt()
                    playerPosition = DateTimeUtils.getMillisecondsIntoTrack(startTime).toInt()

                    if (endTime < System.currentTimeMillis()) {
                        getStationCurrentShow(currentStreamIdentifier)
                    }
                } ?: kotlin.run {
                    getStationCurrentShow(currentStreamIdentifier)
                }
            }

        } else {
            playerPosition = tritonPlayerPosition
            playerDuration = tritonPlayerDuration
            updateCurrentPlayHistoryEntityProgressTime()
        }

        val seekbarInfo = SeekbarInfo(playerPosition, playerDuration)
        playDisplayModelData = playDisplayModelData.copy(seekbarInfo = seekbarInfo)
        playerDisplayModelPublisher.onNext(true)
    }

    private fun updateCurrentPlayHistoryEntityProgressTime() {
        if (tritonPlayer?.state == MediaPlayer.STATE_PLAYING) {

            playDisplayModelData.currentStreamDetails?.let {
                val mediaEntity = lastPlayedToMediaEntity(
                    it,
                    tritonPlayerPosition,
                    playDisplayModelData.listenedToCompletion
                )
                mediaDbHelper.updateMediaItem(mediaEntity) {}
            }
        }
    }

    fun triggerSystemPlayerUpdate() {
        playerDisplayModelPublisher.onNext(true)
    }

}