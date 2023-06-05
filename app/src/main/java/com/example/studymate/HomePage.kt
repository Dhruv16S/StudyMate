package com.example.studymate

import android.os.Bundle

import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.ramotion.circlemenu.CircleMenuView


class HomePage : AppCompatActivity() {

    private lateinit var frame : FrameLayout
    private lateinit var menu : CircleMenuView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        window.statusBarColor = ContextCompat.getColor(this, R.color.mid_blue)

        frame = findViewById(R.id.frame)
        menu = findViewById(R.id.circle_menu)

        val fragmentManager : FragmentManager = supportFragmentManager
        var fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.frame, HomeFragment())
        fragmentTransaction.commit()
        menu.performClick()
        menu.eventListener = object : CircleMenuView.EventListener() {
            override fun onButtonClickAnimationStart(view: CircleMenuView, index: Int) {
                when (index) {
                    0 -> {
                        var fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
                        frame.removeAllViews()
                        fragmentTransaction.add(R.id.frame, HomeFragment())
                        fragmentTransaction.commit()
                    }
                    1 -> {
                        var fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
                        frame.removeAllViews()
                        fragmentTransaction.add(R.id.frame, NotesFragment())
                        fragmentTransaction.commit()
                    }
                    2 -> {
                        var fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
                        frame.removeAllViews()
                        fragmentTransaction.add(R.id.frame, QCardsFragment())
                        fragmentTransaction.commit()
                    }

                    3 -> {
                        var fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
                        frame.removeAllViews()
                        fragmentTransaction.add(R.id.frame, AccountFragment())
                        fragmentTransaction.commit()
                    }
                }
            }

            override fun onButtonLongClickAnimationStart(view: CircleMenuView, buttonIndex: Int) {
                when (buttonIndex) {
                    0 -> {
                        var fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
                        frame.removeAllViews()
                        fragmentTransaction.add(R.id.frame, HomeFragment())
                        fragmentTransaction.commit()
                    }
                    1 -> {
                        var fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
                        frame.removeAllViews()
                        fragmentTransaction.add(R.id.frame, NotesFragment())
                        fragmentTransaction.commit()
                    }
                    2 -> {
                        var fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
                        frame.removeAllViews()
                        fragmentTransaction.add(R.id.frame, QCardsFragment())
                        fragmentTransaction.commit()
                    }

                    3 -> {
                        var fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
                        frame.removeAllViews()
                        fragmentTransaction.add(R.id.frame, AccountFragment())
                        fragmentTransaction.commit()
                    }
                }
            }
        }
    }
}