package com.primedia.primedia_sample_app.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.primedia.primedia_sample_app.room.doas.MediaDao
import com.primedia.primedia_sample_app.room.doas.PlaylistDao
import com.primedia.primedia_sample_app.room.doas.PlaylistMediaLinkDao
import com.primedia.primedia_sample_app.room.entities.MediaEntity
import com.primedia.primedia_sample_app.room.entities.PlaylistEntity
import com.primedia.primedia_sample_app.room.entities.PlaylistMediaLinkEntity

@Database(
    entities = [MediaEntity::class, PlaylistEntity::class, PlaylistMediaLinkEntity::class], version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mediaDao(): MediaDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun playlistMediaLinkDao(): PlaylistMediaLinkDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = buildDatabase(context)
                    }
                }
            }
            return INSTANCE
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "app_database"
                )
                .build()
        }
    }
}