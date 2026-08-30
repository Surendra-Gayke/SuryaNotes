package com.surendra.suryanotes.ui.editor.model

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object HeadingStyleMapper {

    fun toSpanStyle(
        heading: EditorHeading
    ): SpanStyle =
        when (heading) {

            EditorHeading.Normal ->
                SpanStyle()

            EditorHeading.H1 ->
                SpanStyle(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )

            EditorHeading.H2 ->
                SpanStyle(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

            EditorHeading.H3 ->
                SpanStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

            EditorHeading.H4 ->
                SpanStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )

            EditorHeading.H5 ->
                SpanStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )

            EditorHeading.H6 ->
                SpanStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
        }

    fun toEditorHeading(
        spanStyle: SpanStyle
    ): EditorHeading =
        when (spanStyle.fontSize) {

            32.sp -> EditorHeading.H1

            28.sp -> EditorHeading.H2

            24.sp -> EditorHeading.H3

            20.sp -> EditorHeading.H4

            18.sp -> EditorHeading.H5

            16.sp -> EditorHeading.H6

            else -> EditorHeading.Normal
        }

    fun isHeading(
        spanStyle: SpanStyle
    ): Boolean =
        toEditorHeading(spanStyle) != EditorHeading.Normal

    fun isNormal(
        spanStyle: SpanStyle
    ): Boolean =
        toEditorHeading(spanStyle) == EditorHeading.Normal
}