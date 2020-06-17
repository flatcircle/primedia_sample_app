package com.primedia.primedia_sample_app.rest


import com.primedia.primedia_sample_app.models.StationLineUp
import com.primedia.primedia_sample_app.models.StationShow
import com.primedia.primedia_sample_app.models.StreamDataModel
import com.primedia.primedia_sample_app.models.StreamItemDataModel
//import io.flatcircle.primedia_kotlin_native.models.*
//import io.flatcircle.primedia_kotlin_native.models.analytics.AnalyticsEvent
//import io.flatcircle.primedia_kotlin_native.models.analytics.SaveAnalyticsResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface PrimediaRestApi {

//    @GET("api/preference")
//    suspend fun getSignupPreferences(): Response<Preferences>
//
//    @POST("api/user/save")
//    suspend fun registerUser(@Body obj: RegisterUserRequestModel): Response<UserSignUpResponse>

    @GET("api/home")
    suspend fun getHome(): Response<List<StreamDataModel>>

//    @GET("api/user/verify/{userId}")
//    suspend fun verifyUser(@Path("userId") userId: String): Response<UserVerificationResponse>

    @GET("api/media/lineup/{stationIdentifier}")
    suspend fun getStationLineUp(@Path("stationIdentifier") stationIdentifier: String): Response<StationLineUp>

    @GET("api/media/currentshow")
    suspend fun getAllCurrentStationShows(): Response<List<StationShow>>

    @GET("api/media/currentshow/{stationIdentifier}")
    fun getCurrentStationShow(@Path("stationIdentifier") stationIdentifier: String): Call<StationShow>

//    @GET("api/media/artwork/{trackId}")
//    fun getTrackArtwork(@Path("trackId") trackId: String): Call<TrackArtwork>

    @GET("api/media/search/{searchTerm}")
    fun getSearchResults(@Path("searchTerm") searchTerm: String, @Query("pageNumber") pageNumber: Int): Call<List<StreamItemDataModel>>

    @GET("api/media/upnext/clip/{clip_uuid}")
    suspend fun getNextUpClipData(@Path("clip_uuid") clipUuid: String): Response<List<StreamItemDataModel>>

    @GET("api/media/trending/search")
    suspend fun getTrendingSearches(): Response<List<StreamItemDataModel>>

    @GET("api/media/upnext/clip/{clip_uuid}/")
    suspend fun getNextUpClipData(@Path("clip_uuid") clipUuid: String, @Query("pageNumber") pageNumber: String): Response<List<StreamItemDataModel>>

    @GET("api/home/podcasts/")
    suspend fun getAllPodcasts(@Query("pageNumber") pageNumber: String): Response<List<StreamItemDataModel>>

    @GET("api/podcast/{podcastUuid}")
    fun getPodcast(@Path("podcastUuid") podcastUuid: String, @Query("pageNumber") pageNumber: String): Call<List<StreamItemDataModel>>

    @GET("api/podcast/{podcast_uuid}/clip/{clip_uuid}/next")
    suspend fun getNextPodcastClips(@Path("podcast_uuid") podcastUuid: String, @Path("clip_uuid") clipUuid: String): Response<List<StreamItemDataModel>>

    @GET("api/podcast/{podcast_uuid}/clip/{clip_uuid}/previous")
    suspend fun getPreviousPodcastClips(@Path("podcast_uuid") podcastUuid: String, @Path("clip_uuid") clipUuid: String): Response<List<StreamItemDataModel>>

    @GET("api/media/upnext/v2/stream/{streamIdentifier}/")
    suspend fun getNextUpStreamData(@Path("streamIdentifier") streamIdentifier: String, @Query("pageNumber") pageNumber: String): Response<List<StreamItemDataModel>>

//    @POST("api/analytics/save")
//    suspend fun saveEvent(@Body obj: AnalyticsEvent): Response<SaveAnalyticsResponse>

    @GET
    suspend fun getSeeAllListItems(@Url url: String, @Query("pageNumber") pageNumber: String): Response<List<StreamItemDataModel>>

}
