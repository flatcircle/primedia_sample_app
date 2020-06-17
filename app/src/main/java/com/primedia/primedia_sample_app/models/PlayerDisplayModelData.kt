package com.primedia.primedia_sample_app.models

import com.primedia.primedia_sample_app.util.DateTimeUtils


data class PlayerDisplayModelData(
    val currentStreamDetails: StreamItemDataModel? = null,
    val lastCuePointDetails: CuePointMetaDataBundle? = null,
    val currentStationShowDetails: StationShow? = null,
    val seekbarInfo: SeekbarInfo = SeekbarInfo(),
    val historicalProgress: Int = 0,
    val listenedToCompletion: Boolean = false
) {
    fun getDisplayInfo(): PlayerDisplayInfo {

        return when {
            showCuePointData() -> {

                // excuse !! - checked if not null in method above
                PlayerDisplayInfo(
                    currentStreamDetails?.type ?: StreamItemDataModelType.UNKNOWN,
                    lastCuePointDetails!!.title,
                    if (lastCuePointDetails.type == "ad") "Advertisement" else lastCuePointDetails.artist,
                    lastCuePointDetails.trackImageUrl,
                    lastCuePointDetails.startTime,
                    0L,
                    lastCuePointDetails.timeDuration,
                    seekbarInfo,
                    currentStreamDetails?.published_date ?: ""
                )
            }
            showCurrentShowData() -> {
                // excuse !! - checked if not null in method above
                PlayerDisplayInfo(
                    currentStreamDetails?.type ?: StreamItemDataModelType.UNKNOWN,
                    currentStationShowDetails?.showName ?: "",
                    currentStreamDetails?.stationName ?: "",
                    currentStationShowDetails?.image ?: "",
                    currentStationShowDetails?.startTime?.let { DateTimeUtils.convertStringToDate(it).time} ?: kotlin.run { 0L },
                    currentStationShowDetails?.endTime?.let{ DateTimeUtils.convertStringToDate(it).time} ?: kotlin.run { 0L },
                    currentStationShowDetails?.duration?.let {it.toInt()} ?: kotlin.run { 0 },
                    seekbarInfo,
                    currentStreamDetails?.published_date ?: ""
                )
            }
            else -> {
                PlayerDisplayInfo(
                    currentStreamDetails?.type ?: StreamItemDataModelType.UNKNOWN,
                    currentStreamDetails?.title ?: "",
                    "",
                    currentStreamDetails?.image_url ?: "",
                    0L,
                    0L,
                    0,
                    seekbarInfo,
                    currentStreamDetails?.published_date ?: ""
                )
            }
        }
    }

    private fun showCuePointData(): Boolean {
        lastCuePointDetails?.let { cuePoint ->
            return !cuePoint.isCuePointComplete && currentStreamDetails?.type == StreamItemDataModelType.STREAM
        } ?: kotlin.run { return false }
    }

    private fun showCurrentShowData(): Boolean {
        return currentStationShowDetails != null && currentStreamDetails?.type == StreamItemDataModelType.STREAM
    }
}

data class PlayerDisplayInfo(
    val streamType: StreamItemDataModelType = StreamItemDataModelType.UNKNOWN,
    val title: String = "",
    val subtitle: String = "",
    val image: String = "",
    val startTime: Long = 0L,
    val endTime: Long = 0L,
    val duration: Int = 0,
    val seekbarInfo: SeekbarInfo,
    val publishDate: String
)

data class SeekbarInfo(
    val playerPosition: Int = 0,
    val playerDuration: Int = 0
)