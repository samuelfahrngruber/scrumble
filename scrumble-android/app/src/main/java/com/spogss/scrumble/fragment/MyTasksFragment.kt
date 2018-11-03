package com.spogss.scrumble.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.spogss.scrumble.R
import com.spogss.scrumble.adapter.CustomDragItemAdapter
import com.spogss.scrumble.controller.ScrumbleController
import com.spogss.scrumble.data.Project
import com.spogss.scrumble.data.Task
import com.spogss.scrumble.data.User
import com.spogss.scrumble.enums.TaskState
import com.spogss.scrumble.viewItem.CustomDragItem
import com.woxthebox.draglistview.DragListView


class MyTasksFragment: Fragment() {
    private val items = mutableListOf<Pair<Int, Task>>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_my_tasks, container, false)

        setupDragListView(view)

        return view
    }

    private fun setupDragListView(view: View) {
        val dragListView = view.findViewById<DragListView>(R.id.drag_list_view)
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
                    task.state = items[toPosition - 1].second.state
                    task.position = items.filter { it.second.state == task.state }.indexOf(items[toPosition])

                    Toast.makeText(context!!, "new state: ${task.state}, new position: ${task.position}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onItemDragging(itemPosition: Int, x: Float, y: Float) {}
            override fun onItemDragStarted(position: Int) {}

        })

        items.clear()
        val myTasks = ScrumbleController.tasks.filter { it.sprint != null && it.sprint!!.id == ScrumbleController.currentProject!!.currentSprint!!.id }
                .filter { it.responsible.id == ScrumbleController.currentUser.id || it.verify.id == ScrumbleController.currentUser.id }
        TaskState.values().forEachIndexed { index, taskState ->
            if(taskState != TaskState.PRODUCT_BACKLOG) {
                val tasks = myTasks.filter { it.state == taskState }.toMutableList()
                items.addAll(getDragItems(taskState, (index + 1) * -1, tasks))
            }
        }

        val adapter = CustomDragItemAdapter(items, R.layout.board_view_column_item, R.id.item_layout, true, context!!)
        dragListView.setAdapter(adapter, false)
    }

    private fun getDragItems(state: TaskState, headerId: Int, tasks: MutableList<Task>): MutableList<Pair<Int, Task>> {
        val tempUser = User(-1, "", "")
        val tempProject = Project(-1, "", tempUser)
        val tempItems = mutableListOf(Pair(headerId, Task(headerId, tempUser, tempUser,
                state.toString().replace('_', ' '), "", -1,
                state, -1, null, tempProject)))

        tasks.sortedBy { it.position }.forEach { tempItems.add(Pair(it.id, it)) }

        return tempItems
    }
}