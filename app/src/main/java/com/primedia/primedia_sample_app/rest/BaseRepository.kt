package com.primedia.primedia_sample_app.rest

import retrofit2.Response

open class BaseRepository {
    suspend fun <T : Any> performCall(call: suspend () -> Response<T>): ApiResponse<T>? {
        val response = call.invoke()
        val body = response.body()
        return if (response.isSuccessful && body != null)
            ApiResponse(body)
        else
            ApiResponse(Exception("${response.errorBody()}"))

    }
}
