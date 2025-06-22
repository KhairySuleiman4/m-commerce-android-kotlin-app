package com.example.mcommerce.data.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
fun formatDateTime(isoString: String): Pair<String, String> {
    val dateTime = ZonedDateTime.parse(isoString)

    val dateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH)
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH)

    val formattedDate = dateTime.format(dateFormatter)
    val formattedTime = dateTime.format(timeFormatter)

    return Pair(formattedDate, formattedTime)
}
