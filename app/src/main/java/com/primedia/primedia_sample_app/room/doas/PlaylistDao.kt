package com.primedia.primedia_sample_app.room.doas

import androidx.room.*
import com.primedia.primedia_sample_app.room.entities.PlaylistEntity

@Dao
interface PlaylistDao {

    @Query("SELECT * FROM PlaylistEntity WHERE playlistId IS 'RANDOM_GEN_PLAYLIST'")
    fun getRandomPlaylist(): PlaylistEntity?

    @Query("SELECT * FROM PlaylistEntity WHERE playlistId IS :playlistId")
    fun getPlaylistWithPlaylistId(playlistId: String): PlaylistEntity?

    @Query("SELECT * FROM PlaylistEntity WHERE uid IS :uid")
    fun getPlaylistWithUid(uid: Int): PlaylistEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(playlistEntity: PlaylistEntity)

    @Update
    fun update(playlistEntity: PlaylistEntity)

    @Query("DELETE FROM PLAYLISTENTITY")
    fun deleteAll()

}