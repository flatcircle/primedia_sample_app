package com.primedia.primedia_sample_app.room.repositories

import com.primedia.primedia_sample_app.room.AppDatabase
import com.primedia.primedia_sample_app.room.PlaylistWithMedia
import com.primedia.primedia_sample_app.room.entities.MediaEntity
import com.primedia.primedia_sample_app.room.entities.PlaylistMediaLinkEntity
import com.primedia.primedia_sample_app.util.ioThread

class PlaylistMediaLinkRepository(appDatabase: AppDatabase) {

    private val playlistMediaLinkDao = appDatabase.playlistMediaLinkDao()

    @Deprecated("Used in Room many-to-many relationships -- not deleting yet, but doesn't work")
    fun getMediaInPlaylist(successFunction: ((playlistWithMedia: List<PlaylistWithMedia>?) -> Unit)) {
        ioThread {
            successFunction(playlistMediaLinkDao.getPlaylistWithMediaItems())
        }
    }

    fun getMediaForPlaylist(uid: Int, successFunction: ((mediaInPlaylist: List<MediaEntity>?) -> Unit)) {
        ioThread {
            successFunction(playlistMediaLinkDao.getMediaForPlaylist(uid))
        }
    }

    fun insertPlaylistMediaLink(playlistMediaLinkEntity: PlaylistMediaLinkEntity) {
        ioThread {
            playlistMediaLinkDao.insert(playlistMediaLinkEntity)
        }
    }

    fun deleteAllPlaylistMediaLinks(successFunction: ((success: Boolean) -> Unit)) {
        ioThread {
            playlistMediaLinkDao.deleteAllPlaylistMediaLinks()
            successFunction(true)
        }
    }

    fun deleteAllPlaylist(playlistUid: Int, successFunction: ((success: Boolean) -> Unit)) {
        ioThread {
            playlistMediaLinkDao.deleteAllPlaylist(playlistUid)
            successFunction(true)
        }
    }

}