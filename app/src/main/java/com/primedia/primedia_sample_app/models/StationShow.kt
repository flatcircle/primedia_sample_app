package com.primedia.primedia_sample_app.models

data class StationShow(
    val showId: String? = "",
    val stationIdentifier: String? = "",
    val stationImage: String? = "",
    val showName: String? = "",
    val startTime: String? = "",
    val endTime: String? = "",
    val image: String? = "",
    val description: String? = "",
    val duration: Long = 0
)