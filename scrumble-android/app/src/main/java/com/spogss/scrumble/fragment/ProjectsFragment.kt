package com.spogss.scrumble.fragment

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.spogss.scrumble.R
import com.spogss.scrumble.activity.LoginActivity
import com.spogss.scrumble.activity.MainActivity
import com.spogss.scrumble.adapter.CustomOverviewHeaderAdapter
import com.spogss.scrumble.controller.*
import com.spogss.scrumble.data.Project
import com.spogss.scrumble.data.Sprint
import com.spogss.scrumble.enums.TaskState
import kotlinx.android.synthetic.main.fragment_projects.*


class ProjectsFragment : Fragment() {
    lateinit var customOverviewHeaderAdapter: CustomOverviewHeaderAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)

        return inflater.inflate(R.layout.fragment_projects, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (ScrumbleController.isCurrentProjectSpecified())
            setupTextViews()

        setupProjectsList()
        setupSpeedDial()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.project, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_change_project -> PopupController.setupRecyclerViewPopup(context!!, {
                SharedPreferencesController.saveCurrentProjectToSharedPreferences(it.id, context as MainActivity)
                (context as MainActivity).finish()
                (context as MainActivity).startActivity((context as MainActivity).intent)
            }, resources.getString(R.string.project), ScrumbleController.projects.filter { it != ScrumbleController.currentProject } as MutableList<Project>)
            R.id.menu_item_refresh -> {
                PopupController.setupConfirmPopup(context!!, context!!.resources.getString(R.string.refresh), context!!.resources.getString(R.string.confirm_refresh)) {
                    val intent = Intent(context, MainActivity::class.java)
                    (context as MainActivity).finish()
                    startActivity(intent)
                }
            }
            R.id.menu_item_logout -> {
                PopupController.setupConfirmPopup(context!!, context!!.resources.getString(R.string.logout), context!!.resources.getString(R.string.confirm_logout)) {
                    val intent = Intent(context, LoginActivity::class.java)
                    intent.putExtra("logout", true)
                    (context as MainActivity).finish()
                    startActivity(intent)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

     fun setupTextViews() {
        if(product_owner_text_view == null) return

        val sprint = ScrumbleController.currentProject!!.currentSprint

        product_owner_text_view.setText(ScrumbleController.currentProject!!.productOwner.toString())

        product_owner_text_view.setOnClickListener {
            PopupController.setupRecyclerViewPopup(context!!, { user ->
                ScrumbleController.currentProject!!.productOwner = user
                product_owner_text_view.setText(user.toString())
                ScrumbleController.updateProject(ScrumbleController.currentProject!!.id, ScrumbleController.currentProject!!,
                        {}, { message -> MiscUIController.showError(context!!, message) })

            }, resources.getString(R.string.product_owner), ScrumbleController.users.filter { user ->
                user.id != ScrumbleController.currentProject!!.productOwner.id
            }.toMutableList())
        }

        if (ScrumbleController.isCurrentSprintSpecified()) {
            sprint_text_view.setText(sprint!!.toNumberString())
            sprint_text_view2.setText(sprint.timeSpan())
        }
        else {
            sprint_text_view.setText("")
            sprint_text_view2.setText("")
        }

        setOnClickListenerToSprintTextView(sprint_text_view)
        setOnClickListenerToSprintTextView(sprint_text_view2)

        sprint_info_image.setOnClickListener {
            if(ScrumbleController.isCurrentSprintSpecified()) {
                PopupController.setupSprintPopup(context!!, { view ->
                    UIToScrumbleController.updateSprint(sprint!!, view, customOverviewHeaderAdapter, context!!)
                    { isCurrent ->
                        if (isCurrent) {
                            sprint_text_view.setText(sprint.toNumberString())
                            sprint_text_view2.setText(sprint.timeSpan())
                            ScrumbleController.currentProject!!.currentSprint = sprint
                        } else {
                            sprint_text_view.setText("")
                            sprint_text_view2.setText("")
                            ScrumbleController.currentProject!!.currentSprint = null
                        }
                        customOverviewHeaderAdapter.notifyDataSetChanged()

                        ScrumbleController.updateProject(ScrumbleController.currentProject!!.id, ScrumbleController.currentProject!!, {}, { message ->
                            MiscUIController.showError(context!!, message)
                        })
                    }
                }, sprint)
            }
        }
    }

    private fun setOnClickListenerToSprintTextView(textView: TextView) {
        textView.setOnClickListener {
            if (ScrumbleController.isCurrentProjectSpecified()) {
                val list = mutableListOf<Any>("no current sprint")
                list.addAll(ScrumbleController.sprints.filter { s -> s != ScrumbleController.currentProject!!.currentSprint })

                PopupController.setupRecyclerViewPopup(context!!, { any ->
                    if (any is Sprint) {
                        ScrumbleController.currentProject!!.currentSprint = any
                        sprint_text_view.setText(any.toNumberString())
                        sprint_text_view2.setText(any.timeSpan())
                    } else {
                        ScrumbleController.currentProject!!.currentSprint = null
                        sprint_text_view.setText("")
                        sprint_text_view2.setText("")
                    }
                    ScrumbleController.updateProject(ScrumbleController.currentProject!!.id, ScrumbleController.currentProject!!, {}, { message ->
                        MiscUIController.showError(context!!, message)
                    })
                }, resources.getString(R.string.set_current_sprint), list)
            }
        }
    }

    private fun setupProjectsList() {
        //list_headers.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        customOverviewHeaderAdapter = CustomOverviewHeaderAdapter(mutableListOf(
                context!!.resources.getString(R.string.team),
                context!!.resources.getString(R.string.sprints),
                context!!.resources.getString(R.string.product_backlog)), ScrumbleController.users, ScrumbleController.sprints,
                ScrumbleController.tasks.filter { it.state == TaskState.PRODUCT_BACKLOG }.toMutableList(), this, context!!)

        list_headers.layoutManager = LinearLayoutManager(context)
        list_headers.adapter = customOverviewHeaderAdapter
    }

    private fun setupSpeedDial() {
        speed_dial.addActionItem(SpeedDialActionItem.Builder(R.id.fab_add_project, R.drawable.sc_nav_projects)
                .setLabel(R.string.project)
                .setLabelColor(ContextCompat.getColor(context!!, R.color.colorAccent))
                .setFabImageTintColor(ContextCompat.getColor(context!!, R.color.colorAccent))
                .create())
        speed_dial.addActionItem(SpeedDialActionItem.Builder(R.id.fab_add_sprint, R.drawable.sc_sprint)
                .setLabel(R.string.sprint)
                .setLabelColor(ContextCompat.getColor(context!!, R.color.colorAccent))
                .setFabImageTintColor(ContextCompat.getColor(context!!, R.color.colorAccent))
                .create())
        speed_dial.addActionItem(SpeedDialActionItem.Builder(R.id.fab_add_team_member, R.drawable.sc_team)
                .setLabel(R.string.team_member)
                .setLabelColor(ContextCompat.getColor(context!!, R.color.colorAccent))
                .setFabImageTintColor(ContextCompat.getColor(context!!, R.color.colorAccent))
                .create())

        speed_dial.setOnActionSelectedListener { actionItem ->
            var retVal = true
            when (actionItem.id) {
                R.id.fab_add_project -> {
                    PopupController.setupProjectPopup(context!!) {
                        speed_dial.close()
                        UIToScrumbleController.addProject(it, view!!, context!!) {
                            (context as MainActivity).finish()
                            (context as MainActivity).startActivity((context as MainActivity).intent)
                        }
                    }
                }
                R.id.fab_add_sprint -> {
                    if (ScrumbleController.isCurrentProjectSpecified())
                        PopupController.setupSprintPopup(context!!, {
                            speed_dial.close()
                            UIToScrumbleController.addSprint(it, view!!, customOverviewHeaderAdapter, context!!) { sprint ->
                                if (ScrumbleController.currentProject!!.currentSprint == sprint) {
                                    sprint_text_view.setText(sprint.toString())
                                    sprint_text_view2.setText(sprint.timeSpan())
                                    customOverviewHeaderAdapter.notifyDataSetChanged()
                                }
                            }
                        })
                    else
                        MiscUIController.showError(context!!, resources.getString(R.string.error_current_project))
                }
                R.id.fab_add_team_member -> {
                    if (ScrumbleController.isCurrentProjectSpecified())
                        PopupController.setupTeamMemberPopup(context!!) {
                            speed_dial.close()
                            UIToScrumbleController.addTeamMember(it, customOverviewHeaderAdapter, context!!)
                        }
                    else
                        MiscUIController.showError(context!!, resources.getString(R.string.error_current_project))
                }
                else -> retVal = false
            }
            retVal
        }
    }
}
