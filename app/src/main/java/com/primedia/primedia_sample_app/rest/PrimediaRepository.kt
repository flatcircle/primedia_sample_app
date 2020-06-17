package com.primedia.primedia_sample_app.rest

import com.primedia.primedia_sample_app.models.StationLineUp
import com.primedia.primedia_sample_app.models.StationShow
import com.primedia.primedia_sample_app.models.StreamDataModel
import com.primedia.primedia_sample_app.models.StreamItemDataModel

class PrimediaRepository(private val service: RetrofitService) : BaseRepository() {

    private val context = service.appContext
    private val offline: Boolean
        get() = false //!offlineService.isOnline(context)

//    suspend fun getSignupPreferences(): ApiResponse<Preferences>? {
//        if (offline) return null
//        return performCall { service.client.getSignupPreferences() }
//    }
//
//    suspend fun signup(request: RegisterUserRequestModel): ApiResponse<UserSignUpResponse>? {
//        if (offline) return null
//        return performCall { service.client.registerUser(request) }
//    }

    suspend fun getHome(): ApiResponse<List<StreamDataModel>>? {
        if (offline) return null
        return performCall { service.client.getHome() }
    }

//    suspend fun verifyUser(userUuid: String): ApiResponse<UserVerificationResponse>? {
//        if (offline) return null
//        return performCall { service.client.verifyUser(userUuid) }
//    }

    suspend fun getStationLineUp(stationIdentifier: String): ApiResponse<StationLineUp>? {
        if (offline) return null
        return performCall { service.client.getStationLineUp(stationIdentifier) }
    }

    suspend fun getAllCurrentStationShows(): ApiResponse<List<StationShow>>? {
        if (offline) return null
        return performCall { service.client.getAllCurrentStationShows() }
    }

    suspend fun getNextUpClipData(clipUuid: String, pageNumber: String): ApiResponse<List<StreamItemDataModel>>? {
        if (offline) return null
        return performCall { service.client.getNextUpClipData(clipUuid, pageNumber) }
    }

    suspend fun getAllPodcasts(pageNumber: String): ApiResponse<List<StreamItemDataModel>>? {
        if (offline) return null
        return performCall { service.client.getAllPodcasts(pageNumber) }
    }

    suspend fun getNextPodcastClips(podcastUuid: String, clipUuid: String): ApiResponse<List<StreamItemDataModel>>? {
        if (offline) return null
        return performCall { service.client.getNextPodcastClips(podcastUuid, clipUuid) }
    }

    suspend fun getPreviousPodcastClips(podcastUuid: String, clipUuid: String): ApiResponse<List<StreamItemDataModel>>? {
        if (offline) return null
        return performCall { service.client.getPreviousPodcastClips(podcastUuid, clipUuid) }
    }

    suspend fun getNextUpStreamData(streamIdentifier: String, pageNumber: String): ApiResponse<List<StreamItemDataModel>>? {
        if (offline) return null
        return performCall { service.client.getNextUpStreamData(streamIdentifier, pageNumber) }
    }

//    suspend fun saveEvent(obj: AnalyticsEvent): ApiResponse<SaveAnalyticsResponse>? {
//        if (offline) return null
//        return performCall { service.client.saveEvent(obj) }
//    }

//    suspend fun getSeeAllListItems(url: String, pageNumber: String): ApiResponse<List<StreamItemDataModel>>? {
//        if (offline) return null
//        return performCall { service.client.getSeeAllListItems("api$url", pageNumber) }
//    }
}
