package com.primedia.primedia_sample_app.room.entities

import androidx.room.Entity

@Entity(
    primaryKeys = ["mID", "pID"]
)
data class PlaylistMediaLinkEntity(
    val pID: Int,
    val mID: Int
)