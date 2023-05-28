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
            else if(userPwd.length < 6)
                Toast.makeText(this, "The password must have a minimum of 6 characters", Toast.LENGTH_SHORT).show()
            else if(userPwd != userCPwd)
                Toast.makeText(this, "The passwords do not match", Toast.LENGTH_SHORT).show()
            else {
                // Fetch data from database, and compare email address and password
                if(false){
                    Toast.makeText(this, "Could not signup. Try again", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this, "Registered successfully!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun isValidEmail(target: String): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = this!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(this!!.currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }
}