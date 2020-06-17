package com.primedia.primedia_sample_app.room.doas

import androidx.lifecycle.LiveData
import androidx.room.*
import com.primedia.primedia_sample_app.room.entities.MediaEntity

@Dao
interface MediaDao {

    @Query("SELECT * FROM MediaEntity")
    fun getAll(): LiveData<List<MediaEntity>>

    //todo -- refactor last played
//    @Query("SELECT * FROM PlayHistoryEntity ORDER BY lastPlayed DESC LIMIT 1")
//    fun getLastPlayed(): PlayHistoryEntity

    @Query("SELECT * FROM MediaEntity WHERE identifier IS :identifier")
    fun getPlayEntityWithId(identifier: String): MediaEntity?

    @Query("SELECT * FROM MediaEntity WHERE identifier IN (:identifiers)")
    fun getPlaylistEntitiesWithIds(identifiers: List<String>): List<MediaEntity>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(mediaEntity: MediaEntity)

    @Query("DELETE FROM MEDIAENTITY")
    fun deleteAll()

    @Update
    fun update(mediaEntity: MediaEntity)


}