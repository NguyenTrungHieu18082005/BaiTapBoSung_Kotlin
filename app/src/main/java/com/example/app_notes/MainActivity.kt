package com.example.app_notes

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: NoteAdapter
    private lateinit var db: NoteDatabase
    private var allNotes = mutableListOf<Note>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = NoteDatabase.getInstance(this)

        adapter = NoteAdapter(mutableListOf()) { note ->
            val intent = Intent(this, NoteDetailActivity::class.java).apply {
                putExtra("NOTE_ID", note.id)
                putExtra("NOTE_TITLE", note.title)
                putExtra("NOTE_CONTENT", note.content)
                putExtra("NOTE_CREATED_AT", note.createdAt)
                putExtra("NOTE_UPDATED_AT", note.updatedAt)
            }
            startActivity(intent)
        }

        findViewById<RecyclerView>(R.id.recyclerView).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }

        findViewById<FloatingActionButton>(R.id.fabAdd).setOnClickListener {
            startActivity(Intent(this, CreateNoteActivity::class.java))
        }

        findViewById<androidx.appcompat.widget.SearchView>(R.id.searchView)
            .setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?) = false
                override fun onQueryTextChange(newText: String?): Boolean {
                    val filtered = allNotes.filter {
                        it.title.contains(newText ?: "", ignoreCase = true) ||
                                it.content.contains(newText ?: "", ignoreCase = true)
                    }
                    adapter.updateList(filtered)
                    return true
                }
            })
    }

    override fun onResume() {
        super.onResume()
        loadNotes()
    }

    private fun loadNotes() {
        CoroutineScope(Dispatchers.IO).launch {
            allNotes = db.noteDao().getAll().toMutableList()
            withContext(Dispatchers.Main) {
                adapter.updateList(allNotes)
            }
        }
    }
}