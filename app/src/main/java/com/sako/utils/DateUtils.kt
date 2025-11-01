package com.sako.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    fun formatDate(dateString: String, outputFormat: String = Constants.DATE_FORMAT_DISPLAY): String {
        return try {
            val inputFormat = SimpleDateFormat(Constants.DATE_FORMAT_API, Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")

            val date = inputFormat.parse(dateString)
            val outputFormatter = SimpleDateFormat(outputFormat, Locale.getDefault())

            date?.let { outputFormatter.format(it) } ?: dateString
        } catch (e: Exception) {
            dateString
        }
    }

    fun getCurrentTimestamp(): String {
        val formatter = SimpleDateFormat(Constants.DATE_FORMAT_API, Locale.getDefault())
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        return formatter.format(Date())
    }

    fun getTimeAgo(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat(Constants.DATE_FORMAT_API, Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")

            val date = inputFormat.parse(dateString)
            val now = Date()

            date?.let {
                val diff = now.time - it.time
                val seconds = diff / 1000
                val minutes = seconds / 60
                val hours = minutes / 60
                val days = hours / 24
                val weeks = days / 7
                val months = days / 30
                val years = days / 365

                when {
                    years > 0 -> "$years tahun lalu"
                    months > 0 -> "$months bulan lalu"
                    weeks > 0 -> "$weeks minggu lalu"
                    days > 0 -> "$days hari lalu"
                    hours > 0 -> "$hours jam lalu"
                    minutes > 0 -> "$minutes menit lalu"
                    else -> "Baru saja"
                }
            } ?: dateString
        } catch (e: Exception) {
            dateString
        }
    }
}