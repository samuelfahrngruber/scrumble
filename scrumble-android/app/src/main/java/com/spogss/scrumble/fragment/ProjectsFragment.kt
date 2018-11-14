package com.spogss.scrumble.fragment

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.rengwuxian.materialedittext.MaterialEditText
import com.spogss.scrumble.R
import com.spogss.scrumble.activity.MainActivity
import com.spogss.scrumble.adapter.CustomOverviewHeaderAdapter
import com.spogss.scrumble.controller.MiscUIController
import com.spogss.scrumble.controller.PopupController
import com.spogss.scrumble.controller.ScrumbleController
import com.spogss.scrumble.data.Sprint
import com.spogss.scrumble.enums.TaskState
import com.spogss.scrumble.viewItem.CustomSelectableItem
import com.woxthebox.draglistview.DragListView
import kotlinx.android.synthetic.main.fragment_projects.*


class ProjectsFragment: Fragment() {
    private lateinit var customOverviewHeaderAdapter: CustomOverviewHeaderAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)

        return inflater.inflate(R.layout.fragment_projects, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(ScrumbleController.isCurrentProjectSpecified())
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
            R.id.menu_item_change_project -> PopupController.setupRecyclerViewPopup(context!!, {
                saveCurrentProjectToSharedPreferences(it.id)
                (context as MainActivity).finish()
                (context as MainActivity).startActivity((context as MainActivity).intent)
            }, resources.getString(R.string.project), ScrumbleController.projects)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupTextViews() {
        product_owner_text_view.setText(ScrumbleController.currentProject!!.productOwner.toString())
        product_owner_text_view.setOnClickListener {
            PopupController.setupRecyclerViewPopup(context!!, { user ->
                Toast.makeText(context, user.toString(), Toast.LENGTH_SHORT).show()
            }, resources.getString(R.string.product_owner), ScrumbleController.users.filter { user ->
                user.id != ScrumbleController.currentProject!!.productOwner.id }.toMutableList())
        }

        if(ScrumbleController.isCurrentSprintSpecified()) {
            sprint_text_view.setText(ScrumbleController.currentProject!!.currentSprint!!.toString())
            sprint_text_view2.setText(ScrumbleController.currentProject!!.currentSprint!!.timeSpan())

            sprint_text_view.setOnClickListener { PopupController.setupSprintPopup(context!!, {}, ScrumbleController.currentProject!!.currentSprint!!) }
            sprint_text_view2.setOnClickListener { PopupController.setupSprintPopup(context!!, {}, ScrumbleController.currentProject!!.currentSprint!!) }
        }
    }

    private fun setupProjectsList() {
        //list_headers.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        customOverviewHeaderAdapter = CustomOverviewHeaderAdapter(mutableListOf(
                context!!.resources.getString(R.string.team),
                context!!.resources.getString(R.string.sprints),
                context!!.resources.getString(R.string.product_backlog)), ScrumbleController.users, ScrumbleController.sprints,
                ScrumbleController.tasks.filter { it.state == TaskState.PRODUCT_BACKLOG }.toMutableList(), context!!)

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
            when(actionItem.id) {
                R.id.fab_add_project -> {
                    PopupController.setupProjectPopup(context!!) {
                        speed_dial.close()
                        addProject(it)
                    }
                }
                R.id.fab_add_sprint -> {
                    if(ScrumbleController.isCurrentProjectSpecified())
                        PopupController.setupSprintPopup(context!!, {
                            speed_dial.close()
                            addSprint(it)
                        })
                    else
                        MiscUIController.showError(context!!, resources.getString(R.string.error_current_project))
                }
                R.id.fab_add_team_member -> {
                    if(ScrumbleController.isCurrentProjectSpecified())
                        PopupController.setupTeamMemberPopup(context!!) { speed_dial.close() }
                    else
                        MiscUIController.showError(context!!, resources.getString(R.string.error_current_project))
                }
                else -> retVal = false
            }
            retVal
        }
    }

    private fun addProject(customView: View) {
        val nameEditText = customView.findViewById<MaterialEditText>(R.id.popup_add_project_name)
        val productOwnerEditText = customView.findViewById<MaterialEditText>(R.id.popup_add_project_product_owner)
        val listView = customView.findViewById<DragListView>(R.id.swipe_list_add_team_member)
    }

    private fun addSprint(customView: View) {
        val sprintNumberEditText = customView.findViewById<MaterialEditText>(R.id.popup_add_sprint_number)
        val selectListTask = customView.findViewById<RecyclerView>(R.id.popup_add_sprint_tasks)

        val sprint = Sprint(-1, sprintNumberEditText.text.toString().toInt(), PopupController.startCal.time, PopupController.endCal.time, ScrumbleController.currentProject!!)

        var maxPos = if(ScrumbleController.isCurrentSprintSpecified())
            ScrumbleController.tasks.filter { it.sprint != null &&
                it.sprint!!.id == ScrumbleController.currentProject!!.currentSprint!!.id &&
                it.state == TaskState.SPRINT_BACKLOG }.maxBy { it.position }?.position ?: -1
            else -1

        val tasksToAdd = (selectListTask.adapter as FastItemAdapter<CustomSelectableItem>).adapterItems
                .filter { it.isSelected }.map { it.task!!.position = ++maxPos; it.task!! }

        MiscUIController.startLoadingAnimation(view!!, context!!)
        ScrumbleController.addSprint(sprint, {id ->
            sprint.id = id
            ScrumbleController.sprints.add(sprint)
            customOverviewHeaderAdapter.notifyDataSetChanged()

            tasksToAdd.forEach { task ->
                task.state = TaskState.SPRINT_BACKLOG
                task.sprint = sprint
                customOverviewHeaderAdapter.backlog.remove(task)
                ScrumbleController.updateTask(task.id, task, { }, { MiscUIController.showError(context!!, it) })
            }
            MiscUIController.stopLoadingAnimation(view!!, context!!)
        }, {
            MiscUIController.stopLoadingAnimation(view!!, context!!)
            MiscUIController.showError(context!!, it)
        })
    }

    private fun saveCurrentProjectToSharedPreferences(projectId: Int) {
        val sharedPreferences = (context as MainActivity).getPreferences(Context.MODE_PRIVATE)
        sharedPreferences.edit().putInt(ScrumbleController.currentUser.id.toString(), projectId).apply()
    }
}
