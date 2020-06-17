package com.primedia.primedia_sample_app.rest

class ApiResponse<out T> private constructor(
    private val data: T?,
    private val error: Throwable?
) {

    val isSuccessful: Boolean
        get() = data != null && error == null

    constructor(data: T) : this(data, null)

    constructor(exception: Throwable) : this(null, exception)

    fun data(): T {
        check(error == null) { "Check isSuccessful first: call error() instead." }
        return data!!
    }

    fun error(): Throwable {
        check(data == null) { "Check isSuccessful first: call data() instead." }
        return error!!
    }
}