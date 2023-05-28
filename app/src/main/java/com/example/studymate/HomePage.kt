package com.example.studymate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class HomePage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)
        val sessionId = intent.getStringExtra("sessionId")
        val userId = intent.getStringExtra("userId")
        val email = intent.getStringExtra("email")
        Log.d("passsed", "${sessionId}${userId}${email}")
    }
}