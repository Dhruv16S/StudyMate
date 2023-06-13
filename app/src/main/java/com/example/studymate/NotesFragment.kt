package com.example.studymate

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.appwrite.Client
import io.appwrite.extensions.toJson
import io.appwrite.services.Databases
import io.appwrite.services.Realtime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.DelicateCoroutinesApi


@OptIn(DelicateCoroutinesApi::class)
class NotesFragment : Fragment() {
    private lateinit var recyclerView : RecyclerView
    private lateinit var userId : String
    private var noteName = ArrayList<String>()
    private var dateCreated = ArrayList<String>()
    private var documentId = ArrayList<String>()
    //to store details and name
    private lateinit var adapter: CardAdapter
    private lateinit var preferences: SharedPreferences

    private val channelId = "channel_tracking"
    private val notificationID = 101
    private var permissionRequestCode = 200

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v : View = inflater.inflate(R.layout.fragment_notes, container, false)


        createNotificationChannel()
        preferences = requireActivity().getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)

        recyclerView = v.findViewById(R.id.noteRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        userId = preferences.getString("userId", " ").toString()

        val client = Client(requireContext())
            .setEndpoint("https://cloud.appwrite.io/v1")
            .setProject("64734c27ee025a6ee21c")

        val database = Databases(client)
        val realtime = Realtime(client)

        val path = "documents"
        realtime.subscribe(path) {
            try{
                val jsonObject = JSONObject(it.payload.toJson())
                val receiver = jsonObject.getString("receiver")
                Log.d("log", it.payload.toJson())
                if (receiver == userId) {
                    sendNotification(requireContext())
                }
            }catch (_ : Exception){

            }
        }

        GlobalScope.launch(Dispatchers.Main) {
            try {
                val response = database.listDocuments(
                    databaseId = "6479d563804822fc79bb",
                    collectionId = "6479f9af8834a056c20d",
                )
                val documents = response.documents
                for (document in documents){
                    if(document.data["user-id"].toString() == userId){
                        noteName.add(document.data["note-name"].toString())
                        dateCreated.add(document.data["date-created"].toString())
                        documentId.add(document.id)
                    }
                }
                val frag = "notes"
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
    private fun createNotificationChannel(){
        val name = "Notification Title"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, name, importance)
            .apply {  }
        val notificationManager : NotificationManager = context?.getSystemService(Context. NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun sendNotification(context: Context) {
        val channelId = channelId

        // Create a notification intent (if needed)
        val notificationIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        // Build the notification
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.share_icon)
            .setContentTitle("StudyMate")
            .setContentText("You received a note")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent) // Set the notification intent

        // Get the notification manager and show the notification
        val notificationManager = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    permissionRequestCode
                )
            }
        }
        notificationManager.notify(notificationID, builder.build())
    }

}
