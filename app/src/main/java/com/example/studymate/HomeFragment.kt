package com.example.studymate

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import io.appwrite.Client
import io.appwrite.services.Databases
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class HomeFragment : Fragment() {
    private lateinit var makeNote : ImageView
    private lateinit var frame : FrameLayout
    private lateinit var preferences: SharedPreferences
    private lateinit var homeEmail : TextView
    private lateinit var homeUserId : TextView
    private lateinit var notes : TextView
    private lateinit var files : TextView
    private lateinit var qcards : TextView
    private lateinit var logout : Button
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v : View = inflater.inflate(R.layout.fragment_home, container, false)
        makeNote = v.findViewById(R.id.makeNote)
        frame = v.findViewById(R.id.frameWithinHome)
        homeEmail = v.findViewById(R.id.homeEmail)
        homeUserId = v.findViewById(R.id.homeUserId)
        notes = v.findViewById(R.id.notes)
        files = v.findViewById(R.id.files)
        qcards = v.findViewById(R.id.q_cards)
        logout = v.findViewById(R.id.logout)

        preferences = requireActivity().getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        val userId = preferences.getString("userId", "")

        logout.setOnClickListener {
            val editor : SharedPreferences.Editor = preferences.edit()
            editor.putBoolean("CHECKBOX", false)
            editor.apply()
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        makeNote.setOnClickListener {
            val fragmentTransaction : FragmentTransaction = (fragmentManager?.beginTransaction() ?: frame.removeAllViews()) as FragmentTransaction
            frame.removeAllViews()
            fragmentTransaction.add(R.id.frameWithinHome, CreateNotesFragment())
            fragmentTransaction.commit()
        }
        val client = Client(requireContext())
            .setEndpoint("https://cloud.appwrite.io/v1")
            .setProject("64734c27ee025a6ee21c")

        val database = Databases(client)

        GlobalScope.launch(Dispatchers.Main) {
            try {
                val response = database.getDocument(
                    databaseId = "648081c3025f25473245",
                    collectionId = "6480820466d1d4790f90",
                    documentId = userId.toString(),
                    )
                homeUserId.text = response.data["user-id"].toString()
                homeEmail.text = response.data["email"].toString()
                notes.text = response.data["notes"].toString()
                files.text = response.data["files"].toString()
                qcards.text = response.data["q-cards"].toString()
                // Further flow for the logged-in user
            } catch (e: Exception) {
                // Handle login failure
                Toast.makeText(context, "Could not fetch data", Toast.LENGTH_SHORT).show()
            }
        }
        return v
    }
}
