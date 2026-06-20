package com.surendra.suryanotes

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import java.io.File
import java.io.FileOutputStream

object PdfExporter {

    private const val TAG = "PdfExporter"
    private const val PAGE_WIDTH = 595   // A4 width in points (72 DPI)
    private const val PAGE_HEIGHT = 842  // A4 height in points
    private const val MARGIN = 50
    private const val CONTENT_WIDTH = PAGE_WIDTH - (2 * MARGIN)

    fun exportNoteToPdf(context: Context, note: Note): File? {
        return try {
            val document = PdfDocument()
            var pageNumber = 1
            var pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
            var page = document.startPage(pageInfo)
            var canvas = page.canvas
            var yPosition = MARGIN

            // Load fonts
            val fontRegular = loadFont(context, R.font.segoeui)
            val fontBold = loadFont(context, R.font.segoeui_bold)
            val fontItalic = loadFont(context, R.font.segoeui_italic)
            val fontBoldItalic = loadFont(context, R.font.segoeui_bolditalic)

            // Draw background on first page
            if (note.hasBackground) {
                drawBackgroundImage(context, canvas, note)
            }

            // --- Title Paint ---
            val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(26, 26, 46)
                textSize = 22f
                typeface = fontBold ?: Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }

            // --- Date Paint ---
            val datePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(160, 160, 178)
                textSize = 9f
                typeface = fontRegular ?: Typeface.DEFAULT
            }

            // --- Line Paint ---
            val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(232, 232, 240)
                strokeWidth = 1f
            }

            // --- Content Paint ---
            val contentPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(60, 60, 74)
                textSize = 12f
                typeface = when {
                    note.isBold && note.isItalic -> fontBoldItalic
                        ?: Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC)
                    note.isBold -> fontBold
                        ?: Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    note.isItalic -> fontItalic
                        ?: Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
                    else -> fontRegular ?: Typeface.DEFAULT
                }
            }

            // Draw title
            if (note.title.isNotBlank()) {
                val titleLines = wrapText(note.title, titlePaint, CONTENT_WIDTH.toFloat())
                for (line in titleLines) {
                    yPosition += 26
                    canvas.drawText(line, MARGIN.toFloat(), yPosition.toFloat(), titlePaint)
                }
                yPosition += 12
            }

            // Draw date
            yPosition += 14
            canvas.drawText(note.formattedDate, MARGIN.toFloat(), yPosition.toFloat(), datePaint)
            yPosition += 14

            // Draw separator
            canvas.drawLine(
                MARGIN.toFloat(), yPosition.toFloat(),
                (PAGE_WIDTH - MARGIN).toFloat(), yPosition.toFloat(),
                linePaint
            )
            yPosition += 20

            // Draw content
            if (note.content.isNotBlank()) {
                val paragraphs = note.content.split("\n")
                val lineHeight = 18

                for (paragraph in paragraphs) {
                    if (paragraph.isBlank()) {
                        yPosition += lineHeight / 2
                        continue
                    }

                    val lines = wrapText(paragraph, contentPaint, CONTENT_WIDTH.toFloat())
                    for (line in lines) {
                        // Check if we need a new page
                        if (yPosition + lineHeight > PAGE_HEIGHT - MARGIN) {
                            document.finishPage(page)
                            pageNumber++
                            pageInfo = PdfDocument.PageInfo.Builder(
                                PAGE_WIDTH, PAGE_HEIGHT, pageNumber
                            ).create()
                            page = document.startPage(pageInfo)
                            canvas = page.canvas
                            yPosition = MARGIN

                            // Background on new page
                            if (note.hasBackground) {
                                drawBackgroundImage(context, canvas, note)
                            }
                        }

                        yPosition += lineHeight
                        canvas.drawText(
                            line, MARGIN.toFloat(),
                            yPosition.toFloat(), contentPaint
                        )
                    }
                    yPosition += 8 // paragraph spacing
                }
            }

            // Draw footer on last page
            val footerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(200, 200, 210)
                textSize = 8f
                typeface = fontItalic ?: Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            }
            canvas.drawText(
                "Created with Notecraft",
                MARGIN.toFloat(),
                (PAGE_HEIGHT - 20).toFloat(),
                footerPaint
            )

            document.finishPage(page)

            // Save file
            val outputDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
            )
            if (!outputDir.exists()) outputDir.mkdirs()

            val fileName = sanitizeFileName(note.title) + "_${System.currentTimeMillis()}.pdf"
            val pdfFile = File(outputDir, fileName)

            FileOutputStream(pdfFile).use { fos ->
                document.writeTo(fos)
            }
            document.close()

            Log.d(TAG, "PDF exported: ${pdfFile.absolutePath}")
            pdfFile

        } catch (e: Exception) {
            Log.e(TAG, "Error exporting PDF", e)
            null
        }
    }

    private fun drawBackgroundImage(context: Context, canvas: Canvas, note: Note) {
        try {
            val imageUri = Uri.parse(note.backgroundImagePath)
            val inputStream = context.contentResolver.openInputStream(imageUri) ?: return
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            if (bitmap != null) {
                val bgPaint = Paint().apply {
                    alpha = note.backgroundTransparency
                }

                val destRect = Rect(0, 0, PAGE_WIDTH, PAGE_HEIGHT)
                canvas.drawBitmap(bitmap, null, destRect, bgPaint)

                // White overlay for text readability
                val overlayPaint = Paint().apply {
                    val overlayAlpha = (255 - note.backgroundTransparency).coerceIn(0, 255)
                    color = Color.argb(overlayAlpha, 255, 255, 255)
                }
                canvas.drawRect(
                    0f, 0f,
                    PAGE_WIDTH.toFloat(), PAGE_HEIGHT.toFloat(),
                    overlayPaint
                )

                bitmap.recycle()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error drawing background in PDF", e)
        }
    }

    private fun wrapText(text: String, paint: Paint, maxWidth: Float): List<String> {
        if (text.isBlank()) return listOf("")

        val lines = mutableListOf<String>()
        val words = text.split(" ")
        val currentLine = StringBuilder()

        for (word in words) {
            val testLine = if (currentLine.isNotEmpty()) "$currentLine $word" else word
            val width = paint.measureText(testLine)

            if (width > maxWidth && currentLine.isNotEmpty()) {
                lines.add(currentLine.toString())
                currentLine.clear()
                currentLine.append(word)
            } else {
                currentLine.clear()
                currentLine.append(testLine)
            }
        }

        if (currentLine.isNotEmpty()) {
            lines.add(currentLine.toString())
        }

        return lines
    }

    private fun loadFont(context: Context, fontResId: Int): Typeface? {
        return try {
            ResourcesCompat.getFont(context, fontResId)
        } catch (e: Exception) {
            null
        }
    }

    private fun sanitizeFileName(name: String): String {
        if (name.isBlank()) return "untitled_note"
        return name.replace(Regex("[^a-zA-Z0-9._-]"), "_")
            .replace(Regex("_+"), "_")
            .take(50)
    }
}