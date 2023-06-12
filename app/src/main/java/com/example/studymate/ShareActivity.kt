package com.example.studymate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat

class ShareActivity : AppCompatActivity() {

    private lateinit var receiverEmailAddress : EditText
    private lateinit var shareNow : Button
    private lateinit var cancel : Button
    private lateinit var sharedNote : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)

        window.statusBarColor = ContextCompat.getColor(this, R.color.mid_blue)

        receiverEmailAddress = findViewById(R.id.receiverEmailId)
        shareNow = findViewById(R.id.shareNow)
        cancel = findViewById(R.id.cancelShare)
        sharedNote = findViewById(R.id.sharedNoteName)

        shareNow.setOnClickListener {
            finish()
        }

        cancel.setOnClickListener {
            finish()
        }

    }
}