package com.primedia.primedia_sample_app.room

import com.primedia.primedia_sample_app.App
import com.primedia.primedia_sample_app.models.StreamItemDataModel
import com.primedia.primedia_sample_app.room.entities.MediaEntity
import com.primedia.primedia_sample_app.room.repositories.MediaRepository
import com.primedia.primedia_sample_app.util.ModelConversionUtils.lastPlayedToMediaEntity
import org.kodein.di.generic.instance

class MediaDbHelper {

    private val mediaRepository: MediaRepository by App.kodein.instance<MediaRepository>()

    fun insertOrUpdateMediaItem(mediaItem: StreamItemDataModel, successFunction: ((success: Boolean, media: MediaEntity?) -> Unit)) {
        mediaItem.identifier?.let { identifier ->
            // add all random gen media items to Media table
            mediaRepository.getPlayEntityWithId(identifier) {
                it?.let {
                    val mediaEntity = lastPlayedToMediaEntity(mediaItem, it.progress, it.listenedToCompletion)
                    mediaRepository.update(mediaEntity, it.uid)
                    successFunction(true, mediaEntity)
                } ?: kotlin.run {
                    val mediaEntity = lastPlayedToMediaEntity(mediaItem, 0, false)
                    mediaRepository.insertMediaItem(mediaEntity)
                    successFunction(true, mediaEntity)
                }
            }
        } ?: kotlin.run { successFunction(false, null) }
    }

    fun updateMediaItem(mediaItem: MediaEntity, successFunction: ((success: Boolean) -> Unit)){
        mediaRepository.getPlayEntityWithId(mediaItem.identifier) {
            it?.let {
                mediaRepository.update(mediaItem, it.uid)
            }
            successFunction(true)
        }
    }

}