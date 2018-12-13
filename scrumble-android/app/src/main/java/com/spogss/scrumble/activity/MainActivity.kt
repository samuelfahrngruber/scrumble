package com.spogss.scrumble.activity

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.spogss.scrumble.R
import com.spogss.scrumble.controller.MiscUIController
import com.spogss.scrumble.controller.ScrumbleController
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
                setupTitle("${ScrumbleController.currentUser.name.capitalize()}'${if(!ScrumbleController.currentUser.name.endsWith("s")) "s" else ""} Tasks")
            }
            R.id.navigation_scrum_board -> {
                selectedFragment = fragments[1]
                setupTitle(resources.getString(R.string.scrum_board))
            }
            R.id.navigation_daily_scrum -> {
                selectedFragment = fragments[2]
                setupTitle(resources.getString(R.string.daily_scrum))
            }
            R.id.navigation_project_overview -> {
                selectedFragment = fragments[3]
                setupTitle(
                if(ScrumbleController.currentProject == null)
                    resources.getString(R.string.projects)
                else
                    ScrumbleController.currentProject!!.name)
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
        supportActionBar!!.hide()

        ScrumbleController.currentProject = null
        ScrumbleController.users.clear()
        ScrumbleController.projects.clear()
        ScrumbleController.sprints.clear()
        ScrumbleController.tasks.clear()

        loadData(loadCurrentProjectFromSharedPreferences())
    }

    private fun loadData(projectId: Int) {
        if(projectId > -1) {
            ScrumbleController.loadCurrentProject(projectId, {
                image_progress.setProgress(5)
                ScrumbleController.loadTeam(ScrumbleController.currentProject!!.id, {
                    image_progress.setProgress(5, 10)
                    ScrumbleController.loadProjects(ScrumbleController.currentUser.id, {
                        image_progress.setProgress(10, 72)
                        ScrumbleController.loadSprints(ScrumbleController.currentProject!!.id, {
                            image_progress.setProgress(72, 77)
                            ScrumbleController.loadTasks(ScrumbleController.currentProject!!.id, {
                                image_progress.setProgress(77, 100); init(1300)
                            }, { MiscUIController.showError(this, it) })
                        }, { MiscUIController.showError(this, it) })
                    }, { MiscUIController.showError(this, it) })
                }, { MiscUIController.showError(this, it) })
            }, { MiscUIController.showError(this, it) })
        }
        else {
            ScrumbleController.loadProjects(ScrumbleController.currentUser.id, {
                image_progress.setProgress(100)
                init(1300)
            }, { MiscUIController.showError(this, it) })
        }
    }

    private fun init(delay: Long) {
        val handler = Handler()
        handler.postDelayed({
            supportActionBar!!.show()
            image_progress.visibility = View.GONE
            navigation.visibility = View.VISIBLE
            frame_layout.visibility = View.VISIBLE
        }, delay)

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

    private fun loadCurrentProjectFromSharedPreferences(): Int {
        val sharedPreferences = getPreferences(Context.MODE_PRIVATE)
        return sharedPreferences.getInt(ScrumbleController.currentUser.id.toString(), -1)
    }
}
