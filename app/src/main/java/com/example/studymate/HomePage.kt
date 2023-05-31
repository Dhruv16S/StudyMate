package com.example.studymate

import android.os.Bundle

import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.ramotion.circlemenu.CircleMenuView


class HomePage : AppCompatActivity() {

    private lateinit var homeUserEmail : TextView
    private lateinit var homeUserId : TextView
    private lateinit var frame : FrameLayout
    private lateinit var menu : CircleMenuView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)
        val sessionId = intent.getStringExtra("sessionId")
        val userId = intent.getStringExtra("userId")
        val email = intent.getStringExtra("email")

        frame = findViewById(R.id.frame)
        menu = findViewById(R.id.circle_menu)

//        homeUserEmail.text = email
//        homeUserId.text = userId

        val fragmentManager : FragmentManager = supportFragmentManager
        var fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.frame, HomeFragment())
        fragmentTransaction.commit()

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
                    else -> {
                        var fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
                        frame.removeAllViews()
                        fragmentTransaction.add(R.id.frame, QCardsFragment())
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
                    else -> {
                        var fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
                        frame.removeAllViews()
                        fragmentTransaction.add(R.id.frame, QCardsFragment())
                        fragmentTransaction.commit()
                    }
                }
            }
        }
    }
}