package com.primedia.primedia_sample_app.util

import android.text.format.DateUtils
import com.primedia.primedia_sample_app.models.CuePointMetaDataBundle
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

object DateTimeUtils {

    private val mDateFormatBuffer = StringBuilder()

    private val simpleFormat: SimpleDateFormat by lazy {
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale("en"))
    }

    private val simpleFormatWithSeconds: SimpleDateFormat by lazy {
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    }

    private val simpleFormatNoSeconds: SimpleDateFormat by lazy {
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale("en"))
    }

    private val fullFormat: SimpleDateFormat by lazy {
        SimpleDateFormat("d MMMM yyy hh:mm aaa", Locale("en"))
    }

    private val dayMonthFormat: SimpleDateFormat by lazy {
        SimpleDateFormat("d MMM ''yy", Locale("en"))
    }

    private val simpleTimeFormat: SimpleDateFormat by lazy {
        SimpleDateFormat("HH:mm", Locale("en"))
    }

    private fun convertDateToLong(date: Date): Long {
        return date.time
    }

    private fun convertLongToDate(date: Long): Date {
        return Date(date)
    }

    fun convertLongToString(date: Long): String {
        return simpleFormat.format(date)
    }

    fun convertStringToDate(date: String): Date {
        return simpleFormatWithSeconds.parse(date)
    }

    fun convertStringToLong(date: String): Long {
        return if (date.isNotEmpty()) {
            convertDateToLong(convertStringToDate(date))
        } else 0
    }

    fun getFullDateFormattedFromString(date: String): String {
        return if (date.isEmpty()) {
            date
        } else {
            getFullDateFormatedFromLong(convertStringToDate(date).time)
        }
    }

    private fun getFullDateFormatedFromLong(timestamp: Long): String {
        return fullFormat.format(convertLongToDate(timestamp))
    }

    fun getTodaysDay(): String {
        return SimpleDateFormat("EEE", Locale("en")).format(convertLongToDate(System.currentTimeMillis()))
    }

    fun getDayMonthYearFormat(date: Date): String {
        return dayMonthFormat.format(date)
    }

    /**
     * PLAYER *
     */

    fun formatElapsedTime(timeLong: Long): String {
        return DateUtils.formatElapsedTime(mDateFormatBuffer, (timeLong / 1000L))
    }

    fun getMillisecondsIntoTrack(startTime: Long): Long {
        return System.currentTimeMillis() - startTime
    }

    fun isCuePointComplete(lastCuePointMetadata: CuePointMetaDataBundle?): Boolean {
        lastCuePointMetadata?.let { metadata ->
            return (metadata.startTime + metadata.durationInMillis) <= System.currentTimeMillis()
        } ?: run {
            return true // return true so that it will default back to station information
        }
    }

    fun getTimeFromDateString(date: String): String {
        val dateObj = convertStringToDate(date)
        return simpleTimeFormat.format(dateObj)
    }

    fun getSecondsInRoundedMinutes(millis: Double): Int {
        val mins = millis / (60.0)

        return if (mins < 1){
            1
        } else {
            mins.roundToInt()
        }
    }

}