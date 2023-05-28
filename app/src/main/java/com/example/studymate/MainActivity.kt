package com.example.studymate

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.appwrite.Client
import io.appwrite.services.Account
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch



class MainActivity : AppCompatActivity() {

    private lateinit var textTagline : TextView
    private lateinit var signUp : TextView
    private lateinit var emailAddress : EditText
    private lateinit var password : EditText
    private lateinit var login : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textTagline = findViewById(R.id.textTagline)
        signUp = findViewById(R.id.signUpHere)
        emailAddress = findViewById(R.id.editTextUsername)
        password = findViewById(R.id.editTextPassword)
        login = findViewById(R.id.login)

        signUp.setOnClickListener {
            startActivity(Intent(this@MainActivity, SignUp::class.java))
            finish()
        }

        login.setOnClickListener {
            val userEmail = emailAddress.text.toString()
            val userPwd = password.text.toString()
            if(userEmail.isEmpty() || userPwd.isEmpty())
                Toast.makeText(this, "Enter the Email address and password", Toast.LENGTH_SHORT).show()
            else if(!isValidEmail(userEmail))
                Toast.makeText(this, "Enter a valid Email address", Toast.LENGTH_SHORT).show()
            else {
                // Fetch data from database, and compare email address and password
                checkUser(userEmail, userPwd)
            }
        }
        animateTagline()
    }

    private fun isValidEmail(target: String?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }
    private fun animateTagline() {
        val tagline = "Seamless Notemaking\nLearning and\nSharing"
        val delayMillis = 90 // Delay between each character (adjust as needed)
        val handler = Handler()
        val taglineLength = tagline.length
        val currentIndex = intArrayOf(0) // Use an array to hold the current index (to make it effectively final)
        val runnable: Runnable = object : Runnable {
            override fun run() {
                if (currentIndex[0] < taglineLength) {
                    textTagline.text = tagline.substring(0, currentIndex[0] + 1)
                    currentIndex[0]++
                } else {
                    currentIndex[0] = 0 // Reset the index to 0 to start from the beginning
                }
                handler.postDelayed(this, delayMillis.toLong())
            }
        }
        handler.postDelayed(runnable, delayMillis.toLong())
    }
    private fun checkUser(userEmail: String, userPwd: String) {
        val client = Client(this)
            .setEndpoint("https://cloud.appwrite.io/v1")
            .setProject("64734c27ee025a6ee21c")

        val account = Account(client)

        GlobalScope.launch(Dispatchers.Main) {
            try {
                val response = account.createEmailSession(
                    email = userEmail,
                    password = userPwd,
                )
                // Handle successful login
                val intent = Intent(this@MainActivity, HomePage::class.java)
                intent.putExtra("sessionId", response.id)
                intent.putExtra("userId", response.userId)
                intent.putExtra("email", response.providerUid)
                startActivity(intent)
                finish()

                Toast.makeText(this@MainActivity, "Login successful", Toast.LENGTH_SHORT).show()
                // Further flow for the logged-in user
            } catch (e: Exception) {
                // Handle login failure
                Toast.makeText(this@MainActivity, "Login failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = this!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(this!!.currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }


}

