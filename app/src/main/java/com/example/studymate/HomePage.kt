package com.example.studymate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class HomePage : AppCompatActivity() {

    private lateinit var menuToggle : ImageView
    private lateinit var menuBar : LinearLayout
    private lateinit var homeToggle : LinearLayout
    private lateinit var notesToggle : LinearLayout
    private lateinit var questionToggle : LinearLayout
    private lateinit var peerToggle : LinearLayout
    private lateinit var homeUserEmail : TextView
    private lateinit var homeUserId : TextView
    private lateinit var makeNote : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)
        val sessionId = intent.getStringExtra("sessionId")
        val userId = intent.getStringExtra("userId")
        val email = intent.getStringExtra("email")

        menuToggle = findViewById(R.id.menuToggle)
        menuBar = findViewById(R.id.menuBar)
        homeToggle = findViewById(R.id.homeToggle)
        notesToggle = findViewById(R.id.notesToggle)
        questionToggle = findViewById(R.id.questionToggle)
        peerToggle = findViewById(R.id.peerToggle)
        homeUserEmail = findViewById(R.id.homeUserEmail)
        homeUserId = findViewById(R.id.homeUserId)
        makeNote = findViewById(R.id.makeNote)

        homeUserEmail.text = email
        homeUserId.text = userId

        menuToggle.setOnClickListener {
            if (menuBar.visibility == View.VISIBLE) {
                menuBar.visibility = View.GONE
            } else {
                menuBar.visibility = View.VISIBLE
            }
        }

        makeNote.setOnClickListener {
            Toast.makeText(this, "Goto Make Notes Page", Toast.LENGTH_SHORT).show()
        }

    }
}