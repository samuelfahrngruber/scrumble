package com.spogss.scrumble.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.spogss.scrumble.R
import com.spogss.scrumble.adapter.CustomDragItemAdapter
import com.spogss.scrumble.controller.MiscUIController
import com.spogss.scrumble.controller.ScrumbleController
import com.spogss.scrumble.data.Project
import com.spogss.scrumble.data.Task
import com.spogss.scrumble.data.User
import com.spogss.scrumble.enums.TaskState
import com.spogss.scrumble.viewItem.CustomDragItem
import com.woxthebox.draglistview.DragListView
import kotlinx.android.synthetic.main.fragment_my_tasks.*


class MyTasksFragment: Fragment() {
    private lateinit var mView: View
    private val items = mutableListOf<Pair<Int, Task>>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_my_tasks, container, false)

        if(ScrumbleController.isCurrentSprintSpecified())
            setupDragListView()
        else
            mView.findViewById<TextView>(R.id.text_view_no_current_project).visibility = View.VISIBLE

        return mView
    }

    fun setupDragListView() {
        val dragListView = mView.findViewById<DragListView>(R.id.drag_list_view)
        dragListView.recyclerView.isVerticalScrollBarEnabled = true
        dragListView.isDragEnabled = true
        dragListView.setLayoutManager(LinearLayoutManager(context))
        dragListView.setCustomDragItem(CustomDragItem(context!!, R.layout.board_view_column_item))

        dragListView.setDragListCallback(object : DragListView.DragListCallback {
            override fun canDragItemAtPosition(dragPosition: Int): Boolean {
                return items[dragPosition].first >= 0
            }

            override fun canDropItemAtPosition(dropPosition: Int): Boolean {
                return dropPosition > 0
            }
        })
        dragListView.setDragListListener(object : DragListView.DragListListener {
            override fun onItemDragEnded(fromPosition: Int, toPosition: Int) {
                if(fromPosition != toPosition) {
                    val task = items[toPosition].second
                    val newState = items[toPosition - 1].second.state
                    val newPosition = items.filter { it.second.state == task.state }.indexOf(items[toPosition]) - 1

                    if(task.state != newState || task.position != toPosition)
                        ScrumbleController.updatePositions(task.position, newPosition, task.state, newState, task.sprint!!)

                    task.state = newState
                    task.position = newPosition

                    ScrumbleController.updateTask(task.id, task, { }, { MiscUIController.showError(context!!, it) })                }
            }

            override fun onItemDragging(itemPosition: Int, x: Float, y: Float) {}
            override fun onItemDragStarted(position: Int) {}

        })

        items.clear()
        val myTasks = ScrumbleController.tasks.filter { it.sprint != null && it.sprint!!.id == ScrumbleController.currentProject!!.currentSprint!!.id }
                .filter { it.responsible == ScrumbleController.currentUser || it.verify == ScrumbleController.currentUser }
        TaskState.values().forEachIndexed { index, taskState ->
            if(taskState != TaskState.PRODUCT_BACKLOG) {
                val tasks = myTasks.filter { it.state == taskState }.toMutableList()
                items.addAll(getDragItems(taskState, (index + 1) * -1, tasks))
            }
        }

        val adapter = CustomDragItemAdapter(items, R.layout.board_view_column_item, R.id.item_layout, true, context!!, this)
        dragListView.setAdapter(adapter, false)
    }

    private fun getDragItems(state: TaskState, headerId: Int, tasks: MutableList<Task>): MutableList<Pair<Int, Task>> {
        val tempUser = User(-1, "", "")
        val tempProject = Project(-1, "", tempUser)
        val tempItems = mutableListOf(Pair(headerId, Task(headerId, tempUser, tempUser,
                state.toString().replace('_', ' '), "", -1,
                state, -1, "", null, tempProject)))

        tasks.sortedBy { it.position }.forEach { tempItems.add(Pair(it.id, it)) }

        return tempItems
    }
}