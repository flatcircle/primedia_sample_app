package com.primedia.primedia_sample_app.room

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.primedia.primedia_sample_app.room.entities.MediaEntity
import com.primedia.primedia_sample_app.room.entities.PlaylistEntity
import com.primedia.primedia_sample_app.room.entities.PlaylistMediaLinkEntity

class PlaylistWithMedia {
    @Embedded
    lateinit var playlist: PlaylistEntity

    @Relation(
        parentColumn = "uid",
        entityColumn = "uid",
        associateBy = Junction(PlaylistMediaLinkEntity::class)
    )
    lateinit var mediaEntities: List<MediaEntity>
}

class MediaWithPlaylists {
    @Embedded
    lateinit var mediaEntity: MediaEntity

    @Relation(
        parentColumn = "uid",
        entityColumn = "uid",
        associateBy = Junction(PlaylistMediaLinkEntity::class)
    )
    lateinit var playlistEntities: List<PlaylistEntity>
}
