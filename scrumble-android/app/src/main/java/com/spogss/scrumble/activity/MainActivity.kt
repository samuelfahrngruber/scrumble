package com.spogss.scrumble.activity

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import com.spogss.scrumble.R
import com.spogss.scrumble.fragment.DailyScrumFragment
import com.spogss.scrumble.fragment.MyTasksFragment
import com.spogss.scrumble.fragment.ProjectsFragment
import com.spogss.scrumble.fragment.ScrumBoardFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private val fragments = mutableListOf<Fragment>()

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        var res = true
        var selectedFragment = fragments[0]
        when (item.itemId) {
            R.id.navigation_my_tasks -> {
                setupTitle("My Tasks")
            }
            R.id.navigation_scrum_board -> {
                selectedFragment = fragments[1]
                setupTitle("Scrum Board")
            }
            R.id.navigation_daily_scrum -> {
                selectedFragment = fragments[2]
                setupTitle("Daily Scrum")
            }
            R.id.navigation_project_overview -> {
                selectedFragment = fragments[3]
                setupTitle("Projects")
            }
            else -> res = false
        }
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout, selectedFragment)
        transaction.commit()

        res
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupFragments()

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        navigation.selectedItemId = R.id.navigation_my_tasks

    }

    private fun setupFragments() {
        fragments.add(MyTasksFragment())
        fragments.add(ScrumBoardFragment())
        fragments.add(DailyScrumFragment())
        fragments.add(ProjectsFragment())
    }

    private fun setupTitle(newTitle: String) {
        val formattedText = SpannableString(newTitle)
        formattedText.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorAccent)), 0, formattedText.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        title = formattedText
    }
}
