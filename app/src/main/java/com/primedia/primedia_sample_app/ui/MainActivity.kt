package com.primedia.primedia_sample_app.ui

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.RemoteException
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.primedia.primedia_sample_app.App
import com.primedia.primedia_sample_app.R
import com.primedia.primedia_sample_app.triton.BackgroundMusicService
import com.primedia.primedia_sample_app.triton.Player
import com.tritondigital.player.MediaPlayer
import io.reactivex.android.schedulers.AndroidSchedulers
import org.kodein.di.generic.instance
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private val player: Player by App.kodein.instance<Player>()

    private lateinit var viewModel: HomeViewModel

    private val rxSubs: io.reactivex.disposables.CompositeDisposable by lazy {
        io.reactivex.disposables.CompositeDisposable()
    }

    private var mediaBrowserCompat: MediaBrowserCompat? = null
    private var mediaControllerCompat: MediaControllerCompat? = null

    private var image: ImageView? = null
    private var title: TextView? = null
    private var description: TextView? = null
    private var play: ImageButton? = null
    private var skip: ImageButton? = null
    private var errorWarning: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Timber.d("Starts the app" )
        createViewModel()
        setUpViews()
    }


    private fun createViewModel() {

        viewModel = obtainViewModel(HomeViewModel::class.java)

        rxSubs.add(player.playerStateObservable
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ status ->
                if (status == MediaPlayer.STATE_ERROR){
                    errorWarning?.text = "Error State Has Come Through"
                }
                if (!isMyServiceRunning(BackgroundMusicService::class.java)) {
                    launchService()
                }
            }, {

            })
        )
    }

    private fun setUpViews(){
        image = findViewById(R.id.image)
        title = findViewById(R.id.title)
        description = findViewById(R.id.description)
        play = findViewById(R.id.play)
        skip = findViewById(R.id.skip)
        errorWarning = findViewById(R.id.txt_error_warning)

        play?.setOnClickListener {
            if(viewModel.isPausable()){
                viewModel.pause()
            }else {
                setViewData()
                viewModel.play()
            }
        }

        skip?.setOnClickListener {
            errorWarning?.text = ""
            setViewData()
            viewModel.play()
        }

    }

    private fun setViewData(){
        val position = if (viewModel.currentPosition > 9) 0 else viewModel.currentPosition
        val currentItem = viewModel.songList[position]
        title?.text = currentItem.title
        description?.text = currentItem.description
    }

    private val mediaControllerCallback = object : MediaControllerCompat.Callback() {
    }

    private val mediaBrowserConnectionCallback = object : MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            super.onConnected()
            try {
                if (mediaBrowserCompat?.sessionToken != null) {
                    mediaControllerCompat = MediaControllerCompat(this@MainActivity, mediaBrowserCompat?.sessionToken!!)
                    mediaControllerCompat?.registerCallback(mediaControllerCallback)
                }

            } catch (e: RemoteException) {

            }
        }
    }

    private fun launchService() {
        val intent = Intent(this.applicationContext, BackgroundMusicService::class.java)
        mediaBrowserCompat = MediaBrowserCompat(
            this, ComponentName(this, BackgroundMusicService::class.java),
            mediaBrowserConnectionCallback, intent.extras
        )
        mediaBrowserCompat?.connect()
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {

                return true
            }
        }
        return false
    }


    private fun <T : ViewModel> obtainViewModel(viewModelClass: Class<T>) =
        ViewModelProviders.of(this, ViewModelFactory.getInstance(application)).get(viewModelClass)
}