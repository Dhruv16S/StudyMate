package com.example.studymate

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.appwrite.Client
import io.appwrite.services.Databases
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class QCardsFragment : Fragment() {

    private lateinit var recyclerView : RecyclerView
    private lateinit var userId : String
    private var noteName = ArrayList<String>()
    private var dateCreated = ArrayList<String>()
    private var documentId = ArrayList<String>()
    //to store details and name
    private lateinit var adapter: CardAdapter
    private lateinit var preferences: SharedPreferences
    private lateinit var qCardFrame : FrameLayout
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v: View = inflater.inflate(R.layout.fragment_qcards, container, false)

        preferences = requireActivity().getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)

        recyclerView = v.findViewById(R.id.recyclerView)
        qCardFrame = v.findViewById(R.id.qCardFrame)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        userId = preferences.getString("userId", " ").toString()

        val client = Client(requireContext())
            .setEndpoint("https://cloud.appwrite.io/v1")
            .setProject("64734c27ee025a6ee21c")

        val database = Databases(client)

        GlobalScope.launch(Dispatchers.Main) {
            try {
                val response = database.listDocuments(
                    databaseId = "6479d563804822fc79bb",
                    collectionId = "6479f9af8834a056c20d",
                    //listOf(Query.equal("user-id", userId))
                )
                val documents = response.documents
                for (document in documents){
                    if(document.data["user-id"].toString() == userId){
                        noteName.add(document.data["note-name"].toString())
                        dateCreated.add(document.data["date-created"].toString())
                        documentId.add(document.id)
                    }
                }
                val frag = "questions"
                adapter = CardAdapter(noteName, dateCreated, documentId, userId, frag, requireActivity())
                recyclerView.adapter = adapter
            }
            // Further flow for the logged-in user
            catch (e: Exception) {
                // Handle login failure
                Toast.makeText(context, "Could not fetch data", Toast.LENGTH_SHORT).show()
                Log.d("Error", "$e")
            }
        }
        return v
    }
}
