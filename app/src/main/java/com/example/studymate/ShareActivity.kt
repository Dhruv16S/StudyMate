package com.example.studymate

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import io.appwrite.Client
import io.appwrite.services.Databases
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import android.content.SharedPreferences
import io.appwrite.ID
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ShareActivity : AppCompatActivity() {

    private lateinit var receiverEmailAddress : EditText
    private lateinit var shareNow : Button
    private lateinit var cancel : Button
    private lateinit var sharedNote : TextView
    private lateinit var preferences: SharedPreferences
    private lateinit var noteId : String
    private lateinit var senderUserId : String
    private lateinit var receiverUserId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)

        window.statusBarColor = ContextCompat.getColor(this, R.color.home_status_bar)

        receiverEmailAddress = findViewById(R.id.receiverEmailId)
        shareNow = findViewById(R.id.shareNow)
        cancel = findViewById(R.id.cancelShare)
        sharedNote = findViewById(R.id.sharedNoteName)
        preferences = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        noteId = intent.getStringExtra("documentId").toString()
        senderUserId = intent.getStringExtra("userId").toString()
        sharedNote.text = intent.getStringExtra("noteName").toString()

        shareNow.setOnClickListener {
            if (receiverEmailAddress.text.toString().isEmpty()){
                Toast.makeText(this@ShareActivity, "Enter the receiver's email address", Toast.LENGTH_SHORT).show()
            }
            else{
                val client = Client(this@ShareActivity)
                    .setEndpoint("https://cloud.appwrite.io/v1")
                    .setProject("64734c27ee025a6ee21c")

                val database = Databases(client)

                GlobalScope.launch(Dispatchers.Main) {
                    //Check if receiver email address exists
                    try {
                        val response = database.listDocuments(
                            databaseId = "648081c3025f25473245",
                            collectionId = "6480820466d1d4790f90",
                        )
                        val documents = response.documents
                        for (document in documents) {
                            if (document.data["email"].toString() == receiverEmailAddress.text.toString()) {
                                receiverUserId = document.data["user-id"].toString()
                                break
                            }
                        }
                        // Retrieve note data
                        val noteDoc = database.getDocument(
                            databaseId = "6479d563804822fc79bb",
                            collectionId = "6479f9af8834a056c20d",
                            documentId = noteId
                        )
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val currentDate = Date()

                        //adding to created-notes
                        database.createDocument(
                            databaseId = "6479d563804822fc79bb",
                            collectionId = "6479f9af8834a056c20d",
                            documentId = ID.unique(),
                            data = mapOf(
                                "session-id" to senderUserId,
                                "user-id" to receiverUserId,
                                "note-name" to noteDoc.data["note-name"],
                                "note-text" to noteDoc.data["note-text"],
                                "date-created" to "Shared on: " + dateFormat.format(currentDate)
                            )
                        )
                        //adding to shared-notes
                        database.createDocument(
                            databaseId = "6479d563804822fc79bb",
                            collectionId = "64872e58aa0f73e281e0",
                            documentId = ID.unique(),
                            data = mapOf(
                                "sender" to senderUserId,
                                "receiver" to receiverUserId,
                                "note-name" to noteDoc.data["note-name"],
                                "note-text" to noteDoc.data["note-text"],
                                "date-shared" to dateFormat.format(currentDate)
                            )
                        )
                        Toast.makeText(
                            this@ShareActivity,
                            "Note shared with ${receiverEmailAddress.text}",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }catch (e:Exception){
                        Toast.makeText(
                            this@ShareActivity,
                            "Could not share note",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                }
            }
        }
        cancel.setOnClickListener {
            finish()
        }

    }
}