package com.primedia.primedia_sample_app.models

data class StationLineUp(
    val stationIdentifier: String?,
    val lineupForDay: List<StationShow>?
)