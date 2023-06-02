package com.example.studymate

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
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
import io.appwrite.Client
import io.appwrite.services.Databases
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreateNotesFragment : Fragment() {

    private lateinit var addText : Button
    private lateinit var addFile : Button
    private lateinit var saveNotes : Button
    private lateinit var deleteNote : Button
    private lateinit var scroll : ScrollView
    private lateinit var displayNotes : LinearLayout
    private lateinit var noteName : EditText
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
        saveNotes = v.findViewById(R.id.saveNotes)
        deleteNote = v.findViewById(R.id.deleteNote)
        scroll = v.findViewById(R.id.scrollView)
        displayNotes = v.findViewById(R.id.displayNotes)
        noteName = v.findViewById(R.id.noteName)
        preferences = requireActivity().getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)

        sessionId = preferences.getString("sessionId", " ").toString()
        userId = preferences.getString("userId", " ").toString()

        createInstructionCard()

        addText.setOnClickListener {
            val editText = EditText(requireContext())
            val layoutParams = LinearLayout.LayoutParams(370.0.dpToPx(), LinearLayout.LayoutParams.WRAP_CONTENT)
            layoutParams.gravity = Gravity.CENTER // Center aligns the EditText
            editText.layoutParams = layoutParams
            editText.typeface = ResourcesCompat.getFont(requireContext(), R.font.open_sans)
            editText.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            editText.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            editText.setBackgroundResource(0) // Removes the line associated with the EditText
            editText.setPadding(20.0.dpToPx(), 10.0.dpToPx(), 20.0.dpToPx(), 10.0.dpToPx())
            editText.setTextCursorDrawable(R.drawable.black_cursor)
            editText.hint = "Note #${i}"
            displayNotes.addView(editText)
            i += 1
        }

        addFile.setOnClickListener {

        }

        saveNotes.setOnClickListener {
            docCount += 1
            sentencesList = mutableListOf<String>()
            if (noteName.text.isEmpty()) {
                Toast.makeText(context, "Enter a Note Name to proceed", Toast.LENGTH_SHORT).show()
            } else {
                collectTextFromEditTexts()
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
                    } catch (e: Exception) {
                        Log.e("Appwrite", "Error: " + e.message)
                    }
                }
            }
        }


        deleteNote.setOnClickListener {
            val focusedView = displayNotes.findFocus()
            if (focusedView is EditText || focusedView is ImageView) {
                displayNotes.removeView(focusedView)
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

    private fun createInstructionCard() {
        val textView = TextView(requireContext())
        val layoutParams = LinearLayout.LayoutParams(370.0.dpToPx(), LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.gravity = Gravity.CENTER // Center aligns the textView
        textView.layoutParams = layoutParams
        textView.typeface = ResourcesCompat.getFont(requireContext(), R.font.open_sans)
        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        textView.setBackgroundResource(0) // Removes the line associated with the textView
        textView.setPadding(20.0.dpToPx(), 10.0.dpToPx(), 20.0.dpToPx(), 10.0.dpToPx())
        textView.setTextCursorDrawable(R.drawable.black_cursor)
        textView.text = "To get started simply click on the button of your choice below. On clicking Save, your note will be saved. Clicking Delete, will delete the focused text."
        displayNotes.addView(textView)
    }

    private fun Double.dpToPx(): Int {
        val scale = Resources.getSystem().displayMetrics.density
        return (this * scale + 0.5f).toInt()
    }

    private fun collectTextFromEditTexts(): List<String> {
        for (i in 0 until displayNotes.childCount) {
            val view = displayNotes.getChildAt(i)
            if (view is EditText) {
                val text = view.text.toString().trim()
                sentencesList.add(text)
            }
        }

        return sentencesList
    }
}