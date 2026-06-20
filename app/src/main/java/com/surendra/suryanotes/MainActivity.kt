package com.surendra.suryanotes

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.surendra.suryanotes.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var noteDatabase: NoteDatabase
    private lateinit var noteAdapter: NoteAdapter

    // Activity result launcher for editor
    private val editorLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        loadNotes()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        noteDatabase = NoteDatabase(this)

        setupToolbar()
        setupRecyclerView()
        setupSearch()
        setupFab()
    }

    override fun onResume() {
        super.onResume()
        loadNotes()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
    }

    private fun setupRecyclerView() {
        noteAdapter = NoteAdapter(
            onNoteClick = { note -> openNoteEditor(note) },
            onNoteLongClick = { note, _ -> showDeleteDialog(note) }
        )

        binding.notesRecyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = noteAdapter
            setHasFixedSize(false)
        }
    }

    private fun setupSearch() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchNotes(s?.toString().orEmpty())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupFab() {
        binding.fabAddNote.setOnClickListener {
            val intent = Intent(this, NoteEditorActivity::class.java)
            editorLauncher.launch(intent)
        }
    }

    private fun loadNotes() {
        val query = binding.searchEditText.text?.toString().orEmpty()
        val notes = if (query.isBlank()) {
            noteDatabase.getAllNotes()
        } else {
            noteDatabase.searchNotes(query)
        }
        noteAdapter.submitList(notes)
        updateEmptyState(notes.isEmpty())
    }

    private fun searchNotes(query: String) {
        val notes = noteDatabase.searchNotes(query)
        noteAdapter.submitList(notes)
        updateEmptyState(notes.isEmpty())
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        binding.emptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.notesRecyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun openNoteEditor(note: Note) {
        val intent = Intent(this, NoteEditorActivity::class.java).apply {
            putExtra(NoteEditorActivity.EXTRA_NOTE_ID, note.id)
        }
        editorLauncher.launch(intent)
    }

    private fun showDeleteDialog(note: Note) {
        val title = note.title.ifBlank { "Untitled" }
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_note_title)
            .setMessage("Delete \"$title\"?")
            .setPositiveButton(R.string.delete) { _, _ ->
                noteDatabase.deleteNote(note.id)
                loadNotes()
                Toast.makeText(this, R.string.note_deleted, Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
}

