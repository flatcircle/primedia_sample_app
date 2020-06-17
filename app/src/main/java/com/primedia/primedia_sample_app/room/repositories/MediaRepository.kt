package com.primedia.primedia_sample_app.room.repositories

import com.primedia.primedia_sample_app.room.AppDatabase
import com.primedia.primedia_sample_app.room.entities.MediaEntity
import com.primedia.primedia_sample_app.util.ioThread


class MediaRepository(appDatabase: AppDatabase) {
    private val mediaDao = appDatabase.mediaDao()

    fun getPlayEntityWithId(identifier: String, successFunction: ((mediaEntry: MediaEntity?) -> Unit)) {
        ioThread {
            successFunction(mediaDao.getPlayEntityWithId(identifier))
        }
    }

    fun getPlaylistEntitiesWithIds(identifiers: List<String>, successFunction: ((mediaEntries: List<MediaEntity>?) -> Unit)){
        ioThread {
            successFunction(mediaDao.getPlaylistEntitiesWithIds(identifiers))
        }
    }

    fun insertMediaItem(playItem: MediaEntity) {
        ioThread {
            mediaDao.insert(playItem)
        }
    }

    fun clearPlayHistory(successFunction: ((success: Boolean) -> Unit)) {
        ioThread {
            mediaDao.deleteAll()
            successFunction(true)
        }
    }

    fun update(mediaEntry: MediaEntity, uid: Int?) {
        ioThread {
            uid?.let { mediaDao.update(mediaEntry.copy(uid = it)) }
        }
    }

}
