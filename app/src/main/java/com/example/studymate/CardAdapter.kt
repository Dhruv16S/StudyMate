package com.example.studymate

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView

class CardAdapter(
    noteNameList: ArrayList<String>,
    dateCreatedList: ArrayList<String>,
    documentIdList: ArrayList<String>,
    frag : String,
    var context: Context
) : RecyclerView.Adapter<CardAdapter.CountryViewHolder>() {
    private var noteName = noteNameList
    private var dateCreated = dateCreatedList
    private var documentId = documentIdList
    private var frag = frag
    class CountryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var noteName : TextView = itemView.findViewById(R.id.cardNoteName)
        var dateCreated : TextView = itemView.findViewById(R.id.cardDateCreated)
        var cardView : CardView = itemView.findViewById(R.id.cardView)
        var shareButton : Button = itemView.findViewById(R.id.share)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        //define the card design we made aka which design is displayed
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.card_design, parent, false)
        return CountryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int){
        //what should be done when design is connected to recycler view
        holder.noteName.text = noteName[position]
        holder.dateCreated.text = dateCreated[position]

        //card view is defined here, so toast is also specified here
        holder.shareButton.setOnClickListener {
            val intent = Intent(context, ShareActivity::class.java)
            intent.putExtra("documentId", documentId[position])
            context.startActivity(intent)
        }

        holder.cardView.setOnClickListener{
            if(frag == "questions") {
                val intent = Intent(context, QuestionAnsweringSystem::class.java)
                intent.putExtra("documentId", documentId[position])
                context.startActivity(intent)
            }
            else{
                val intent = Intent(context, ViewNotes::class.java)
                intent.putExtra("documentId", documentId[position])
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        //amount of data to be displayed
        return noteName.size
    }
}