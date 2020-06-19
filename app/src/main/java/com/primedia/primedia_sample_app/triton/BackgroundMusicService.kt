package com.primedia.primedia_sample_app.triton

import android.app.NotificationManager
import android.app.Service
import android.content.*
import android.graphics.Bitmap
import android.media.AudioManager
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.text.TextUtils
import android.view.KeyEvent
import androidx.core.content.ContextCompat
import androidx.media.MediaBrowserServiceCompat
import androidx.media.session.MediaButtonReceiver
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.primedia.primedia_sample_app.App
import com.primedia.primedia_sample_app.R
import com.primedia.primedia_sample_app.models.StreamItemDataModelType
import com.tritondigital.player.MediaPlayer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.kodein.di.generic.instance
import timber.log.Timber

class BackgroundMusicService : MediaBrowserServiceCompat(), AudioManager.OnAudioFocusChangeListener {

    companion object {
        const val NOW_PLAYING_NOTIFICATION = 123
    }


    private val kContext: Context by App.kodein.instance<Context>()
    private val player: Player by App.kodein.instance<Player>()

    private val rxSubs: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    private var mediaSession: MediaSessionCompat? = null
    private lateinit var mediaControllerCompat: MediaControllerCompat
    private var mediaNotificationBuilder: MediaNotificationBuilderHelper
    private var notificationManager: NotificationManager

    private var isForegroundService = false

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    private var currentMetaData: MediaMetadataCompat? = null
    private var currentState: PlaybackStateCompat? = null

    //noisy rreceiverfor audio change ie plugging in headphones
    private val mNoisyReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            if (player.isPausable()) {
                player.pause()
            }
        }
    }

    init {
        notificationManager = kContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mediaNotificationBuilder = MediaNotificationBuilderHelper(kContext, notificationManager)
    }

    override fun onCreate() {
        super.onCreate()
        initMediaSession()

        //noisey receiver is used when headphones are plugged in to pause
        initNoiseyReceiver()

        //update notification on change in state or metadata
        rxSubs.add(player.playerStateObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({statusInt ->
                setCurrentMetaData()
                handleStateChange(statusInt)
            },{

            }))

        rxSubs.add(player.playerDisplayModelObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( {
                setCurrentMetaData()
                // keeps seekbar up-to-date
                val playState = updatePlayBackStateBuilder()
                mediaSession?.setPlaybackState(playState)
            }, {
                Timber.e(it, "error getting player display model observable")
            }))
    }

    override fun onDestroy() {
        mediaSession?.run {
            isActive = false
            release()
        }

        unregisterReceiver(mNoisyReceiver)
        stopSelf()
        removeNowPlayingNotification()

        rxSubs.clear()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        stopSelf()
        stopForeground(true)
        removeNowPlayingNotification()
        player.release()

        super.onTaskRemoved(rootIntent)
    }


    private fun initMediaSession() {
        //receiver gets the callbacks from the system player controls
        val mediaButtonReceiver = ComponentName(this, MediaButtonReceiver::class.java)

        //media session is used to control the various media processes needed to display to the system and connect to other media devices
        mediaSession = MediaSessionCompat(kContext, "com.primedia.primedia_sample_app.triton.BackgroundMusicService", mediaButtonReceiver, null)
        val token = mediaSession?.sessionToken
        Timber.d("PLAYERTOKEN = $token")

        mediaSession?.setCallback(mediaSessionCallback)
        mediaSession?.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)

//        metaDataBundle = player.getLastMetaDataReceived()
        setCurrentMetaData()

        val playState = updatePlayBackStateBuilder()

        if (currentState != playState) {
            mediaSession?.setPlaybackState(playState)
            currentState = playState
        }

