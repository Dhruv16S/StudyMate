package com.example.studymate

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class QCardsFragment : Fragment() {

    lateinit var recyclerView : RecyclerView
    var noteName = ArrayList<String>()
    var dateCreated = ArrayList<String>()
    //to store details and name
    lateinit var adapter: CardAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v : View = inflater.inflate(R.layout.fragment_qcards, container, false)

        recyclerView = v.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        noteName.add("UK")
        noteName.add("India")
        noteName.add("Italy")
        noteName.add("UK")
        noteName.add("India")
        noteName.add("Italy")
        noteName.add("UK")
        noteName.add("India")
        noteName.add("Italy")
        noteName.add("UK")
        noteName.add("India")
        noteName.add("Italy")

        dateCreated.add("Silverstone")
        dateCreated.add("Buddh International")
        dateCreated.add("Monza")
        dateCreated.add("Silverstone")
        dateCreated.add("Buddh International")
        dateCreated.add("Monza")
        dateCreated.add("Silverstone")
        dateCreated.add("Buddh International")
        dateCreated.add("Monza")
        dateCreated.add("Silverstone")
        dateCreated.add("Buddh International")
        dateCreated.add("Monza")

        adapter = CardAdapter(noteName, dateCreated, requireActivity())
        recyclerView.adapter = adapter

        return v
    }
}
