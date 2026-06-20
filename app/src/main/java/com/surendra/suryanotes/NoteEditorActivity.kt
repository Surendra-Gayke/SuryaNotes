package com.surendra.suryanotes

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import android.widget.ToggleButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import com.surendra.suryanotes.databinding.ActivityNoteEditorBinding
import java.io.File

class NoteEditorActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_NOTE_ID = "note_id"
    }

    private lateinit var binding: ActivityNoteEditorBinding
    private lateinit var noteDatabase: NoteDatabase
    private var currentNote = Note()
    private var isNewNote = true
    private var backgroundImageUri: String? = null
    private var currentTransparency = 180

    // Fonts
    private var fontRegular: Typeface? = null
    private var fontBold: Typeface? = null
    private var fontItalic: Typeface? = null
    private var fontBoldItalic: Typeface? = null

    // Image picker launcher
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { handleSelectedImage(it) }
    }

    // Permission launcher
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launchImagePicker()
        } else {
            Toast.makeText(this, R.string.permission_needed, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        noteDatabase = NoteDatabase(this)

        loadFonts()
        setupToolbar()
        setupFormattingButtons()
        setupBackgroundControls()
        loadNoteIfEditing()
    }

    // ─── Initialization ──────────────────────────────────────────

    private fun loadFonts() {
        try {
            fontRegular = ResourcesCompat.getFont(this, R.font.segoeui)
            fontBold = ResourcesCompat.getFont(this, R.font.segoeui_bold)
            fontItalic = ResourcesCompat.getFont(this, R.font.segoeui_italic)
            fontBoldItalic = ResourcesCompat.getFont(this, R.font.segoeui_bolditalic)
        } catch (e: Exception) {
            // Fonts not available; will use system defaults
        }

        // Apply initial fonts
        fontBold?.let { binding.editTitle.typeface = it }
        fontRegular?.let { binding.editContent.typeface = it }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = ""
        }
    }

    // ─── Text Formatting ────────────────────────────────────────

    private fun setupFormattingButtons() {
        binding.btnBold.setOnCheckedChangeListener { _, isChecked ->
            applyTextFormatting()
            updateToggleAppearance(binding.btnBold, isChecked)
        }

        binding.btnItalic.setOnCheckedChangeListener { _, isChecked ->
            applyTextFormatting()
            updateToggleAppearance(binding.btnItalic, isChecked)
        }
    }

    private fun updateToggleAppearance(button: ToggleButton, isActive: Boolean) {
        val color = if (isActive) R.color.format_active else R.color.format_inactive
        button.setTextColor(ContextCompat.getColor(this, color))
    }

    private fun applyTextFormatting() {
        val isBold = binding.btnBold.isChecked
        val isItalic = binding.btnItalic.isChecked

        // Save cursor position
        val selStart = binding.editContent.selectionStart
        val selEnd = binding.editContent.selectionEnd

        // Choose correct font variant
        val typeface: Typeface? = when {
            isBold && isItalic -> fontBoldItalic
            isBold -> fontBold
            isItalic -> fontItalic
            else -> fontRegular
        }

        if (typeface != null) {
            binding.editContent.typeface = typeface
        } else {
            // Fallback to style flags
            val style = when {
                isBold && isItalic -> Typeface.BOLD_ITALIC
                isBold -> Typeface.BOLD
                isItalic -> Typeface.ITALIC
                else -> Typeface.NORMAL
            }
            binding.editContent.setTypeface(binding.editContent.typeface, style)
        }

        // Restore cursor
        if (selStart >= 0 && selEnd >= 0) {
            binding.editContent.setSelection(
                selStart.coerceAtMost(binding.editContent.text?.length ?: 0),
                selEnd.coerceAtMost(binding.editContent.text?.length ?: 0)
            )
        }
    }

    // ─── Background Image ───────────────────────────────────────

    private fun setupBackgroundControls() {
        binding.btnBackgroundImage.setOnClickListener {
            pickBackgroundImage()
        }

        binding.btnRemoveBackground.setOnClickListener {
            removeBackground()
        }

        binding.transparencySeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                currentTransparency = progress
                val percentage = ((progress / 255f) * 100).toInt()
                binding.transparencyValue.text = "${percentage}%"
                updateBackgroundTransparency(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun pickBackgroundImage() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                if (ContextCompat.checkSelfPermission(
                        this, Manifest.permission.READ_MEDIA_IMAGES
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                } else {
                    launchImagePicker()
                }
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                // Scoped storage – no permission needed for picker
                launchImagePicker()
            }
            else -> {
                if (ContextCompat.checkSelfPermission(
                        this, Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                } else {
                    launchImagePicker()
                }
            }
        }
    }

    private fun launchImagePicker() {
        imagePickerLauncher.launch(arrayOf("image/*"))
    }

    private fun handleSelectedImage(uri: Uri) {
        // Take persistent permission
        try {
            contentResolver.takePersistableUriPermission(
                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        } catch (_: SecurityException) {
            // Some providers don't support persistent permissions
        }

        backgroundImageUri = uri.toString()
        displayBackgroundImage(uri)
    }

    private fun displayBackgroundImage(uri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (bitmap != null) {
                binding.backgroundImage.apply {
                    setImageBitmap(bitmap)
                    visibility = View.VISIBLE
                }
                binding.backgroundOverlay.visibility = View.VISIBLE
                binding.btnRemoveBackground.visibility = View.VISIBLE
                binding.transparencyLayout.visibility = View.VISIBLE

                binding.transparencySeekBar.progress = currentTransparency
                updateBackgroundTransparency(currentTransparency)
            }
        } catch (e: Exception) {
            Toast.makeText(this, R.string.image_load_failed, Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateBackgroundTransparency(alpha: Int) {
        // Image alpha: how visible the background is
        binding.backgroundImage.imageAlpha = alpha

        // Overlay alpha: inverse – ensures text stays readable
        val overlayAlpha = (255 - alpha).coerceIn(0, 255)
        binding.backgroundOverlay.setBackgroundColor(
            Color.argb(overlayAlpha, 255, 255, 255)
        )
    }

    private fun removeBackground() {
        backgroundImageUri = null
        binding.backgroundImage.apply {
            setImageDrawable(null)
            visibility = View.GONE
        }
        binding.backgroundOverlay.visibility = View.GONE
        binding.btnRemoveBackground.visibility = View.GONE
        binding.transparencyLayout.visibility = View.GONE
    }

    // ─── Load / Save / Delete ───────────────────────────────────

    private fun loadNoteIfEditing() {
        val noteId = intent.getStringExtra(EXTRA_NOTE_ID)

        if (noteId != null) {
            isNewNote = false
            val note = noteDatabase.getNoteById(noteId)

            if (note != null) {
                currentNote = note
                binding.editTitle.setText(note.title)
                binding.editContent.setText(note.content)

                // Restore formatting
                binding.btnBold.isChecked = note.isBold
                binding.btnItalic.isChecked = note.isItalic
                applyTextFormatting()

                // Restore background
                if (note.hasBackground) {
                    backgroundImageUri = note.backgroundImagePath
                    currentTransparency = note.backgroundTransparency
                    binding.transparencySeekBar.progress = currentTransparency

                    try {
                        val uri = Uri.parse(backgroundImageUri)
                        displayBackgroundImage(uri)
                    } catch (_: Exception) {
                        backgroundImageUri = null
                    }
                }

                supportActionBar?.title = getString(R.string.edit_note)
            }
        } else {
            currentNote = Note().apply {
                colorIndex = (0..5).random() // Random accent color
            }
            supportActionBar?.title = getString(R.string.new_note)
        }
    }

    private fun saveNote(): Boolean {
        val title = binding.editTitle.text.toString().trim()
        val content = binding.editContent.text.toString().trim()

        if (title.isEmpty() && content.isEmpty()) {
            Toast.makeText(this, R.string.note_empty, Toast.LENGTH_SHORT).show()
            return false
        }

        currentNote.apply {
            this.title = title
            this.content = content
            this.isBold = binding.btnBold.isChecked
            this.isItalic = binding.btnItalic.isChecked
            this.backgroundImagePath = backgroundImageUri
            this.backgroundTransparency = currentTransparency
        }

        noteDatabase.saveNote(currentNote)
        isNewNote = false

        Toast.makeText(this, R.string.note_saved, Toast.LENGTH_SHORT).show()
        return true
    }

    private fun deleteNote() {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_note_title)
            .setMessage(R.string.delete_confirm)
            .setPositiveButton(R.string.delete) { _, _ ->
                if (!isNewNote) {
                    noteDatabase.deleteNote(currentNote.id)
                }
                Toast.makeText(this, R.string.note_deleted, Toast.LENGTH_SHORT).show()
                finish()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    // ─── PDF Export ─────────────────────────────────────────────

    private fun exportToPdf() {
        if (!saveNote()) return

        val pdfFile = PdfExporter.exportNoteToPdf(this, currentNote)

        if (pdfFile != null && pdfFile.exists()) {
            showPdfExportedDialog(pdfFile)
        } else {
            Toast.makeText(this, R.string.pdf_export_failed, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showPdfExportedDialog(pdfFile: File) {
        AlertDialog.Builder(this)
            .setTitle(R.string.pdf_exported)
            .setMessage("${getString(R.string.pdf_saved_msg)}\n\n${pdfFile.name}")
            .setPositiveButton(R.string.share) { _, _ ->
                sharePdf(pdfFile)
            }
            .setNeutralButton(R.string.ok, null)
            .show()
    }

    private fun sharePdf(pdfFile: File) {
        try {
            val uri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                pdfFile
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_pdf)))
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to share PDF", Toast.LENGTH_SHORT).show()
        }
    }

    // ─── Menu ───────────────────────────────────────────────────

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_editor, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            R.id.action_save -> {
                saveNote()
                true
            }
            R.id.action_export_pdf -> {
                exportToPdf()
                true
            }
            R.id.action_delete -> {
                deleteNote()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        // Auto-save on back
        val title = binding.editTitle.text.toString().trim()
        val content = binding.editContent.text.toString().trim()

        if (title.isNotEmpty() || content.isNotEmpty()) {
            saveNote()
        }

        super.onBackPressed()
    }
}