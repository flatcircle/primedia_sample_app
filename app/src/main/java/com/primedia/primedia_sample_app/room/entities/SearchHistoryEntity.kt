package com.primedia.primedia_sample_app.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.primedia.primedia_sample_app.models.StreamItemDataModelType

@Entity
data class SearchHistoryEntity(
    @PrimaryKey @ColumnInfo(name = "identifier") val identifier: String,
    @ColumnInfo(name = "type") val type: String? = StreamItemDataModelType.UNKNOWN.type,
    @ColumnInfo(name = "image_url") val imageUrl: String? = "",
    @ColumnInfo(name = "title") val title: String? = "",
    @ColumnInfo(name = "description") val description: String? = "",
    @ColumnInfo(name = "media") val media: String? = "",
    @ColumnInfo(name = "duration") val duration: Double? = 0.0,
    @ColumnInfo(name = "created_at") val created_at: Long? = 0)
