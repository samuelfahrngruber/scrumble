package com.spogss.scrumble.activity

import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.spogss.scrumble.R
import com.spogss.scrumble.controller.MiscUIController
import com.spogss.scrumble.controller.ScheduleController
import com.spogss.scrumble.controller.ScrumbleController
import com.spogss.scrumble.controller.SharedPreferencesController
import com.spogss.scrumble.fragment.DailyScrumFragment
import com.spogss.scrumble.fragment.MyTasksFragment
import com.spogss.scrumble.fragment.ProjectsFragment
import com.spogss.scrumble.fragment.ScrumBoardFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private val fragments = mutableListOf<Fragment>()

    lateinit var selectedFragment: Fragment

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        var res = true
        when (item.itemId) {
            R.id.navigation_my_tasks -> {
                setupTitle("${ScrumbleController.currentUser.name.capitalize()}'${if (!ScrumbleController.currentUser.name.endsWith("s")) "s" else ""} Tasks")

                if(selectedFragment != fragments[0]) {
                    supportFragmentManager.beginTransaction().hide(selectedFragment).show(fragments[0]).commit();
                    selectedFragment = fragments[0]

                    (selectedFragment as MyTasksFragment).refresh()
                }
            }
            R.id.navigation_scrum_board -> {
                if(selectedFragment != fragments[1]) {
                    supportFragmentManager.beginTransaction().hide(selectedFragment).show(fragments[1]).commit();
                    selectedFragment = fragments[1]
                    setupTitle(resources.getString(R.string.scrum_board))

                    (selectedFragment as ScrumBoardFragment).refresh()
                }
            }
            R.id.navigation_daily_scrum -> {
                if(selectedFragment != fragments[2]) {
                    supportFragmentManager.beginTransaction().hide(selectedFragment).show(fragments[2]).commit();
                    selectedFragment = fragments[2]
                    setupTitle(resources.getString(R.string.daily_scrum))

                    (selectedFragment as DailyScrumFragment).refresh()
                }
            }
            R.id.navigation_project_overview -> {
                if(selectedFragment != fragments[3]) {
                    supportFragmentManager.beginTransaction().hide(selectedFragment).show(fragments[3]).commit();
                    selectedFragment = fragments[3]
                    setupTitle(
                            if (ScrumbleController.currentProject == null)
                                resources.getString(R.string.projects)
                            else
                                ScrumbleController.currentProject!!.name)
                }
            }
            else -> res = false
        }
        /*val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout, selectedFragment)
        transaction.commit()*/

        res
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar!!.hide()

        showTips()

        ScrumbleController.currentProject = null
        ScrumbleController.users.clear()
        ScrumbleController.projects.clear()
        ScrumbleController.sprints.clear()
        ScrumbleController.tasks.clear()

        loadData(SharedPreferencesController.loadCurrentProjectFromSharedPreferences(this))
    }

    private fun showTips() {
        val tips = resources.getStringArray(R.array.tips).toMutableList()
        tips.shuffle()

        fadingTextView.setTexts(tips.toTypedArray())
    }

    private fun loadData(projectId: Int) {
        if(projectId > -1) {
            ScrumbleController.loadCurrentProject(projectId, {
                image_progress.setProgress(5)
                ScrumbleController.loadTeam(ScrumbleController.currentProject!!.id, {
                    image_progress.setProgress(5, 10)
                    ScrumbleController.loadProjects(ScrumbleController.currentUser.id, {
                        image_progress.setProgress(10, 65)
                        ScrumbleController.loadSprints(ScrumbleController.currentProject!!.id, {
                            image_progress.setProgress(65, 72)
                            ScrumbleController.loadTasks(ScrumbleController.currentProject!!.id, {
                                image_progress.setProgress(72, 85)
                                //Handler().postDelayed({ image_progress.setProgress(85, 99) }, 1800)
                                ScrumbleController.loadDailyScrum(ScrumbleController.currentProject!!.id, {
                                    image_progress.setProgress(85, 100)
                                    init(1300)
                                }, { MiscUIController.showError(this, it)})
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
            relative_layout_loading.visibility = View.GONE
            navigation.visibility = View.VISIBLE
            frame_layout.visibility = View.VISIBLE
        }, delay)

        setupFragments()

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        navigation.selectedItemId = R.id.navigation_my_tasks

        ScheduleController.run(this)
    }

    private fun setupFragments() {
        fragments.add(MyTasksFragment())
        fragments.add(ScrumBoardFragment())
        fragments.add(DailyScrumFragment())
        fragments.add(ProjectsFragment())

        supportFragmentManager.beginTransaction().add(R.id.frame_layout, fragments[3], "4").hide(fragments[3]).commit()
        supportFragmentManager.beginTransaction().add(R.id.frame_layout, fragments[2], "3").hide(fragments[2]).commit()
        supportFragmentManager.beginTransaction().add(R.id.frame_layout, fragments[1], "2").hide(fragments[1]).commit()
        supportFragmentManager.beginTransaction().add(R.id.frame_layout, fragments[0], "1").commit()

        selectedFragment = fragments[0]
    }

    private fun setupTitle(newTitle: String) {
        val formattedText = SpannableString(newTitle)
        formattedText.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorAccent)), 0, formattedText.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        title = formattedText
    }
}
