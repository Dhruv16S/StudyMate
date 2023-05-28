package com.example.studymate

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import io.appwrite.Client
import io.appwrite.services.Account
import io.appwrite.ID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SignUp : AppCompatActivity() {

    private lateinit var textTagline : TextView
    private lateinit var emailAddress : EditText
    private lateinit var password : EditText
    private lateinit var confirmPassword : EditText
    private lateinit var signUp : Button
    private lateinit var gotoLogin : LinearLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        textTagline = findViewById(R.id.signUptextTagline)
        emailAddress = findViewById(R.id.signUpeditTextUsername)
        password = findViewById(R.id.signUpeditTextPassword)
        confirmPassword = findViewById(R.id.signUpeditTextConfirmPassword)
        gotoLogin = findViewById(R.id.gotoLogin)
        signUp = findViewById(R.id.signUp)

        gotoLogin.setOnClickListener {
            startActivity(Intent(this@SignUp, MainActivity::class.java))
            finish()
        }

        signUp.setOnClickListener {
            val userEmail = emailAddress.text.toString()
            val userPwd = password.text.toString()
            val userCPwd = confirmPassword.text.toString()
            if(userEmail.isEmpty() || userPwd.isEmpty())
                Toast.makeText(this, "Enter the Email address and password", Toast.LENGTH_SHORT).show()
            else if(!isValidEmail(userEmail))
                Toast.makeText(this, "Enter a valid Email address", Toast.LENGTH_SHORT).show()
            else if(userPwd.length < 8)
                Toast.makeText(this, "The password must have a minimum of 8 characters", Toast.LENGTH_SHORT).show()
            else if(userPwd != userCPwd)
                Toast.makeText(this, "The passwords do not match", Toast.LENGTH_SHORT).show()
            else {
                registerUser(userEmail, userPwd)
                checkUser(userEmail, userPwd)
            }
        }
    }

    private fun isValidEmail(target: String): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    private fun registerUser(userEmail:String, userPwd:String) {
        val client = Client(this)
            .setEndpoint("https://cloud.appwrite.io/v1")
            .setProject("64734c27ee025a6ee21c")

        val account = Account(client)

        GlobalScope.launch(Dispatchers.Main) {
            try {
                val user = account.create(
                    userId = ID.unique(),
                    email = userEmail,
                    password = userPwd,
                )

                // Handle successful registration
                Toast.makeText(this@SignUp, "User registered successfully", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                // Handle registration failure
                Toast.makeText(this@SignUp, "Registration failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
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
                val intent = Intent(this@SignUp, HomePage::class.java)
                intent.putExtra("sessionId", response.id)
                intent.putExtra("userId", response.userId)
                intent.putExtra("email", response.providerUid)
                startActivity(intent)
                finish()

                Toast.makeText(this@SignUp, "Login successful", Toast.LENGTH_SHORT).show()
                // Further flow for the logged-in user
            } catch (e: Exception) {
                // Handle login failure
                Toast.makeText(this@SignUp, "Login failed: ${e.message}", Toast.LENGTH_SHORT).show()
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