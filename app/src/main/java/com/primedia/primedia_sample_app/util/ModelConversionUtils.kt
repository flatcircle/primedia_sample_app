package com.primedia.primedia_sample_app.util

import com.primedia.primedia_sample_app.models.StreamItemDataModel
import com.primedia.primedia_sample_app.models.StreamItemDataModelType
import com.primedia.primedia_sample_app.room.entities.MediaEntity
import com.primedia.primedia_sample_app.room.entities.PlaylistEntity
import com.primedia.primedia_sample_app.room.entities.SearchHistoryEntity
import com.primedia.primedia_sample_app.util.DateTimeUtils.convertLongToString
import com.primedia.primedia_sample_app.util.DateTimeUtils.convertStringToLong
import java.util.*

object ModelConversionUtils {

    const val RANDOM_GEN_PLAYLIST_ID = "RANDOM_GEN_PLAYLIST"

    fun lastPlayedToMediaEntity(streamItemDataModel: StreamItemDataModel, progress: Int, listenedToCompletion: Boolean): MediaEntity {
        return MediaEntity(
            identifier = streamItemDataModel.identifier ?: "",
            type = streamItemDataModel.type.type,
            imageUrl = streamItemDataModel.image_url ?: "",
            title = streamItemDataModel.title ?: "",
            description = streamItemDataModel.description ?: "",
            media = streamItemDataModel.media ?: "",
            duration = streamItemDataModel.duration,
            progress = progress,
            listenedToCompletion = listenedToCompletion,
            publishDate = streamItemDataModel.published_date?.let { convertStringToLong(it) } ?: kotlin.run { 0L }
        )
    }

    fun mediaEntityToStreamItemDataModel(mediaEntity: MediaEntity): StreamItemDataModel{
        return StreamItemDataModel(
            type = StreamItemDataModelType.create(mediaEntity.type),
            image_url = mediaEntity.imageUrl,
            identifier = mediaEntity.identifier,
            title = mediaEntity.title,
            description = mediaEntity.description,
            media = mediaEntity.media,
            duration = mediaEntity.duration,
            published_date = convertLongToString(mediaEntity.publishDate)
        )
    }

    fun playlistEntityToStreamItemDataModel(playlistEntity: PlaylistEntity): StreamItemDataModel{
        return StreamItemDataModel(
            type = StreamItemDataModelType.PODCAST,
            image_url = playlistEntity.playlistImageUrl,
            identifier = playlistEntity.playlistId,
            title = playlistEntity.playlistName,
            description = playlistEntity.playlistDescription
        )
    }

    fun convertSearchHistoryEntityListToStreamDataModel(searchHistoryList: List<SearchHistoryEntity>): List<StreamItemDataModel>{
        val streamDataList: MutableList<StreamItemDataModel> = mutableListOf()
        searchHistoryList.forEach {
            val type = if (it.type != null) StreamItemDataModelType.create(it.type.toLowerCase(Locale.getDefault())) else StreamItemDataModelType.UNKNOWN
            val imageUrl = it.imageUrl ?: ""
            val title = it.title ?: ""
            val description = it.description ?: ""
            val duration = it.duration ?: 0.0
            val streamModel = StreamItemDataModel(
                type = type ,
                image_url = imageUrl,
                identifier = it.identifier,
                title = title,
                description = description,
                media = it.media,
                duration = duration
            )
            streamDataList.add(streamModel)
        }
        return streamDataList
    }

    fun convertStreamItemDataModelToSearchHistoryEntity(streamItemDataModel: StreamItemDataModel): SearchHistoryEntity {
        val time = System.currentTimeMillis()
        return SearchHistoryEntity(
            identifier = streamItemDataModel.identifier ?: "",
            type = streamItemDataModel.type.name,
            imageUrl = streamItemDataModel.image_url,
            title = streamItemDataModel.title,
            description = streamItemDataModel.description,
            media = streamItemDataModel.media ?: "",
            duration = streamItemDataModel.duration,
            created_at = time
        )
    }

}