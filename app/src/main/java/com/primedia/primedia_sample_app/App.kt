package com.primedia.primedia_sample_app

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.primedia.primedia_sample_app.rest.PrimediaRepository
import com.primedia.primedia_sample_app.rest.RetrofitService
import com.primedia.primedia_sample_app.room.AppDatabase
import com.primedia.primedia_sample_app.room.repositories.MediaRepository
import com.primedia.primedia_sample_app.room.repositories.PlaylistMediaLinkRepository
import com.primedia.primedia_sample_app.room.repositories.PlaylistRepository
import com.primedia.primedia_sample_app.service.PreferenceService
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
//            bind<AuthService>() with singleton {
//                AuthService(
//                    instance(),
//                    instance(),
//                    instance(),
//                    instance(),
//                    instance(),
//                    instance(),
//                    instance()
//                )
//            }
//            bind<AnalyticsService>() with singleton { AnalyticsService(instance(), instance(), instance()) }
            bind<RetrofitService>() with singleton { RetrofitService(instance(), instance()) }
            bind<PrimediaRepository>() with singleton { PrimediaRepository(instance()) }
            bind<Player>() with singleton { Player(instance(), instance(), instance(), instance(), instance(), instance(), instance()) }
            bind<PreferenceService>() with singleton { PreferenceService(instance()) }
            bind<AppDatabase>() with instance(AppDatabase.getInstance(this@App) as AppDatabase)
//            bind<SearchRepository>() with provider { SearchRepository(instance()) }
            bind<MediaRepository>() with provider { MediaRepository(instance()) }
            bind<PlaylistRepository>() with provider { PlaylistRepository(instance()) }
            bind<PlaylistMediaLinkRepository>() with provider { PlaylistMediaLinkRepository(instance()) }
//            bind<OfflineService>() with provider { OfflineService(instance()) }
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