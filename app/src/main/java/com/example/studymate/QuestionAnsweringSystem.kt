package com.example.studymate

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.extensions.toJson
import io.appwrite.services.Databases
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.text.qa.BertQuestionAnswerer
import org.tensorflow.lite.task.text.qa.BertQuestionAnswerer.BertQuestionAnswererOptions
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
@OptIn(DelicateCoroutinesApi::class)
class QuestionAnsweringSystem : AppCompatActivity() {

    private lateinit var noteName : TextView
    private lateinit var noteText: ArrayList<*>
    private lateinit var textData : String
    private lateinit var question : EditText
    private lateinit var askQuestion : Button
    private lateinit var displayQuestions : LinearLayout
    private lateinit var preferences: SharedPreferences
    private lateinit var userId : String
    private lateinit var sessionId : String
    private lateinit var questionId : String
    private var questionCount : Int = 0
    private var count : Int = 0
    private var questionAndAnswerList = mutableListOf<String>()
    private var questionCardExists : Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_answering_system)

        window.statusBarColor = ContextCompat.getColor(this, R.color.home_status_bar)
        noteName = findViewById(R.id.qAndaNoteName)
        question = findViewById(R.id.questionContent)
        askQuestion = findViewById(R.id.askQuestion)
        displayQuestions = findViewById(R.id.displayQuestions)

        preferences = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        sessionId = preferences.getString("sessionId", " ").toString()
        userId = preferences.getString("userId", " ").toString()

        val client = Client(this@QuestionAnsweringSystem)
            .setEndpoint("https://cloud.appwrite.io/v1")
            .setProject("64734c27ee025a6ee21c")

        val database = Databases(client)

        GlobalScope.launch(Dispatchers.Main) {
            try {
                val response = database.listDocuments(
                    databaseId = "6486eab49597787c39e3",
                    collectionId = "6486eac01060479d4782",
                )
                val documents = response.documents
                for (document in documents){
                    if(document.data["note-id"].toString() == intent.getStringExtra("documentId")){
                        questionCardExists = true
                        questionId = document.id
                        questionAndAnswerList = Json.decodeFromString<MutableList<String>?>(document.data["question-answer"]?.toJson()
                            .toString())!!
                        for (item in questionAndAnswerList){
                            createQuestionCard(item.split("\n")[0], item.split("\n")[1])
                        }
                    }
                }
            }
            // Further flow for the logged-in user
            catch (e: Exception) {
                // Handle login failure
                Toast.makeText(this@QuestionAnsweringSystem, "Could not fetch data", Toast.LENGTH_SHORT).show()
                Log.d("Error", "$e")
            }
        }

        getNoteData(object : NoteDataCallback {
            override fun onNoteDataLoaded() {

                readAndUpdateDatabase(shouldUpdate = false)

                val options = BertQuestionAnswererOptions.builder()
                    .setBaseOptions(BaseOptions.builder().setNumThreads(4).build())
                    .build()
                val answerer = BertQuestionAnswerer.createFromFileAndOptions(
                    this@QuestionAnsweringSystem, "mobilebert.tflite", options
                )
                askQuestion.setOnClickListener{

                    if(question.text.isEmpty()){
                        Toast.makeText(
                            this@QuestionAnsweringSystem,
                            "Enter a valid question",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else{
                        val answers = answerer.answer(
                            textData,
                            question.text.toString()
                        )
                        if (answers.isNotEmpty()) {
                            val highestConfidenceAnswer = answers.first()
                            var highestConfidenceAnswerText = highestConfidenceAnswer.text
                            val words = highestConfidenceAnswerText.split(" ")
                            val capitalizedWords = words.map { it.capitalize() }
                            highestConfidenceAnswerText = capitalizedWords.joinToString(" ")
                            createQuestionCard(
                                question.text.toString(),
                                highestConfidenceAnswerText
                            )
                            questionAndAnswerList.add(question.text.toString() + "\n" + highestConfidenceAnswerText)
                            count += 1
                            question.setText("")
                        } else {
                            Toast.makeText(
                                this@QuestionAnsweringSystem,
                                "Could not find the answer",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        })
    }

    override fun onBackPressed() {
        // Check if the user went back
        // Perform your desired action here
        updateQuestionCards(questionCardExists = questionCardExists)
        Toast.makeText(this@QuestionAnsweringSystem, "Questions Saved!", Toast.LENGTH_SHORT).show()
        super.onBackPressed()
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
        questionTextView.text = questionContent + "\n"
        questionTextView.typeface = ResourcesCompat.getFont(this, R.font.open_sans_bold)
        questionTextView.setTextColor(ContextCompat.getColor(this, R.color.black))
        questionTextView.textSize = 18.0f

        linearLayout.addView(questionTextView)

        // Create the question
        val answerTextView = TextView(this)
        answerTextView.text = answerContent
        answerTextView.typeface = ResourcesCompat.getFont(this, R.font.montserrat)
        answerTextView.setTextColor(ContextCompat.getColor(this, R.color.black))
        answerTextView.textSize = 16.0f
        linearLayout.addView(answerTextView)
        cardView.addView(linearLayout)

        // Add the CardView to the parent LinearLayout (displayNotes)
        displayQuestions.addView(cardView)
    }

    private fun Double.dpToPx(): Int {
        val scale = Resources.getSystem().displayMetrics.density
        return (this * scale + 0.5f).toInt()
    }
    @OptIn(DelicateCoroutinesApi::class)
    private fun readAndUpdateDatabase(shouldUpdate :  Boolean) {
        val client = Client(this)
            .setEndpoint("https://cloud.appwrite.io/v1")
            .setProject("64734c27ee025a6ee21c")

        val database = Databases(client)

        GlobalScope.launch(Dispatchers.Main) {
            try {
                if(!shouldUpdate){
                    val response = database.getDocument(
                        databaseId = "648081c3025f25473245",
                        collectionId = "6480820466d1d4790f90",
                        documentId = userId,
                    )
                    questionCount = (response.data["q-cards"] as Long).toInt()
                }
                else{
                    questionCount += count
                    questionAndAnswerList = mutableListOf()
                    database.updateDocument(
                        databaseId = "648081c3025f25473245",
                        collectionId = "6480820466d1d4790f90",
                        documentId = userId,
                        data = mapOf(
                            "q-cards" to questionCount,
                        )
                    )
                }
                // Further flow for the logged-in user
            } catch (e: Exception) {
                // Handle login failure
                Toast.makeText(this@QuestionAnsweringSystem, "Could not fetch data", Toast.LENGTH_SHORT).show()
                Log.d("Error", "$e")
            }
        }
    }

    private fun updateQuestionCards(questionCardExists : Boolean){
        CoroutineScope(Dispatchers.Main).launch {
            val client = Client(this@QuestionAnsweringSystem)
                .setEndpoint("https://cloud.appwrite.io/v1")
                .setProject("64734c27ee025a6ee21c")
            val databases = Databases(client)
            try {
                if(questionCardExists){
                    databases.updateDocument(
                        databaseId = "6486eab49597787c39e3",
                        collectionId = "6486eac01060479d4782",
                        documentId = questionId,
                        data = mapOf(
                            "question-answer" to questionAndAnswerList,
                        )
                    )
                }
                else{
                    databases.createDocument(
                        databaseId = "6486eab49597787c39e3",
                        collectionId = "6486eac01060479d4782",
                        documentId = ID.unique(),
                        data = mapOf(
                            "session-id" to sessionId,
                            "user-id" to userId,
                            "question-answer" to questionAndAnswerList,
                            "note-id" to intent.getStringExtra("documentId")
                        )
                    )
                }
                Toast.makeText(
                    this@QuestionAnsweringSystem,
                    "Questions Saved!",
                    Toast.LENGTH_SHORT
                ).show()
                readAndUpdateDatabase(shouldUpdate = true)
            } catch (e: Exception) {
                Log.e("Appwrite", "Error: " + e.message)
            }
        }
    }
}