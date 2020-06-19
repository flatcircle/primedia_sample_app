package com.primedia.primedia_sample_app

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.primedia.primedia_sample_app.rest.RetrofitService
import com.primedia.primedia_sample_app.room.AppDatabase
import com.primedia.primedia_sample_app.room.repositories.MediaRepository
import com.primedia.primedia_sample_app.triton.Player
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton
import timber.log.Timber

class App: Application() {

    companion object {
        lateinit var kodein: Kodein
    }

    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        kodein = Kodein {
            bind<Context>() with instance(this@App)
            bind<RetrofitService>() with singleton { RetrofitService(instance()) }
            bind<Player>() with singleton { Player(instance(), instance(), instance()) }
            bind<AppDatabase>() with instance(AppDatabase.getInstance(this@App) as AppDatabase)
            bind<MediaRepository>() with provider { MediaRepository(instance()) }
        }

        Timber.plant(Timber.DebugTree())
    }

    override fun onTerminate() {
        val player: Player by kodein.instance<Player>()
        player.appClosing = true
        player.stop()
        player.stopLiveStream()
        super.onTerminate()
    }
}