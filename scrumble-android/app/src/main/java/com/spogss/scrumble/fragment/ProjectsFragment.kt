package com.spogss.scrumble.fragment

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.spogss.scrumble.R
import com.spogss.scrumble.adapter.CustomOverviewHeaderAdapter
import com.spogss.scrumble.controller.PopupController
import com.spogss.scrumble.controller.ScrumbleController
import com.spogss.scrumble.enums.TaskState
import kotlinx.android.synthetic.main.fragment_projects.*


class ProjectsFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)

        return inflater.inflate(R.layout.fragment_projects, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTextViews()
        setupProjectsList()
        setupSpeedDial()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.change_project, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_item_change_project -> PopupController.setupRecyclerViewPopup(context!!, { Toast.makeText(context, it.name, Toast.LENGTH_SHORT).show() }, resources.getString(R.string.project), ScrumbleController.projects)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupTextViews() {
        product_owner_text_view.setText(ScrumbleController.currentProject!!.productOwner.toString())
        sprint_text_view.setText(ScrumbleController.currentProject!!.currentSprint!!.toString())
        sprint_text_view2.setText(ScrumbleController.currentProject!!.currentSprint!!.timeSpan())

        product_owner_text_view.setOnClickListener { PopupController.setupRecyclerViewPopup(context!!, { Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show() }, resources.getString(R.string.product_owner), ScrumbleController.users)}
        sprint_text_view.setOnClickListener { PopupController.setupSprintPopup(context!!, {}, ScrumbleController.currentProject!!.currentSprint!!) }
        sprint_text_view2.setOnClickListener { PopupController.setupSprintPopup(context!!, {}, ScrumbleController.currentProject!!.currentSprint!!) }
    }

    private fun setupProjectsList() {
        //list_headers.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        list_headers.layoutManager = LinearLayoutManager(context)
        list_headers.adapter = CustomOverviewHeaderAdapter(mutableListOf(
                context!!.resources.getString(R.string.team),
                context!!.resources.getString(R.string.sprints),
                context!!.resources.getString(R.string.product_backlog)), ScrumbleController.users, ScrumbleController.sprints,
                ScrumbleController.tasks.filter { it.state == TaskState.PRODUCT_BACKLOG }.toMutableList(), context!!)
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
            when(actionItem.id) {
                R.id.fab_add_project -> {
                    PopupController.setupProjectPopup(context!!) { speed_dial.close() }
                }
                R.id.fab_add_sprint -> {
                    //TODO: check if there is a current project
                    PopupController.setupSprintPopup(context!!, { speed_dial.close() })
                }
                R.id.fab_add_team_member -> {
                    //TODO: check if there is a current project
                    PopupController.setupTeamMemberPopup(context!!) { speed_dial.close() }
                }
                else -> retVal = false
            }
            retVal
        }
    }
}