//        setMediaButtonReceiver()

        //media controller gets callbacks whenever the session state or meta data is changed and set to the session
        mediaControllerCompat = MediaControllerCompat(kContext, mediaSession?.sessionToken!!)

        mediaControllerCompat.registerCallback(object : MediaControllerCompat.Callback() {
            override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
                mediaControllerCompat.playbackState?.let { state ->
                    serviceScope.launch {
                        Timber.d("On MetaData changed Update notification")
                        updateNotification(state)
                    }
                }
                Timber.d("onMetaDataChanged")
            }

            override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
                state?.let {
                    serviceScope.launch {
                        Timber.d("On Playback State changed update notification")
                        updateNotification(state)
                    }
                }
                Timber.d("OnPlayBackStateChanged")
            }
        })
        sessionToken = mediaSession?.sessionToken

        mediaSession?.isActive = true
    }

    private fun handleStateChange(state: Int) {
        if (mediaSession != null) {
            val playState = updatePlayBackStateBuilder(state)
            if (currentState != playState) {
                mediaSession?.setPlaybackState(playState)
                currentState = playState
                mediaSession?.isActive = true
            }
        }
    }

    /* updating of the notification on a background thread
        get a different notification type whether clip or stream
        depending on the play state start or stop the background service
    */
    private fun updateNotification(state: PlaybackStateCompat) {
        val updatedState = state.state

        val notification = if (mediaControllerCompat.metadata != null && updatedState != PlaybackStateCompat.STATE_NONE) {
            if (player.currentStationType == StreamItemDataModelType.STREAM) {

                mediaNotificationBuilder.buildLiveStreamNotification(state, mediaSession?.sessionToken!!)
            } else {
                mediaNotificationBuilder.buildClipNotification(state, mediaSession?.sessionToken!!)
            }
        } else {
            null
        }

        when (state.state) {
            PlaybackStateCompat.STATE_BUFFERING,
            PlaybackStateCompat.STATE_PLAYING -> {
                if (notification != null) {
                    notificationManager.notify(NOW_PLAYING_NOTIFICATION, notification)

                    if (!isForegroundService) {
                        ContextCompat.startForegroundService(
                            kContext,
                            Intent(kContext, this@BackgroundMusicService::class.java)
                        )

                        startForeground(NOW_PLAYING_NOTIFICATION, notification)
                        isForegroundService = true
                    }
                }
            }
            PlaybackStateCompat.STATE_PAUSED -> {
                if (isForegroundService) {
                    stopForeground(false)
                    isForegroundService = false

                    if (notification != null) {
                        notificationManager.notify(NOW_PLAYING_NOTIFICATION, notification)
                    }
                }
            }

            PlaybackStateCompat.STATE_NONE -> {
                // If playback has ended, also stop the service.
                stopSelf()
            }
            PlaybackStateCompat.STATE_STOPPED -> {
                Timber.d("State stopped")

                stopSelf()

                if (isForegroundService && !player.appClosing) {
                    stopForeground(false)
                    isForegroundService = false
                    if (notification != null) {
                        notificationManager.notify(NOW_PLAYING_NOTIFICATION, notification)
                    } else {
                        removeNowPlayingNotification()
                    }
                } else {
                    removeNowPlayingNotification()
                }
            }
            else -> {
                if (isForegroundService) {
                    stopForeground(false)
                    isForegroundService = false

                    if (notification != null) {
                        notificationManager.notify(NOW_PLAYING_NOTIFICATION, notification)
                    } else {
                        removeNowPlayingNotification()
                    }
                }
            }
        }
    }

    private fun removeNowPlayingNotification() {
        stopForeground(true)
    }

    // this gets the callbacks from media notifications that are passed onto the receiver for this media session
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        Timber.d("On start command called")
        if (intent != null) handleIntent(intent)
        MediaButtonReceiver.handleIntent(mediaSession, intent)
        return Service.START_NOT_STICKY
    }

    private fun handleIntent(intent: Intent) =
        (intent.extras?.get(Intent.EXTRA_KEY_EVENT) as KeyEvent?)?.keyCode.also {
            when (it) {
                KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                    Timber.d("Handle Intent pause hit")
                    player.pause()
                }
                KeyEvent.KEYCODE_MEDIA_PLAY -> {
                    Timber.d("Handle Intent play hit")
                    player.resumeStream()

                }
                KeyEvent.KEYCODE_MEDIA_STOP -> {
                    Timber.d("On Stop hit")
                    player.stop()
                }
                KeyEvent.KEYCODE_MEDIA_NEXT -> {
//                    player.playNextClip(true)

                }
                KeyEvent.KEYCODE_MEDIA_PREVIOUS -> {
//                    player.playPreviousClip()
                }
            }
        }

    // onLoadChildren() and onGetRoot() are used for the connections to other devices and then controlling
    // what data needs to be shown based off the device. Currently have basic set up which allows just the app
    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        Timber.d("onLoadChildren")
        result.sendResult(null)
    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot? {
        Timber.d("onGetRoot")
        if (TextUtils.equals(clientPackageName, packageName)) {
            return BrowserRoot(getString(R.string.app_name), null)
        }
        // allow and disallow different client connections here
        return null//MediaBrowserServiceCompat.BrowserRoot(MY_MEDIA_ROOT_ID, null)
    }

    // todo implement audio focus change states
    override fun onAudioFocusChange(p0: Int) {
        Timber.d("OnAudioFocusChanged")
    }

    private fun initNoiseyReceiver() {
        val filter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        registerReceiver(mNoisyReceiver, filter)
    }

    //callback that calls the method depending on the action used on the notification
    private val mediaSessionCallback: MediaSessionCompat.Callback = object : MediaSessionCompat.Callback() {
        override fun onMediaButtonEvent(mediaButtonEvent: Intent): Boolean {
            Timber.d("Media button event")
            return super.onMediaButtonEvent(mediaButtonEvent)
        }


        override fun onPlay() {
            super.onPlay()
            if (!successfullyRetrievedAudioFocus()) {
                return
            }

            Timber.d("Media Session Callback on play triggered")
            player.resumeStream()
        }

        override fun onPause() {
            super.onPause()
            player.pause()
            Timber.d("Pause")
        }

        override fun onStop() {
            super.onStop()
            player.stop()
        }

        override fun onSkipToNext() {
            super.onSkipToNext()
//            player.playNextClip(true)
            Timber.d("onSkipToNext")
        }

        override fun onSkipToPrevious() {
            super.onSkipToPrevious()
//            player.playPreviousClip()
            Timber.d("onSkipToPrevious")
        }

        override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
            super.onPlayFromMediaId(mediaId, extras)
            Timber.d("Play from media id")
        }

        override fun onCommand(command: String, extras: Bundle?, cb: ResultReceiver?) {
            super.onCommand(command, extras, cb)
            Timber.d("On Command")
        }

        override fun onSeekTo(pos: Long) {
            super.onSeekTo(pos)
            player.seekTo(pos.toInt())
            Timber.d("Seek to")
            val playState = updatePlayBackStateBuilder()
            mediaSession?.setPlaybackState(playState)
        }
    }

    private fun successfullyRetrievedAudioFocus(): Boolean {
        val audioManager = kContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
        return result == AudioManager.AUDIOFOCUS_GAIN
    }

    // getting the playback state based off triton players states
    private fun getPlayBackState(state: Int, playbackStateBuilder: PlaybackStateCompat.Builder): Int {
        val playbackState: Int

        when (state) {
            MediaPlayer.STATE_PLAYING -> {
                playbackState = PlaybackStateCompat.STATE_PLAYING
                playbackStateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE or PlaybackStateCompat.ACTION_PAUSE or PlaybackStateCompat.ACTION_SEEK_TO)
            }
            MediaPlayer.STATE_STOPPED -> {
                playbackState = PlaybackStateCompat.STATE_STOPPED
                playbackStateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE or PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_SEEK_TO)
            }
            MediaPlayer.STATE_PAUSED -> {
                playbackState = PlaybackStateCompat.STATE_PAUSED
                playbackStateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE or PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_SEEK_TO)
            }
            MediaPlayer.STATE_CONNECTING -> {
                playbackState = PlaybackStateCompat.STATE_CONNECTING
                playbackStateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE or PlaybackStateCompat.ACTION_PAUSE or PlaybackStateCompat.ACTION_SEEK_TO)
            }
            else -> {
                playbackState = PlaybackStateCompat.STATE_NONE
            }
        }

        return playbackState
    }

    // setting the necessary meta data to display in the notification
    private fun setCurrentMetaData() {
        val builder = MediaMetadataCompat.Builder()

        val playerDisplayModel = player.playDisplayModelData.getDisplayInfo()

        //this sets the total duration of the clip in the seekbar
        if (playerDisplayModel.streamType == StreamItemDataModelType.CLIP) {
            builder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, player.tritonPlayerDuration.toLong())
        }

        builder
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, playerDisplayModel.subtitle)
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, playerDisplayModel.title)
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, playerDisplayModel.image)

        //in bg thread to get the stream image from glide
        serviceScope.launch {
            //getting the stream image here to set the lock icon field
            if (playerDisplayModel.image.isNotEmpty()) {

                Glide.with(kContext).asBitmap().load(playerDisplayModel.image).into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        if (resource != null) builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, resource)

                        val newMetaData = builder.build()
                        if (newMetaData != currentMetaData) {
                            mediaSession?.setMetadata(newMetaData)
                            currentMetaData = newMetaData
                        }
                    }
                })
            }
        }
    }

    //this gets the playbackstatecompat arguments
    private fun updatePlayBackStateBuilder(state: Int = player.playerState): PlaybackStateCompat {
        val playbackStateBuilder = PlaybackStateCompat.Builder()
        val playbackState: Int = getPlayBackState(state, playbackStateBuilder)
        val position = if (player.currentStationType == StreamItemDataModelType.CLIP) player.tritonPlayerPosition.toLong() else -1

        //&& (playbackState == PlaybackStateCompat.STATE_PLAYING || playbackState == PlaybackStateCompat.STATE_PAUSED
        //                    || playbackState == PlaybackStateCompat.STATE_STOPPED)

//
//        //here we set the position of the seekbar and it's state, and playback speed
        if (position >= 0 && player.currentStationType == StreamItemDataModelType.CLIP) {
            playbackStateBuilder.setBufferedPosition(position)
            playbackStateBuilder.setState(playbackState, position, 1.0f)
        } else if (player.currentStationType == StreamItemDataModelType.STREAM) {
            // no seekbar required
            playbackStateBuilder.setState(playbackState, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 0f)
        }

        return playbackStateBuilder.build()
    }
}