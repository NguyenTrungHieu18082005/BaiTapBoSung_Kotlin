package com.example.app_notes

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class CreateNoteActivity : AppCompatActivity() {

    private lateinit var db: NoteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_note)

        db = NoteDatabase.getInstance(this)

        val sdf = SimpleDateFormat("HH:mm, dd/MM/yyyy", Locale.getDefault())
        findViewById<TextView>(R.id.tvDate).text = "Hôm nay, ${sdf.format(Date())}"

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        findViewById<Button>(R.id.btnSave).setOnClickListener {
            val etTitle = findViewById<EditText>(R.id.etTitle)
            val title = etTitle.text.toString().trim()
            val content = findViewById<EditText>(R.id.etContent).text.toString().trim()

            // Validate
            if (title.isEmpty()) {
                etTitle.error = "Tiêu đề không được để trống"
                return@setOnClickListener
            }
            if (title.length < 3) {
                etTitle.error = "Tiêu đề phải có ít nhất 3 ký tự"
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                val now = System.currentTimeMillis()
                db.noteDao().insert(
                    Note(
                        title = title,
                        content = content,
                        createdAt = now,
                        updatedAt = now
                    )
                )
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CreateNoteActivity, "Đã lưu!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}