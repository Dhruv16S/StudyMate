package com.example.studymate

import android.annotation.SuppressLint
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import io.appwrite.Client
import io.appwrite.services.Databases
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class QuestionAnsweringSystem : AppCompatActivity() {

    private lateinit var noteName : TextView
    private lateinit var noteText: ArrayList<*>
    private lateinit var textData : String
    private lateinit var question : EditText
    private lateinit var askQuestion : Button
    private lateinit var displayQuestions : LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_answering_system)

        window.statusBarColor = ContextCompat.getColor(this, R.color.home_status_bar)
        noteName = findViewById(R.id.qAndaNoteName)
        question = findViewById(R.id.questionContent)
        askQuestion = findViewById(R.id.askQuestion)
        displayQuestions = findViewById(R.id.displayQuestions)

        getNoteData(object : NoteDataCallback {
            override fun onNoteDataLoaded() {
                // textData has the entire content to be passed to BERT
                // get question from user, send it to BERT.
                // pass the content of question.text.toString() to BERT
                // send both question and answer received from BERT to createQuestionCard
                createQuestionCard("question", "answer")
            }
        })
    }

    interface NoteDataCallback {
        fun onNoteDataLoaded()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun getNoteData(callback: NoteDataCallback) {
        val passedDocumentId = intent.getStringExtra("documentId")

        val client = Client(this)
            .setEndpoint("https://cloud.appwrite.io/v1")
            .setProject("64734c27ee025a6ee21c")

        val database = Databases(client)

        GlobalScope.launch(Dispatchers.Main) {
            try {
                val response = database.getDocument(
                    databaseId = "6479d563804822fc79bb",
                    collectionId = "6479f9af8834a056c20d",
                    documentId = passedDocumentId.toString()
                )
                noteName.text = response.data["note-name"].toString()
                noteText = response.data["note-text"] as ArrayList<*>
                textData = noteText.joinToString(separator = ". ")
                callback.onNoteDataLoaded()
            } catch (e: Exception) {
                Toast.makeText(this@QuestionAnsweringSystem, "Could not fetch data", Toast.LENGTH_SHORT).show()
                Log.d("Error", "$e")
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun createQuestionCard(questionContent : String, answerContent : String){
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

        // Create the question
        val questionTextView = TextView(this)
        questionTextView.text = questionContent
        questionTextView.typeface = ResourcesCompat.getFont(this, R.font.open_sans)
        questionTextView.setTextColor(ContextCompat.getColor(this, R.color.black))
        questionTextView.textSize = 18.0f
        linearLayout.addView(questionTextView)

        // Create the question
        val answerTextView = TextView(this)
        answerTextView.text = answerContent
        answerTextView.typeface = ResourcesCompat.getFont(this, R.font.open_sans)
        answerTextView.setTextColor(ContextCompat.getColor(this, R.color.black))
        answerTextView.textSize = 18.0f
        linearLayout.addView(answerTextView)
        cardView.addView(linearLayout)

        // Add the CardView to the parent LinearLayout (displayNotes)
        displayQuestions.addView(cardView)
        question.setText("")
    }

    private fun Double.dpToPx(): Int {
        val scale = Resources.getSystem().displayMetrics.density
        return (this * scale + 0.5f).toInt()
    }
}