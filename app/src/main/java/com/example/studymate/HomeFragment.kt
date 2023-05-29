package com.example.studymate

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

class HomeFragment : Fragment() {
    private lateinit var makeNote : ImageView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v : View = inflater.inflate(R.layout.fragment_home, container, false)
        makeNote = v.findViewById(R.id.makeNote)
        makeNote.setOnClickListener {
            Toast.makeText(requireContext(), "Clicked", Toast.LENGTH_SHORT).show()
        }
        return v
    }
}
