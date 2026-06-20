package com.surendra.suryanotes

import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class NoteAdapter(
    private val onNoteClick: (Note) -> Unit,
    private val onNoteLongClick: (Note, Int) -> Unit
) : ListAdapter<Note, NoteAdapter.NoteViewHolder>(NoteDiffCallback()) {

    // Accent colors for cards
    private val accentColors = intArrayOf(
        0xFF5B5EA6.toInt(),
        0xFFE74C3C.toInt(),
        0xFF2ECC71.toInt(),
        0xFFF39C12.toInt(),
        0xFF9B59B6.toInt(),
        0xFF1ABC9C.toInt()
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = getItem(position)
        holder.bind(note, position, accentColors, onNoteClick, onNoteLongClick)
    }

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val noteTitle: TextView = itemView.findViewById(R.id.noteTitle)
        private val noteContent: TextView = itemView.findViewById(R.id.noteContent)
        private val noteDate: TextView = itemView.findViewById(R.id.noteDate)
        private val accentStripe: View = itemView.findViewById(R.id.accentStripe)
        private val boldIndicator: TextView = itemView.findViewById(R.id.boldIndicator)
        private val italicIndicator: TextView = itemView.findViewById(R.id.italicIndicator)
        private val hasBackgroundIcon: ImageView = itemView.findViewById(R.id.hasBackgroundIcon)

        fun bind(
            note: Note,
            position: Int,
            accentColors: IntArray,
            onNoteClick: (Note) -> Unit,
            onNoteLongClick: (Note, Int) -> Unit
        ) {
            // Title
            val title = note.title.ifBlank { "Untitled" }
            noteTitle.text = title

            // Content with formatting preview
            if (note.content.isNotBlank()) {
                val preview = note.contentPreview
                val spannable = SpannableString(preview)
                val style = when {
                    note.isBold && note.isItalic -> Typeface.BOLD_ITALIC
                    note.isBold -> Typeface.BOLD
                    note.isItalic -> Typeface.ITALIC
                    else -> Typeface.NORMAL
                }
                if (style != Typeface.NORMAL) {
                    spannable.setSpan(
                        StyleSpan(style), 0, spannable.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                noteContent.text = spannable
                noteContent.visibility = View.VISIBLE
            } else {
                noteContent.visibility = View.GONE
            }

            // Date
            noteDate.text = note.formattedDate

            // Accent color
            val colorIdx = note.colorIndex % accentColors.size
            accentStripe.setBackgroundColor(accentColors[colorIdx])

            // Format indicators
            boldIndicator.visibility = if (note.isBold) View.VISIBLE else View.GONE
            italicIndicator.visibility = if (note.isItalic) View.VISIBLE else View.GONE
            hasBackgroundIcon.visibility = if (note.hasBackground) View.VISIBLE else View.GONE

            // Click listeners
            itemView.setOnClickListener { onNoteClick(note) }
            itemView.setOnLongClickListener {
                onNoteLongClick(note, position)
                true
            }
        }
    }

    class NoteDiffCallback : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean =
            oldItem == newItem
    }
}