package com.primedia.primedia_sample_app.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlin.math.roundToInt

enum class StreamItemDataModelType(val type: String) {
    CLIP("clip"),
    STREAM("stream"),
    PODCAST("podcast"),
    SONG("song"),
    UNKNOWN("");

    companion object {
        fun create(type: String): StreamItemDataModelType {
            return values().singleOrNull { it.type == type } ?: UNKNOWN
        }
    }
}

enum class StreamHomeDataModelKey(val type: String) {
    TRENDING("trending"),
    RECENT("recent"),
    ONAIR("onair"),
    RADIO("radio"),
    PODCASTS("podcasts"),
    CATCHUP("catchup"),
    MUSIC("music"),
    UNKNOWN("");

    companion object {
        fun create(type: String): StreamHomeDataModelKey {
            return values().singleOrNull { it.type == type } ?: UNKNOWN
        }
    }
}

enum class StreamLayoutType(val type: Int) {
    LARGE(0),
    NORMAL(1),
    UNKNOWN(-1);

    companion object {
        fun create(type: Int): StreamLayoutType {
            return values().singleOrNull { it.type == type } ?: UNKNOWN
        }
    }
}

@Parcelize
data class StreamItemDataModelsParcelized(
    val data: List<StreamItemDataModel>
) : Parcelable


@Parcelize
data class StreamItemDataModel(
    val type: StreamItemDataModelType = StreamItemDataModelType.UNKNOWN,
    val image_url: String? = "",
    val identifier: String? = "",
    val title: String? = "",
    val description: String? = "",
    val media: String? = null,
    val duration: Double = 0.0, // seconds
    val artist: String? = "",
    val published_date: String? = "",
    val stationName: String? = "",
    val streamIdentifier: String? = ""
) : Parcelable {
    val durationInMillis: Int
        get() = duration.roundToInt() * 1000
}

data class StreamDataModel(
    val key: String?,
    val title: String?,
    val layout: StreamLayoutType?,
    val see_all: String?,
    val filter: Boolean = false,
    val data: List<StreamItemDataModel>?,
    val treat: Boolean = false
)
