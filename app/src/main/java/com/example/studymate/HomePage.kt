package com.example.studymate

import android.os.Bundle

import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.ramotion.circlemenu.CircleMenuView


@Suppress("NAME_SHADOWING")
class HomePage : AppCompatActivity() {

    private lateinit var frame : FrameLayout
    private lateinit var menu : CircleMenuView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        window.statusBarColor = ContextCompat.getColor(this, R.color.home_status_bar)

        frame = findViewById(R.id.frame)
        menu = findViewById(R.id.circle_menu)

        val fragmentManager : FragmentManager = supportFragmentManager
        val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.frame, HomeFragment())
        fragmentTransaction.commit()

        menu.eventListener = object : CircleMenuView.EventListener() {
            override fun onButtonClickAnimationStart(view: CircleMenuView, index: Int) {
                when (index) {
                    0 -> {
                        val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
                        frame.removeAllViews()
                        fragmentTransaction.add(R.id.frame, HomeFragment())
                        fragmentTransaction.commit()
                    }
                    1 -> {
                        val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
                        frame.removeAllViews()
                        fragmentTransaction.add(R.id.frame, CreateNotesFragment())
                        fragmentTransaction.commit()
                    }
                    2 -> {
                        val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
                        frame.removeAllViews()
                        fragmentTransaction.add(R.id.frame, NotesFragment())
                        fragmentTransaction.commit()
                    }

                    3 -> {
                        val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
                        frame.removeAllViews()
                        fragmentTransaction.add(R.id.frame, QCardsFragment())
                        fragmentTransaction.commit()
                    }
                }
            }

            override fun onButtonLongClickAnimationStart(view: CircleMenuView, buttonIndex: Int) {
                when (buttonIndex) {
                    0 -> {
                        val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
                        frame.removeAllViews()
                        fragmentTransaction.add(R.id.frame, HomeFragment())
                        fragmentTransaction.commit()
                    }
                    1 -> {
                        val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
                        frame.removeAllViews()
                        fragmentTransaction.add(R.id.frame, CreateNotesFragment())
                        fragmentTransaction.commit()
                    }
                    2 -> {
                        val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
                        frame.removeAllViews()
                        fragmentTransaction.add(R.id.frame, NotesFragment())
                        fragmentTransaction.commit()
                    }

                    3 -> {
                        val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
                        frame.removeAllViews()
                        fragmentTransaction.add(R.id.frame, QCardsFragment())
                        fragmentTransaction.commit()
                    }
                }
            }
        }
    }
}