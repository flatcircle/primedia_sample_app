package com.primedia.primedia_sample_app.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.primedia.primedia_sample_app.models.StreamItemDataModelType

@Entity
data class MediaEntity(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    val identifier: String,
    val type: String = StreamItemDataModelType.UNKNOWN.type,
    val imageUrl: String,
    val title: String,
    val description: String, // seconds
    val media: String,
    val duration: Double,
    val progress: Int, // milliseconds
    val listenedToCompletion: Boolean,
    val publishDate: Long
)