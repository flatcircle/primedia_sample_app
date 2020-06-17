package com.primedia.primedia_sample_app.room.repositories

import com.primedia.primedia_sample_app.room.AppDatabase
import com.primedia.primedia_sample_app.room.entities.PlaylistEntity
import com.primedia.primedia_sample_app.util.ioThread


class PlaylistRepository(appDatabase: AppDatabase) {

    private val playlistDao = appDatabase.playlistDao()

    fun getRandomPlaylist(successFunction: ((playlist: PlaylistEntity?) -> Unit)) {
        ioThread {
            successFunction(playlistDao.getRandomPlaylist())
        }
    }

    fun getPlaylistWithPlaylistId(playlistId: String, successFunction: ((playlist: PlaylistEntity?) -> Unit)) {
        ioThread {
            successFunction(playlistDao.getPlaylistWithPlaylistId(playlistId))
        }
    }

    fun getPlaylistWithUid(uid: Int, successFunction: ((playlist: PlaylistEntity?) -> Unit)) {
        ioThread {
            successFunction(playlistDao.getPlaylistWithUid(uid))
        }
    }

    fun insertPlaylist(playlistEntity: PlaylistEntity) {
        ioThread {
            playlistDao.insert(playlistEntity)
        }
    }

    fun updatePlaylist(playlistEntity: PlaylistEntity) {
        ioThread {
            playlistDao.update(playlistEntity)
        }
    }

    fun clearAllPlaylist(successFunction: ((success: Boolean) -> Unit)) {
        ioThread {
            playlistDao.deleteAll()
            successFunction(true)
        }
    }

}