package com.example.studymate

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import io.appwrite.Client
import io.appwrite.services.Account
import io.appwrite.ID
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import io.appwrite.services.Databases

class SignUp : AppCompatActivity() {

    private lateinit var textTagline : TextView
    private lateinit var emailAddress : EditText
    private lateinit var password : EditText
    private lateinit var confirmPassword : EditText
    private lateinit var signUp : Button
    private lateinit var gotoLogin : LinearLayout
    private lateinit var signUpCheckBox : CheckBox
    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        window.statusBarColor = ContextCompat.getColor(this, R.color.mid_blue)

        textTagline = findViewById(R.id.signUptextTagline)
        emailAddress = findViewById(R.id.signUpeditTextUsername)
        password = findViewById(R.id.signUpeditTextPassword)
        confirmPassword = findViewById(R.id.signUpeditTextConfirmPassword)
        gotoLogin = findViewById(R.id.gotoLogin)
        signUp = findViewById(R.id.signUp)
        signUpCheckBox = findViewById(R.id.signUpRemember)

        preferences = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)

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
            }
        }
    }
    private fun isValidEmail(target: String): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun registerUser(userEmail:String, userPwd:String) {
        val client = Client(this)
            .setEndpoint("https://cloud.appwrite.io/v1")
            .setProject("64734c27ee025a6ee21c")

        val account = Account(client)

        GlobalScope.launch(Dispatchers.Main) {
            try {
                account.create(
                    userId = ID.unique(),
                    email = userEmail,
                    password = userPwd,
                )
                // Handle successful registration
                Toast.makeText(this@SignUp, "User registered successfully", Toast.LENGTH_SHORT).show()

                if(signUpCheckBox.isChecked){
                    val editor: SharedPreferences.Editor = preferences.edit()
                    editor.putBoolean("CHECKBOX", true)
                    editor.apply()
                }
                checkUser(userEmail, userPwd)
            } catch (e: Exception) {
                // Handle registration failure
                Toast.makeText(this@SignUp, "Registration failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun checkUser(userEmail: String, userPwd: String) {
        val client = Client(this)
            .setEndpoint("https://cloud.appwrite.io/v1")
            .setProject("64734c27ee025a6ee21c")

        val account = Account(client)
        val database = Databases(client)

        GlobalScope.launch(Dispatchers.Main) {
            try {
                intent = Intent(this@SignUp, HomePage::class.java)
                val response = account.createEmailSession(
                    email = userEmail,
                    password = userPwd,
                )
                database.createDocument(
                    databaseId = "648081c3025f25473245",
                    collectionId = "6480820466d1d4790f90",
                    documentId = response.userId,
                    data = mapOf(
                        "user-id" to response.userId,
                        "session-id" to response.id,
                        "email" to userEmail,
                        "files" to 0,
                        "notes" to 0,
                        "q-cards" to 0
                    )
                )
                // Handle successful login
                val editor : SharedPreferences.Editor = preferences.edit()
                editor.putString("sessionId", response.id)
                editor.putString("userId", response.userId)
                editor.apply()
                startActivity(intent)
                finish()
                // Further flow for the logged-in user
            } catch (e: Exception) {
                // Handle login failure
                Toast.makeText(this@SignUp, "Could not login", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(this.currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }
}