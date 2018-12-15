package com.spogss.scrumble.controller

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.llollox.androidtoggleswitch.widgets.ToggleSwitch
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.rengwuxian.materialedittext.MaterialEditText
import com.spogss.scrumble.R
import com.spogss.scrumble.activity.MainActivity
import com.spogss.scrumble.adapter.CustomOverviewHeaderAdapter
import com.spogss.scrumble.data.Project
import com.spogss.scrumble.data.Sprint
import com.spogss.scrumble.data.Task
import com.spogss.scrumble.data.User
import com.spogss.scrumble.enums.TaskState
import com.spogss.scrumble.viewItem.CustomSelectableItem
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner
import com.woxthebox.draglistview.DragListView

object UIToScrumbleController {
    fun addTask(view: View, mainView: View, context: Context, callback: () -> Unit) {
        val checkedPosition = view.findViewById<ToggleSwitch>(R.id.popup_add_task_toggle_button).checkedPosition!!
        val name = view.findViewById<MaterialEditText>(R.id.popup_add_task_name).text.toString()
        val info = view.findViewById<MaterialEditText>(R.id.popup_add_task_info).text.toString()
        val responsible = view.findViewById<MaterialBetterSpinner>(R.id.popup_add_task_responsible).text.toString()
        val verify = view.findViewById<MaterialBetterSpinner>(R.id.popup_add_task_verify).text.toString()
        val color = view.findViewById<TextView>(R.id.popup_add_task_color).tag as String

        val responsibleUser = if(responsible.isNotEmpty()) ScrumbleController.users.find { it.name == responsible }!! else null
        val verifyUser = if(verify.isNotEmpty()) ScrumbleController.users.find { it.name == verify }!! else null
        val state = if(checkedPosition == 0) TaskState.PRODUCT_BACKLOG else TaskState.SPRINT_BACKLOG
        val sprint = if(checkedPosition == 0) null else ScrumbleController.currentProject!!.currentSprint
        val position = if(checkedPosition != 0) {
            val maxPosTask = ScrumbleController.tasks.filter { it.sprint != null && it.sprint!!.id == ScrumbleController.currentProject!!.currentSprint!!.id && it.state == TaskState.SPRINT_BACKLOG }.maxBy { it.position }
            maxPosTask?.position ?: 0
        }
        else 0

        val task = Task(-1, responsibleUser, verifyUser, name, info, 0, state, position, color, sprint, ScrumbleController.currentProject!!)

        MiscUIController.startLoadingAnimation(mainView, context)
        ScrumbleController.addTask(task, {
            task.id = it
            ScrumbleController.tasks.add(task)
            if(checkedPosition != 0)
                callback()

            MiscUIController.stopLoadingAnimation(mainView, context)
        }, {
            MiscUIController.stopLoadingAnimation(mainView, context)
            MiscUIController.showError(context, it)
        })
    }
    fun updateTask(task: Task, view: View, context: Context, callback: () -> Unit) {
        val name = view.findViewById<MaterialEditText>(R.id.popup_add_task_name).text.toString()
        val info = view.findViewById<MaterialEditText>(R.id.popup_add_task_info).text.toString()
        val responsible = view.findViewById<MaterialBetterSpinner>(R.id.popup_add_task_responsible).text.toString()
        val verify = view.findViewById<MaterialBetterSpinner>(R.id.popup_add_task_verify).text.toString()
        val rejections = view.findViewById<MaterialEditText>(R.id.popup_add_task_rejections).text.toString().toInt()
        val color = view.findViewById<TextView>(R.id.popup_add_task_color).tag as String

        val responsibleUser = if(responsible.isNotEmpty()) ScrumbleController.users.find { it.name == responsible }!! else null
        val verifyUser = if(verify.isNotEmpty()) ScrumbleController.users.find { it.name == verify }!! else null

        if(task.name != name || task.info != info || task.color != color || task.responsible != responsibleUser
                || task.verify != verifyUser || task.rejections != rejections) {
            task.name = name
            task.info = info
            task.color = color
            task.responsible = responsibleUser
            task.verify = verifyUser
            task.rejections = rejections

            ScrumbleController.updateTask(task.id, task, {}, { MiscUIController.showError(context, it) } )

            callback()
        }
    }

