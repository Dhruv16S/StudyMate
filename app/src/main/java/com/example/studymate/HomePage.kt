package com.example.studymate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

class HomePage : AppCompatActivity() {

    private lateinit var menuToggle : ImageView
    private lateinit var menuBar : ConstraintLayout
    private lateinit var homeToggle : LinearLayout
    private lateinit var notesToggle : LinearLayout
    private lateinit var questionToggle : LinearLayout
    private lateinit var homeUserEmail : TextView
    private lateinit var homeUserId : TextView
    private lateinit var frame : FrameLayout

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
        homeUserEmail = findViewById(R.id.homeUserEmail)
        homeUserId = findViewById(R.id.homeUserId)
        frame = findViewById(R.id.frame)

        homeUserEmail.text = email
        homeUserId.text = userId

        val fragmentManager : FragmentManager = supportFragmentManager
        var fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.frame, HomeFragment())
        fragmentTransaction.commit()

        homeToggle.setOnClickListener {
            menuBar.visibility = View.GONE
            var fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
            frame.removeAllViews()
            fragmentTransaction.add(R.id.frame, HomeFragment())
            fragmentTransaction.commit()
        }

        notesToggle.setOnClickListener {
            menuBar.visibility = View.GONE
            var fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
            frame.removeAllViews()
            fragmentTransaction.add(R.id.frame, NotesFragment())
            fragmentTransaction.commit()
        }

        questionToggle.setOnClickListener {
            menuBar.visibility = View.GONE
            var fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
            frame.removeAllViews()
            fragmentTransaction.add(R.id.frame, QCardsFragment())
            fragmentTransaction.commit()
        }

        menuToggle.setOnClickListener {
            if (menuBar.visibility == View.VISIBLE) {
                menuBar.visibility = View.GONE
            } else {
                menuBar.visibility = View.VISIBLE
            }
        }
    }
}