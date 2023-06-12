package com.example.studymate

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import io.appwrite.Client
import io.appwrite.extensions.toJson
import io.appwrite.services.Databases
import io.appwrite.services.Realtime
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@OptIn(DelicateCoroutinesApi::class)
class ViewNotes : AppCompatActivity() {

    private lateinit var viewNoteName : TextView
    private lateinit var viewNotes : LinearLayout
    private lateinit var preferences: SharedPreferences
    private lateinit var userId : String
    private lateinit var sessionId : String
    private var noteContentList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_notes)

        window.statusBarColor = ContextCompat.getColor(this, R.color.home_status_bar)
        viewNoteName = findViewById(R.id.viewNoteName)
        viewNotes = findViewById(R.id.viewNotes)
        preferences = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        sessionId = preferences.getString("sessionId", " ").toString()
        userId = preferences.getString("userId", " ").toString()

        val client = Client(this@ViewNotes)
            .setEndpoint("https://cloud.appwrite.io/v1")
            .setProject("64734c27ee025a6ee21c")

        val database = Databases(client)
        val realtime = Realtime(client)

        val path = "documents"
        realtime.subscribe(path) {
            Log.d("log", it.toString() )
        }

        GlobalScope.launch(Dispatchers.Main) {
            try {
                val response = database.listDocuments(
                    databaseId = "6479d563804822fc79bb",
                    collectionId = "6479f9af8834a056c20d",
                )
                val documents = response.documents
                for (document in documents){
                    if(document.id == intent.getStringExtra("documentId")){
                        viewNoteName.text = document.data["note-name"].toString()
                        noteContentList = Json.decodeFromString(document.data["note-text"]?.toJson()
                            .toString())!!
                        for (item in noteContentList){
                            createQuestionCard(item)
                        }
                    }
                }
            }
            // Further flow for the logged-in user
            catch (e: Exception) {
                Toast.makeText(this@ViewNotes, "Could not fetch data", Toast.LENGTH_SHORT).show()
                Log.d("Error", "$e")
            }
        }
    }
    @SuppressLint("SetTextI18n")
    private fun createQuestionCard(noteContent : String){
        val cardView = CardView(this)

        // Set CardView layout parameters
        val cardLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        cardLayoutParams.setMargins(10.0.dpToPx(), 5.0.dpToPx(), 10.0.dpToPx(), 5.0.dpToPx())
        cardView.layoutParams = cardLayoutParams
        cardView.cardElevation = 25.0.dpToPx().toFloat()
        cardView.setBackgroundResource(R.drawable.note_card_template)

        // Create a LinearLayout to hold the TextView and EditText
        val linearLayout = LinearLayout(this)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.setPadding(16.0.dpToPx(), 16.0.dpToPx(), 16.0.dpToPx(), 16.0.dpToPx())

        val noteDetails = TextView(this)
        noteDetails.text = noteContent
        noteDetails.typeface = ResourcesCompat.getFont(this, R.font.open_sans)
        noteDetails.setTextColor(ContextCompat.getColor(this, R.color.black))
        noteDetails.textSize = 18.0f
        linearLayout.addView(noteDetails)

        cardView.addView(linearLayout)
        viewNotes.addView(cardView)
    }
    private fun Double.dpToPx(): Int {
        val scale = Resources.getSystem().displayMetrics.density
        return (this * scale + 0.5f).toInt()
    }
}