    fun addSprint(view: View, mainView: View, customOverviewHeaderAdapter: CustomOverviewHeaderAdapter, context: Context) {
        val sprintNumber = view.findViewById<MaterialEditText>(R.id.popup_add_sprint_number).text.toString().toInt()
        val selectListTask = view.findViewById<RecyclerView>(R.id.popup_add_sprint_tasks)

        val sprint = Sprint(-1, sprintNumber, PopupController.startCal.time, PopupController.endCal.time, ScrumbleController.currentProject!!)

        var maxPos = if(ScrumbleController.isCurrentSprintSpecified())
            ScrumbleController.tasks.filter { it.sprint != null &&
                    it.sprint!!.id == ScrumbleController.currentProject!!.currentSprint!!.id &&
                    it.state == TaskState.SPRINT_BACKLOG }.maxBy { it.position }?.position ?: -1
        else -1

        val tasksToAdd = (selectListTask.adapter as FastItemAdapter<CustomSelectableItem>).adapterItems
                .filter { it.isSelected }.map { it.task!!.position = ++maxPos; it.task!! }

        MiscUIController.startLoadingAnimation(mainView, context)
        ScrumbleController.addSprint(sprint, {id ->
            sprint.id = id
            ScrumbleController.sprints.add(sprint)
            customOverviewHeaderAdapter.notifyDataSetChanged()

            tasksToAdd.forEach { task ->
                task.state = TaskState.SPRINT_BACKLOG
                task.sprint = sprint
                customOverviewHeaderAdapter.backlog.remove(task)
                ScrumbleController.updateTask(task.id, task, { }, { MiscUIController.showError(context, it) })
            }
            MiscUIController.stopLoadingAnimation(mainView, context)
        }, {
            MiscUIController.stopLoadingAnimation(mainView, context)
            MiscUIController.showError(context, it)
        })
    }
    fun updateSprint(sprint: Sprint, view: View, customOverviewHeaderAdapter: CustomOverviewHeaderAdapter, context: Context, callback: () -> Unit) {
        val sprintNumberEditText = view.findViewById<MaterialEditText>(R.id.popup_add_sprint_number)
        val selectListTask = view.findViewById<RecyclerView>(R.id.popup_add_sprint_tasks)

        sprint.number = sprintNumberEditText.text.toString().toInt()
        sprint.startDate = PopupController.startCal.time
        sprint.deadline = PopupController.endCal.time

        var maxPos = ScrumbleController.tasks.filter { it.sprint != null &&  it.sprint!!.id == sprint.id &&
                it.state == TaskState.SPRINT_BACKLOG }.maxBy { it.position }?.position ?: -1

        val tasksToAUpdate = (selectListTask.adapter as FastItemAdapter<CustomSelectableItem>).adapterItems
                .filter { it.isSelected && it.task!!.sprint == null || !it.isSelected && it.task!!.sprint != null }
                .map {
                    if(it.isSelected) {
                        it.task!!.sprint = sprint
                        it.task.state = TaskState.SPRINT_BACKLOG
                        it.task.position = ++maxPos
                        customOverviewHeaderAdapter.backlog.remove(it.task)
                    }
                    else {
                        it.task!!.sprint = null
                        it.task.state = TaskState.PRODUCT_BACKLOG
                        it.task.position = 0
                        customOverviewHeaderAdapter.backlog.add(it.task)
                    }
                    it.task
                }

        ScrumbleController.updateSprint(sprint.id, sprint, {}, { MiscUIController.showError(context, it) })
        tasksToAUpdate.forEach { task ->
            ScrumbleController.updateTask(task!!.id, task, {}, { MiscUIController.showError(context, it) })
        }

        callback()
    }

    fun addProject(customView: View, mainView: View, context: Context, callback: () -> Unit) {
        val name = customView.findViewById<MaterialEditText>(R.id.popup_add_project_name).text.toString()
        val productOwner = ScrumbleController.currentUser
        val team = customView.findViewById<DragListView>(R.id.swipe_list_add_team_member).adapter.itemList
                .map { (it as Pair<Int, User>).second }.toMutableList()
        team.add(ScrumbleController.currentUser)

        val project = Project(-1, name, productOwner)
        MiscUIController.startLoadingAnimation(mainView, context)
        ScrumbleController.addProject(project, {id ->
            project.id = id

            ScrumbleController.addTeamMembers(project.id, team, {
                SharedPreferencesController.saveCurrentProjectToSharedPreferences(project.id, context as MainActivity)
                callback()
            }, {
                MiscUIController.showError(context, it)
                MiscUIController.stopLoadingAnimation(mainView, context)
            })
        }, {
            MiscUIController.showError(context, it)
            MiscUIController.startLoadingAnimation(mainView, context)
        })
    }

    fun addTeamMember(view: View, customOverviewHeaderAdapter: CustomOverviewHeaderAdapter, context: Context) {
        val team = view.findViewById<DragListView>(R.id.swipe_list_add_team_member).adapter.itemList
                .map { (it as Pair<Int, User>).second }.toMutableList()

        ScrumbleController.users.addAll(team)
        ScrumbleController.users.sortBy { it.name }
        customOverviewHeaderAdapter.notifyDataSetChanged()

        ScrumbleController.addTeamMembers(ScrumbleController.currentProject!!.id, team, {}, { MiscUIController.showError(context, it) })
    }
}