package com.primedia.primedia_sample_app.room

import com.primedia.primedia_sample_app.App
import com.primedia.primedia_sample_app.models.StreamItemDataModel
import com.primedia.primedia_sample_app.room.entities.PlaylistEntity
import com.primedia.primedia_sample_app.room.repositories.PlaylistMediaLinkRepository
import com.primedia.primedia_sample_app.room.repositories.PlaylistRepository
import com.primedia.primedia_sample_app.service.PreferenceService
import com.primedia.primedia_sample_app.util.ModelConversionUtils.RANDOM_GEN_PLAYLIST_ID
import org.kodein.di.generic.instance
import timber.log.Timber

class PlaylistDbHelper {

    private val playlistRepository: PlaylistRepository by App.kodein.instance<PlaylistRepository>()
    private val playlistMediaLinkRepository: PlaylistMediaLinkRepository by App.kodein.instance<PlaylistMediaLinkRepository>()
    val preferenceService: PreferenceService by App.kodein.instance<PreferenceService>()

    fun createNewPlaylist(isRandom: Boolean = false, playlist: StreamItemDataModel = StreamItemDataModel(), successFunction: ((success: Boolean, uid: Int) -> Unit)) {
        if (isRandom) playlistRepository.getRandomPlaylist {
            createNewRandomPlaylist(it) { uid ->
                successFunction(true, uid)
            }
        } else {
            playlist.identifier?.let { identifier ->
                playlistRepository.getPlaylistWithPlaylistId(identifier) {
                    createNewPodcastPlaylist(it, playlist) { success, uid ->
                        successFunction(success, uid)
                    }
                }
            } ?: kotlin.run {
                // TODO - error handling
                Timber.e("Cannot get Playlist when the identifier is null")
            }
        }

    }

    private fun createNewRandomPlaylist(dbPlaylist: PlaylistEntity?, successFunction: ((uid: Int) -> Unit)) {
// Playlist does not exist in db so create a new one
        if (dbPlaylist == null || dbPlaylist.playlistId.isEmpty()) {

            val nuPlaylist =
                PlaylistEntity(
                    playlistId = RANDOM_GEN_PLAYLIST_ID,
                    playlistName = "Random Generated Playlist",
                    playlistDescription = "",
                    currentPosition = 0
                )

            playlistRepository.insertPlaylist(nuPlaylist)
            playlistRepository.getRandomPlaylist {
                successFunction(it?.uid ?: 0)
            }
        } else {
            // If random playlist exists, clear it from the link table - as it has new randomly generated items each call
            playlistMediaLinkRepository.deleteAllPlaylist(dbPlaylist.uid) {
                playlistRepository.updatePlaylist(dbPlaylist.copy(currentPosition = 0))
                successFunction(dbPlaylist.uid)
            }

        }
    }

    private fun createNewPodcastPlaylist(dbPlaylist: PlaylistEntity?, playlist: StreamItemDataModel = StreamItemDataModel(), successFunction: ((success: Boolean, uid: Int) -> Unit)) {
// Playlist does not exist in db so create a new one
        if (playlist.identifier.isNullOrEmpty()) {
            successFunction(false, -1)
        } else {
            if (dbPlaylist == null || dbPlaylist.playlistId.isEmpty()) {

                val nuPlaylist =
                    PlaylistEntity(
                        playlistId = playlist.identifier,
                        playlistName = playlist.title ?: "",
                        playlistDescription = playlist.description ?: "",
                        playlistImageUrl = playlist.image_url ?: "",
                        currentPosition = 0
                    )

                playlistRepository.insertPlaylist(nuPlaylist)
                playlistRepository.getPlaylistWithPlaylistId(nuPlaylist.playlistId) {
                    successFunction(true, it?.uid ?: 0)
                }
            } else {
                val updatedPlaylist = PlaylistEntity(
                    uid = dbPlaylist.uid,
                    playlistId = playlist.identifier,
                    playlistName = playlist.title ?: "",
                    playlistDescription = playlist.description ?: "",
                    playlistImageUrl = playlist.image_url ?: "",
                    currentPosition = dbPlaylist.currentPosition
                )
                playlistRepository.updatePlaylist(updatedPlaylist)
                successFunction(true, updatedPlaylist.uid)
            }
        }
    }

    fun hasNext(successFunction: ((show: Boolean) -> Unit)) {
        playlistRepository.getPlaylistWithUid(preferenceService.currentPlaylistUid) { playlist ->
            playlistMediaLinkRepository.getMediaForPlaylist(preferenceService.currentPlaylistUid) { items ->
                if (playlist != null && !items.isNullOrEmpty()) successFunction(playlist.currentPosition < items.size)
            }
        }
    }

    fun hasPrevious(successFunction: ((show: Boolean) -> Unit)) {
        playlistRepository.getPlaylistWithUid(preferenceService.currentPlaylistUid) { playlist ->
            playlist?.let { successFunction(playlist.currentPosition > 0) }
        }
    }
}