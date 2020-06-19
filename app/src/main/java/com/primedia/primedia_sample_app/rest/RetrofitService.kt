package com.primedia.primedia_sample_app.rest

import android.content.Context
import com.google.gson.GsonBuilder
import com.primedia.primedia_sample_app.ServerInfo
import com.primedia.primedia_sample_app.models.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit

class RetrofitService(val appContext: Context) {

    companion object {
        const val CONNECTION_TIMEOUT = 60L
    }

    var client: PrimediaRestApi = {
        val builder = OkHttpClient.Builder()
//            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
//            .addInterceptor { chain ->
//                val token = preferenceService.token
//                Timber.d("FIREBASE_TOKEN = $token")
//                val requestBuilder = chain.request().newBuilder()
//                if (token != null) {
//                    requestBuilder.header("Authorization", "Bearer $token")
//                }
//                chain.proceed(requestBuilder.build())
//            }
            .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .callTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS).build()
        val gson = GsonBuilder()
        gson.registerTypeAdapter(StreamItemDataModelType::class.java, StreamItemDataModelTypeDeserializer())
        gson.registerTypeAdapter(StreamLayoutType::class.java, StreamLayoutTypeDeserializer())

        val baseUrl = ServerInfo.BASE_PRIMEDIA_REST_URL

        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson.create()))
            .client(builder)
            .build().create(PrimediaRestApi::class.java)
    }()

    fun getStationCurrentShow(stationIdentifier: String?, successFunction: ((success: Boolean, stationShow: StationShow?) -> Unit)) {
//        Timber.d("getStationCurrentShow for stationIdentifier == ${stationIdentifier ?: "null"}")
        stationIdentifier?.let { identifier ->
            client.getCurrentStationShow(identifier).enqueue(object : Callback<StationShow> {
                override fun onFailure(call: Call<StationShow>, t: Throwable) {
                    successFunction(false, null)
                }

                override fun onResponse(call: Call<StationShow>, response: Response<StationShow>) {
                    val responseBody = response.body()
                    responseBody?.let {
                        val stationShow = StationShow(
                            it.showId,
                            it.stationIdentifier,
                            it.stationImage,
                            it.showName,
                            it.startTime,
                            it.endTime,
                            it.image,
                            it.description,
                            it.duration
                        )
                        successFunction(true, stationShow)
                    } ?: kotlin.run {
                        successFunction(false, null)
                    }
                }
            })
        } ?: kotlin.run {
            Timber.e("Cannot get current show if stationIdentifier is null.")
            successFunction(false, null)
        }
    }
}
