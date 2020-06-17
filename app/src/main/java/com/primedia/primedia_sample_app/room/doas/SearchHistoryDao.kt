package com.primedia.primedia_sample_app.room.doas

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.primedia.primedia_sample_app.room.entities.SearchHistoryEntity

@Dao
interface SearchHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSearch(searchItem: SearchHistoryEntity)

    @Query("DELETE FROM SearchHistoryEntity")
    fun clearAllSearches()

    @Query("DELETE FROM SearchHistoryEntity WHERE identifier = :identifier")
    fun deleteRecentSearchItem(identifier: String)

    @Query("DELETE FROM SearchHistoryEntity WHERE identifier NOT IN (SELECT identifier FROM SearchHistoryEntity ORDER BY identifier DESC LIMIT 10)")
    fun removeOldSearchHistoryItems()

    @Query("SELECT * FROM SearchHistoryEntity ORDER BY created_at DESC LIMIT 10")
    fun getRecentSearches(): List<SearchHistoryEntity>


}