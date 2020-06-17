package com.primedia.primedia_sample_app.models

import com.primedia.primedia_sample_app.util.DateTimeUtils

data class CuePointMetaDataBundle(
    val startTime: Long,
    val type: String,
    val timeDuration: Int,
    val title: String,
    val artist: String,
    val trackId: String,
    val trackImageUrl: String
) {
    val durationInMillis: Int
        get() = timeDuration * 1000

    val isCuePointComplete: Boolean
        get() = DateTimeUtils.isCuePointComplete(this)
}