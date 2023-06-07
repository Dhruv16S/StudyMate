package com.example.studymate

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.fragment.app.FragmentTransaction

@Suppress("DEPRECATION")
class HomeFragment : Fragment() {
    private lateinit var makeNote : ImageView
    private lateinit var frame : FrameLayout
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v : View = inflater.inflate(R.layout.fragment_home, container, false)
        makeNote = v.findViewById(R.id.makeNote)
        frame = v.findViewById(R.id.frameWithinHome)
        makeNote.setOnClickListener {
            val fragmentTransaction : FragmentTransaction = (fragmentManager?.beginTransaction() ?: frame.removeAllViews()) as FragmentTransaction
            frame.removeAllViews()
            fragmentTransaction.add(R.id.frameWithinHome, CreateNotesFragment())
            fragmentTransaction.commit()
        }
        return v
    }
}
