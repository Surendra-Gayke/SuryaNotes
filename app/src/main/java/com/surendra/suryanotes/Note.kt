package com.surendra.suryanotes

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class Note(
    var id: String = UUID.randomUUID().toString(),
    var title: String = "",
    var content: String = "",
    var createdAt: Long = System.currentTimeMillis(),
    var updatedAt: Long = System.currentTimeMillis(),
    var isBold: Boolean = false,
    var isItalic: Boolean = false,
    var backgroundImagePath: String? = null,
    var backgroundTransparency: Int = 180,  // 0 - 255
    var colorIndex: Int = 0                 // accent color index
) : Serializable {

    val formattedDate: String
        get() {
            val sdf = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault())
            return sdf.format(Date(updatedAt))
        }

    val hasBackground: Boolean
        get() = !backgroundImagePath.isNullOrBlank()

    val isEmpty: Boolean
        get() = title.isBlank() && content.isBlank()

    val contentPreview: String
        get() = content.take(200).replace("\n", " ")
}