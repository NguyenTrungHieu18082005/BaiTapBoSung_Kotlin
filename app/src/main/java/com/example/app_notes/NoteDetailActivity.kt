package com.example.app_notes

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class NoteDetailActivity : AppCompatActivity() {

    private lateinit var db: NoteDatabase
    private val sdf = SimpleDateFormat("HH:mm, dd/MM/yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_detail)

        db = NoteDatabase.getInstance(this)

        val noteId   = intent.getIntExtra("NOTE_ID", 0)
        val title    = intent.getStringExtra("NOTE_TITLE") ?: ""
        val content  = intent.getStringExtra("NOTE_CONTENT") ?: ""
        val createdAt = intent.getLongExtra("NOTE_CREATED_AT", System.currentTimeMillis())
        val updatedAt = intent.getLongExtra("NOTE_UPDATED_AT", System.currentTimeMillis())

        val etTitle   = findViewById<EditText>(R.id.etTitle)
        val etContent = findViewById<EditText>(R.id.etContent)

        etTitle.setText(title)
        etContent.setText(content)

        // Hiển thị thời gian
        findViewById<TextView>(R.id.tvTimestamp).text =
            "Đã cập nhật: ${sdf.format(Date(updatedAt))}"

        findViewById<TextView>(R.id.tvCreatedAt).text =
            "Ngày khởi tạo: ${sdf.format(Date(createdAt))}"

        // Back
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }


        findViewById<Button>(R.id.btnSave).setOnClickListener {
            val newTitle = etTitle.text.toString().trim()
            val newContent = etContent.text.toString().trim()

            if (newTitle.isEmpty()) {
                etTitle.error = "Tiêu đề không được để trống"
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                db.noteDao().update(
                    Note(
                        id = noteId,
                        title = newTitle,
                        content = newContent,
                        createdAt = createdAt,
                        updatedAt = System.currentTimeMillis() // thời gian lưu mới nhất
                    )
                )
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@NoteDetailActivity, "Đã cập nhật!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}