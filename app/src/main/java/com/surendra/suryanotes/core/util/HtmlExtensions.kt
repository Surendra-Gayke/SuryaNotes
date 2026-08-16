package com.surendra.suryanotes.core.util

import androidx.core.text.HtmlCompat

fun String.toNotePreview(): String =
    HtmlCompat.fromHtml(
        this,
        HtmlCompat.FROM_HTML_MODE_COMPACT
    )
        .toString()
        .replace("\n\n", "\n")
        .trim()