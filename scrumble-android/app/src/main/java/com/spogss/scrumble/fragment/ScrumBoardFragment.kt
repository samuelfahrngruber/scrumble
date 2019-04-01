package com.spogss.scrumble.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.spogss.scrumble.R
import com.spogss.scrumble.adapter.CustomDragItemAdapter
import com.spogss.scrumble.controller.MiscUIController
import com.spogss.scrumble.controller.PopupController
import com.spogss.scrumble.controller.ScrumbleController
import com.spogss.scrumble.controller.UIToScrumbleController
import com.spogss.scrumble.data.Task
import com.spogss.scrumble.enums.TaskState
import com.spogss.scrumble.viewItem.CustomDragItem
import com.woxthebox.draglistview.BoardView
import kotlinx.android.synthetic.main.fragment_my_tasks.*
import kotlinx.android.synthetic.main.fragment_scrum_board.*


class ScrumBoardFragment: Fragment() {
    private lateinit var mView: View
    private val items: MutableList<MutableList<Pair<Int, Task>>> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        mView = inflater.inflate(R.layout.fragment_scrum_board, container, false)

        val noCurrentTextView = mView.findViewById<TextView>(R.id.text_view_no_current_project)
        if(ScrumbleController.isCurrentProjectSpecified()) {
            if (ScrumbleController.isCurrentSprintSpecified()) {
                noCurrentTextView.visibility = View.GONE
                setupBoardView()
            }
            else {
                noCurrentTextView.visibility = View.VISIBLE
                noCurrentTextView.text = resources.getString(R.string.error_no_current_sprint)
            }
        }
        else noCurrentTextView.visibility = View.VISIBLE

        val fab = mView.findViewById(R.id.fab_add_task) as FloatingActionButton
        fab.setOnClickListener {
            if(ScrumbleController.isCurrentProjectSpecified()) {
                PopupController.setupTaskPopup(context!!, { view ->
                    UIToScrumbleController.addTask(view, mView, context!!) { setupBoardView() }
                }, {})
            }
            else
                MiscUIController.showError(context!!, resources.getString(R.string.error_current_project))
        }

        return mView
    }

    private fun setupBoardView() {
        val boardView = mView.findViewById(R.id.board_view) as BoardView
        val noCurrentTextView = mView.findViewById<TextView>(R.id.text_view_no_current_project)

        if (ScrumbleController.isCurrentSprintSpecified())
            noCurrentTextView.visibility = View.GONE
        else {
            boardView.visibility = View.GONE
            noCurrentTextView.visibility = View.VISIBLE
            noCurrentTextView.text = resources.getString(R.string.error_no_current_sprint)
            return
        }

        boardView.setSnapToColumnsWhenScrolling(true)
        boardView.setSnapToColumnWhenDragging(true)
        boardView.setSnapDragItemToTouch(true)
        boardView.setCustomDragItem(CustomDragItem(context!!, R.layout.board_view_column_item))
        boardView.setSnapToColumnInLandscape(false)
        boardView.setColumnSnapPosition(BoardView.ColumnSnapPosition.CENTER)

        boardView.setBoardListener(ColumnChangeListener())

        boardView.clearBoard()
        items.clear()
        addColumn(TaskState.SPRINT_BACKLOG, boardView)
        addColumn(TaskState.IN_PROGRESS, boardView)
        addColumn(TaskState.TO_VERIFY, boardView)
        addColumn(TaskState.DONE, boardView)
    }

    private fun addColumn(taskState: TaskState, boardView: BoardView) {
        val tempItems = mutableListOf<Pair<Int, Task>>()
        ScrumbleController.tasks.filter { it.sprint != null && it.sprint!!.id == ScrumbleController.currentProject!!.currentSprint!!.id && it.state == taskState  }
                .sortedBy { it.position }.forEach { tempItems.add(Pair(it.id, it)) }
        items.add(tempItems)

        val adapter = CustomDragItemAdapter(tempItems, R.layout.board_view_column_item, R.id.item_layout, true, context!!, this)
        val header = View.inflate(context, R.layout.board_view_column_header, null)
        (header.findViewById(R.id.column_header_text_view) as TextView).text = taskState.toString().replace('_', ' ')
        boardView.addColumn(adapter, header, null, false)
    }

    fun refresh() {
        val boardView = mView.findViewById(R.id.board_view) as BoardView

        items.clear()
        for(idx in 0 until boardView.columnCount) {
            val tempItems = mutableListOf<Pair<Int, Task>>()
            ScrumbleController.tasks.filter { it.sprint != null && it.sprint!!.id == ScrumbleController.currentProject!!.currentSprint!!.id && it.state == TaskState.values()[idx + 1] }
                    .sortedBy { it.position }.forEach { tempItems.add(Pair(it.id, it)) }
            items.add(tempItems)

            boardView.getAdapter(idx).itemList = tempItems.toList()
            boardView.getAdapter(idx).notifyDataSetChanged()
        }
    }

    inner class ColumnChangeListener: BoardView.BoardListener {
        override fun onItemDragEnded(fromColumn: Int, fromRow: Int, toColumn: Int, toRow: Int){
            if(fromRow != toRow || fromColumn != toColumn) {
                val task = items[toColumn][toRow].second
                val newState = TaskState.values()[toColumn + 1]

                if(task.state != newState || task.position != toRow)
                    ScrumbleController.updatePositions(task.position, toRow, task.state, newState, task.sprint!!)

                task.state = newState
                task.position = toRow

                ScrumbleController.updateTask(task.id, task, { }, { MiscUIController.showError(context!!, it) })
            }
        }

        override fun onItemDragStarted(column: Int, row: Int) {}

        override fun onItemChangedPosition(oldColumn: Int, oldRow: Int, newColumn: Int, newRow: Int) {}

        override fun onColumnDragStarted(position: Int) {}

        override fun onColumnDragEnded(position: Int) {}

        override fun onColumnDragChangedPosition(oldPosition: Int, newPosition: Int){}

        override fun onFocusedColumnChanged(oldColumn: Int, newColumn: Int){}

        override fun onItemChangedColumn(oldColumn: Int, newColumn: Int) {}
    }
}