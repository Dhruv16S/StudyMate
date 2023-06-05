package com.example.studymate

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.view.marginBottom
import androidx.core.view.marginStart
import io.appwrite.Client
import io.appwrite.services.Databases
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreateNotesFragment : Fragment() {

    private lateinit var addText : Button
    private lateinit var addFile : Button
    private lateinit var addOcr : Button
    private lateinit var saveNotes : Button
    private lateinit var scroll : ScrollView
    private lateinit var displayNotes : LinearLayout
    private lateinit var noteName : EditText
    private lateinit var noteContent : EditText
    private lateinit var preferences: SharedPreferences
    private lateinit var userId : String
    private lateinit var sessionId : String
    private var docCount : Int = 0

    private var sentencesList = mutableListOf<String>()
    private var i : Int = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val v : View = inflater.inflate(R.layout.fragment_create_notes, container, false)

        addText = v.findViewById(R.id.addText)
        addFile = v.findViewById(R.id.addFile)
        addOcr = v.findViewById(R.id.addOcr)
        saveNotes = v.findViewById(R.id.saveNotes)
        scroll = v.findViewById(R.id.scrollView)
        displayNotes = v.findViewById(R.id.displayNotes)
        noteName = v.findViewById(R.id.noteName)
        noteContent = v.findViewById(R.id.noteContent)
        preferences = requireActivity().getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        sessionId = preferences.getString("sessionId", " ").toString()
        userId = preferences.getString("userId", " ").toString()

        addText.setOnClickListener {
            val toBeAdded = noteContent.text.toString()

            if(toBeAdded.isEmpty()){
                Toast.makeText(context, "Enter some text: ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Create a new CardView
            sentencesList.add(toBeAdded)
            val cardView = CardView(requireContext())

            // Set CardView layout parameters
            val cardLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            cardLayoutParams.setMargins(10.0.dpToPx(), 5.0.dpToPx(), 10.0.dpToPx(), 5.0.dpToPx())
            cardView.layoutParams = cardLayoutParams
            cardView.cardElevation = 25.0.dpToPx().toFloat()
            cardView.setBackgroundResource(R.drawable.note_card_template)

            // Create a LinearLayout to hold the TextView and EditText
            val linearLayout = LinearLayout(requireContext())
            linearLayout.orientation = LinearLayout.VERTICAL
            linearLayout.setPadding(16.0.dpToPx(), 16.0.dpToPx(), 16.0.dpToPx(), 16.0.dpToPx())

            // Create the TextView
            val textView = TextView(requireContext())
            textView.text = "Note ${i}:"
            textView.typeface = ResourcesCompat.getFont(requireContext(), R.font.open_sans)
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            textView.textSize = 16.0f
            textView.setTypeface(null, Typeface.BOLD)

            // Add the TextView to the LinearLayout
            linearLayout.addView(textView)

            // Create the EditText
            val editText = EditText(requireContext())
            val editTextLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            editText.layoutParams = editTextLayoutParams
            editText.typeface = ResourcesCompat.getFont(requireContext(), R.font.open_sans)
            editText.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            editText.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            editText.setBackgroundResource(0) // Removes the line associated with the EditText
            editText.setTextCursorDrawable(R.drawable.black_cursor)
            editText.setText(toBeAdded)
            editText.tag = "noteEditText"
            editText.textSize = 17.0f
            editText.setTypeface(null, Typeface.BOLD)

            // Add the EditText to the LinearLayout
            linearLayout.addView(editText)

            // Add the LinearLayout to the CardView
            cardView.addView(linearLayout)

            // Add the CardView to the parent LinearLayout (displayNotes)
            displayNotes.addView(cardView)

            i += 1
            noteContent.setText("")
        }


        addFile.setOnClickListener {

        }

        addOcr.setOnClickListener {

        }

        saveNotes.setOnClickListener {
            docCount += 1
            if (noteName.text.isEmpty()) {
                Toast.makeText(context, "Enter a Note Name to proceed", Toast.LENGTH_SHORT).show()
            } else {

                Log.d("list", sentencesList.toString())
                CoroutineScope(Dispatchers.Main).launch {

                    val client = Client(requireContext())
                        .setEndpoint("https://cloud.appwrite.io/v1")
                        .setProject("64734c27ee025a6ee21c")

                    val databases = Databases(client)

                    // Create a collection first
                    // Could not do this
                    Log.d("userId", userId)
                    Log.d("userId", sessionId)
                    try {
                        val response = databases.createDocument(
                            databaseId = "6479d563804822fc79bb",
                            collectionId = "6479f9af8834a056c20d",
                            documentId = sessionId + docCount.toString(),
                            data = mapOf(
                                    "session-id" to sessionId,
                                    "user-id" to userId,
                                    "note-name" to noteName.text.toString(),
                                    "note-content" to sentencesList
                                )
                        )
                        Toast.makeText(context, "Note Created!", Toast.LENGTH_SHORT).show()
                        sentencesList = mutableListOf<String>()
                        i = 0
                        noteName.setText("")
                        displayNotes.removeAllViews()
                    } catch (e: Exception) {
                        Log.e("Appwrite", "Error: " + e.message)
                    }
                }
            }
        }


        v.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
            }
            false
        }

        return v
    }

    private fun Double.dpToPx(): Int {
        val scale = Resources.getSystem().displayMetrics.density
        return (this * scale + 0.5f).toInt()
    }
}