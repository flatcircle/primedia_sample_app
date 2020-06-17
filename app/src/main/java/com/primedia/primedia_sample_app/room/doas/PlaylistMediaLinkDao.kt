package com.primedia.primedia_sample_app.room.doas

import androidx.room.*
import com.primedia.primedia_sample_app.room.MediaWithPlaylists
import com.primedia.primedia_sample_app.room.PlaylistWithMedia
import com.primedia.primedia_sample_app.room.entities.MediaEntity
import com.primedia.primedia_sample_app.room.entities.PlaylistEntity
import com.primedia.primedia_sample_app.room.entities.PlaylistMediaLinkEntity

@Dao
interface PlaylistMediaLinkDao {

    @Transaction
    @Query("SELECT * FROM PlaylistEntity")
    fun getPlaylistWithMediaItems(): List<PlaylistWithMedia>

    @Transaction
    @Query("SELECT * FROM MediaEntity")
    fun getMediaItemsWithPlaylists(): List<MediaWithPlaylists>

    @Query("SELECT * FROM PLAYLISTMEDIALINKENTITY")
    fun getAllPlaylistMediaLinks(): List<PlaylistMediaLinkEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(playlistMediaLinkEntity: PlaylistMediaLinkEntity)

    @Query("SELECT * FROM MediaEntity INNER JOIN PlaylistMediaLinkEntity ON MediaEntity.uid=PlaylistMediaLinkEntity.mID WHERE PlaylistMediaLinkEntity.pID=:uid ORDER BY MediaEntity.publishDate")
    fun getMediaForPlaylist(uid: Int): List<MediaEntity>

    @Query("SELECT * FROM PlaylistEntity INNER JOIN PlaylistMediaLinkEntity ON PlaylistEntity.uid=PlaylistMediaLinkEntity.pID WHERE PlaylistMediaLinkEntity.mID=:identifier")
    fun getPlaylistForMedia(identifier: String): List<PlaylistEntity>

    @Query("DELETE FROM PLAYLISTMEDIALINKENTITY")
    fun deleteAllPlaylistMediaLinks()

    @Query("DELETE FROM PLAYLISTMEDIALINKENTITY WHERE pID IS :playlistUid")
    fun deleteAllPlaylist(playlistUid: Int)

}