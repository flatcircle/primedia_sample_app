package com.primedia.primedia_sample_app.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.primedia.primedia_sample_app.util.ModelConversionUtils.RANDOM_GEN_PLAYLIST_ID

@Entity
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    val playlistId: String,
    val playlistName: String,
    val playlistDescription: String = "",
    val playlistImageUrl: String = "",
    val currentPosition: Int = 0
) {
    val isRandomPlaylist: Boolean
    get() = playlistId == RANDOM_GEN_PLAYLIST_ID
}