package com.primedia.primedia_sample_app.triton

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.media.session.MediaButtonReceiver
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.primedia.primedia_sample_app.R
import com.primedia.primedia_sample_app.ui.MainActivity
import com.primedia.primedia_sample_app.util.PreferencesConstants
import timber.log.Timber

class MediaNotificationBuilderHelper(private val context: Context, private val notificationManager: NotificationManager) {

    companion object {
        private const val NOW_PLAYING_CHANNEL = "io.flatcircle.primedia_kotlin_native.NOW_PLAYING"
    }

    private val playAction = NotificationCompat.Action(
        R.drawable.ic_mini_play,
        "Play",
        MediaButtonReceiver.buildMediaButtonPendingIntent(
            context,
            PlaybackStateCompat.ACTION_PLAY
        )
    )

    private val pauseAction = NotificationCompat.Action(
        R.drawable.ic_pause_black_24dp,
        "Pause",
        MediaButtonReceiver.buildMediaButtonPendingIntent(
            context,
            PlaybackStateCompat.ACTION_PAUSE
        )
    )

    private val stopAction = NotificationCompat.Action(
        R.drawable.ic_mini_stop,
        "Pause",
        MediaButtonReceiver.buildMediaButtonPendingIntent(
            context,
            PlaybackStateCompat.ACTION_PAUSE
        )
    )

    private val previousAction = NotificationCompat.Action(
        R.drawable.ic_player_previous,
        "Previous",
        MediaButtonReceiver.buildMediaButtonPendingIntent(
            context,
            PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
        )
    )

    private val nextAction = NotificationCompat.Action(
        R.drawable.ic_player_next,
        "Next",
        MediaButtonReceiver.buildMediaButtonPendingIntent(
            context,
            PlaybackStateCompat.ACTION_SKIP_TO_NEXT
        )
    )

    private val stopPendingIntent = MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_STOP)

    fun buildLiveStreamNotification(state: PlaybackStateCompat,
                                    sessionToken: MediaSessionCompat.Token): Notification{
        if (shouldCreateNowPlayingChannel()){
            getAndroidNotificationChannel(NOW_PLAYING_CHANNEL, "random title")
        }

        val controller = MediaControllerCompat(context, sessionToken)
        val description = controller.metadata.description

        val builder = NotificationCompat.Builder(context, NOW_PLAYING_CHANNEL)

        val playPauseAction = if (state.state == PlaybackStateCompat.STATE_PLAYING || state.state == PlaybackStateCompat.STATE_BUFFERING){
            stopAction
        }else{
            playAction
        }

        val largeIconBitmap = getLargeIconBitmap(controller)

        if (largeIconBitmap != null){
            builder.setLargeIcon(largeIconBitmap)
        }

        val notificationIntent = Intent(context, MainActivity::class.java)
        notificationIntent.putExtra(PreferencesConstants.GO_TO_PLAYER.value, true)
        val intent = PendingIntent.getActivity(context, 0, notificationIntent, 0)

//        Timber.d("Did we get the notification drawable: $largeIconBitmap")

        return builder
            .setContentIntent(intent)
            .setContentText(description.subtitle)
            .setContentTitle(description.title)
            .setColor(context.resources.getColor(R.color.colorAccent))
            .setColorized(true)
            .addAction(playPauseAction)
            .setSound(null)
            .setVibrate(null)
            .setDeleteIntent(stopPendingIntent)
//            .setLargeIcon(largeIconBitmap)
            .setSmallIcon(R.drawable.ic_audiotrack_dark)
            .setStyle(
            androidx.media.app.NotificationCompat.MediaStyle()
                .setCancelButtonIntent(
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        context,
                        PlaybackStateCompat.ACTION_STOP
                    )
                )
                .setShowActionsInCompactView(0)
                .setShowCancelButton(true)
                .setMediaSession(sessionToken))
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    fun buildClipNotification(state: PlaybackStateCompat,
                              sessionToken: MediaSessionCompat.Token): Notification{
        if (shouldCreateNowPlayingChannel()){
            getAndroidNotificationChannel(NOW_PLAYING_CHANNEL, "random title")
        }

        val controller = MediaControllerCompat(context, sessionToken)
        val description = controller.metadata.description
        val playbackState = controller.playbackState

        val builder = NotificationCompat.Builder(context, NOW_PLAYING_CHANNEL)

        val playPauseAction = if (state.state == PlaybackStateCompat.STATE_PLAYING || state.state == PlaybackStateCompat.STATE_BUFFERING){
            pauseAction
        }else{
            playAction
        }

        val largeIconBitmap = getLargeIconBitmap(controller)

        if (largeIconBitmap != null){
            builder.setLargeIcon(largeIconBitmap)
        }

        val mediaStyle = androidx.media.app.NotificationCompat.MediaStyle()
            .setCancelButtonIntent(stopPendingIntent)
            .setMediaSession(sessionToken)
            .setShowActionsInCompactView(0,1,2)
            .setShowCancelButton(true)

        val notificationIntent = Intent(context, MainActivity::class.java)
        notificationIntent.putExtra(PreferencesConstants.GO_TO_PLAYER.value, true)
        val intent = PendingIntent.getActivity(context, 0, notificationIntent, 0)


        return builder
            .setContentIntent(intent)
            .setContentText(description.subtitle)
            .setContentTitle(description.title)
            .setColor(context.resources.getColor(R.color.colorAccent))
            .setColorized(true)
            .setSound(null)
            .setVibrate(null)
            .addAction(previousAction)
            .addAction(playPauseAction)
            .addAction(nextAction)
            .setDeleteIntent(stopPendingIntent)
//            .setLargeIcon(largeIconBitmap)
            .setOnlyAlertOnce(true)
            .setSmallIcon(R.drawable.ic_audiotrack_dark)
            .setStyle(mediaStyle)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

    }

    private fun getLargeIconBitmap(controller: MediaControllerCompat): Bitmap? {
        val imageUrl = controller.metadata?.getString(MediaMetadataCompat.METADATA_KEY_ALBUM)
//        Timber.d("Image url is: $imageUrl")
        return if (!imageUrl.isNullOrEmpty()) {
            Glide.with(context).asBitmap().load(imageUrl).listener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                    Timber.d("Failed to load bitmap image")
                    return true

                }

                override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
//                    Timber.d("Loaded bitmap image")
                    return true
                }

            }).submit(100, 100).get() ?: null
        }else{
            null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getAndroidNotificationChannel(id: String, title: String){



        val notificationChannel = NotificationChannel(NOW_PLAYING_CHANNEL,
            "Now Playing",
            NotificationManager.IMPORTANCE_LOW)
            .apply {
                description = "Currently Playing"
                setSound(null, null)
                enableVibration(false)
                setShowBadge(false)
            }

        notificationManager.createNotificationChannel(notificationChannel)
    }

    private fun shouldCreateNowPlayingChannel() =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !nowPlayingChannelExists()

    @RequiresApi(Build.VERSION_CODES.O)
    private fun nowPlayingChannelExists() =
        notificationManager.getNotificationChannel(NOW_PLAYING_CHANNEL) != null
}