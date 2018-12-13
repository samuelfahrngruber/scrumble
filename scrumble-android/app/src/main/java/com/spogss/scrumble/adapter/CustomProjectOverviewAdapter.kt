package com.spogss.scrumble.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.rengwuxian.materialedittext.MaterialEditText
import com.spogss.scrumble.R
import com.spogss.scrumble.controller.MiscUIController
import com.spogss.scrumble.controller.PopupController
import com.spogss.scrumble.controller.ScrumbleController
import com.spogss.scrumble.data.Sprint
import com.spogss.scrumble.data.Task
import com.spogss.scrumble.data.User
import com.spogss.scrumble.enums.TaskState
import com.spogss.scrumble.viewItem.CustomSelectableItem
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner

class CustomProjectOverviewAdapter<T>(private val data: MutableList<T>, private val context: Context,
                                      private val customOverviewHeaderAdapter: CustomOverviewHeaderAdapter): RecyclerView.Adapter<CustomProjectOverviewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_list_project_overview, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        when(item) {
            is User -> {
                holder.textView1.text = item.toString()
                holder.textView2.visibility = View.GONE
            }
            is Sprint -> {
                holder.textView1.text = item.toString()
                holder.textView2.text = item.timeSpan()
                holder.textView2.visibility = View.VISIBLE
                holder.rlProjectOverview.setOnClickListener {
                    PopupController.setupSprintPopup(context, { view -> updateSprint(item, view)}, item)
                }
            }
            is Task -> {
                holder.textView1.text = item.toString()
                holder.textView2.visibility = View.GONE
                holder.rlProjectOverview.setOnClickListener {
                    PopupController.setupTaskPopup(context, { view -> updateTask(item, view)}, item)
                }
            }
        }
    }

    private fun updateSprint(sprint: Sprint, customView: View) {
        val sprintNumberEditText = customView.findViewById<MaterialEditText>(R.id.popup_add_sprint_number)
        val selectListTask = customView.findViewById<RecyclerView>(R.id.popup_add_sprint_tasks)

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

        customOverviewHeaderAdapter.backlog.sortBy { it.name }
        customOverviewHeaderAdapter.notifyDataSetChanged()
        notifyDataSetChanged()
    }

    private fun updateTask(task: Task, view: View) {
        val name = view.findViewById<MaterialEditText>(R.id.popup_add_task_name).text.toString()
        val info = view.findViewById<MaterialEditText>(R.id.popup_add_task_info).text.toString()
        val responsible = view.findViewById<MaterialBetterSpinner>(R.id.popup_add_task_responsible).text.toString()
        val verify = view.findViewById<MaterialBetterSpinner>(R.id.popup_add_task_verify).text.toString()
        val color = view.findViewById<TextView>(R.id.popup_add_task_color).tag as String

        val responsibleUser = if(responsible.isNotEmpty()) ScrumbleController.users.find { it.name == responsible }!! else null
        val verifyUser = if(verify.isNotEmpty()) ScrumbleController.users.find { it.name == verify }!! else null

        if(task.name != name || task.info != info || task.color != color || task.responsible != responsibleUser || task.verify != verifyUser) {
            task.name = name
            task.info = info
            task.color = color
            task.responsible = responsibleUser
            task.verify = verifyUser

            ScrumbleController.updateTask(task.id, task, {}, { MiscUIController.showError(context, it) })

            notifyDataSetChanged()
        }
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var rlProjectOverview = v.findViewById<View>(R.id.rl_project_overview) as RelativeLayout
        var textView1 = v.findViewById<View>(R.id.project_overview_text_view) as TextView
        var textView2 = v.findViewById<View>(R.id.project_overview_text_view_2) as TextView
    }
}