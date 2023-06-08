package com.example.studymate

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class CardAdapter(
    countryNameList: ArrayList<String>,
    detailsList: ArrayList<String>,
    var context: Context
) : RecyclerView.Adapter<CardAdapter.CountryViewHolder>() {
    private var noteName = countryNameList
    private var dateCreated = detailsList

    class CountryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var noteName : TextView = itemView.findViewById(R.id.cardNoteName)
        var dateCreated : TextView = itemView.findViewById(R.id.cardDateCreated)
        var cardView : CardView = itemView.findViewById(R.id.cardView)
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
        holder.cardView.setOnClickListener{

            Toast.makeText(context, "You selected ${noteName[position]}", Toast.LENGTH_SHORT).show()
        }

    }

    override fun getItemCount(): Int {
        //amount of data to be displayed
        return noteName.size
    }

}