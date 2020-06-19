package com.primedia.primedia_sample_app.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory private constructor(private val application: Application): ViewModelProvider.NewInstanceFactory() {

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(app: Application) =
            INSTANCE ?: synchronized(ViewModelFactory::class.java){
                INSTANCE ?: ViewModelFactory(app)
                    .also { INSTANCE = it }
            }
    }

    override fun <T: ViewModel?> create(modelClass: Class<T>): T {
        return with(modelClass) {
            when {
//                isAssignableFrom(PlayerViewModel::class.java) -> PlayerViewModel(application)
//                isAssignableFrom(AuthViewModel::class.java) -> AuthViewModel(application)
//                isAssignableFrom(SettingsViewModel::class.java) -> SettingsViewModel(application)
//                isAssignableFrom(SignupPreferencesViewModel::class.java) -> SignupPreferencesViewModel()
//                isAssignableFrom(SearchViewModel::class.java) -> SearchViewModel(application)
                isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(application)
//                isAssignableFrom(PodcastDetailViewModel::class.java) -> PodcastDetailViewModel(application)
                else ->  throw IllegalArgumentException("Unknown ViewModel Class: ${modelClass.name}")
            }
        } as T
    }
}