package com.spogss.scrumble.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.spogss.scrumble.R
import com.spogss.scrumble.adapter.CustomDragItemAdapter
import com.spogss.scrumble.controller.PopupController
import com.spogss.scrumble.controller.ScrumbleController
import com.spogss.scrumble.data.Task
import com.spogss.scrumble.enums.TaskState
import com.spogss.scrumble.viewItem.CustomDragItem
import com.woxthebox.draglistview.BoardView


class ScrumBoardFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_scrum_board, container, false)

        setupBoardView(view)

        val fab = view.findViewById<FloatingActionButton>(R.id.fab_add_task)
        fab.setOnClickListener {
            PopupController.setupTaskPopup(context!!, {})
        }
        return view
    }

    private fun setupBoardView(view: View) {
        val boardView = view.findViewById<BoardView>(R.id.board_view)
        boardView.setSnapToColumnsWhenScrolling(true)
        boardView.setSnapToColumnWhenDragging(true)
        boardView.setSnapDragItemToTouch(true)
        boardView.setCustomDragItem(CustomDragItem(context!!, R.layout.board_view_column_item))
        boardView.setSnapToColumnInLandscape(false)
        boardView.setColumnSnapPosition(BoardView.ColumnSnapPosition.CENTER)

        boardView.setBoardListener(ColumnChangeListener())

        //TODO: remove when webservice call is possible
        addColumn(TaskState.SPRINT_BACKLOG, boardView)
        addColumn(TaskState.IN_PROGRESS, boardView)
        addColumn(TaskState.TO_VERIFY, boardView)
        addColumn(TaskState.DONE, boardView)
    }

    //TODO: replace with data from webservice
    private fun addColumn(taskState: TaskState, boardView: BoardView) {
        val items = mutableListOf<Pair<Int, Task>>()
        ScrumbleController.tasks.filter { it.sprint != null && it.sprint!!.id == ScrumbleController.currentProject!!.currentSprint!!.id && it.state == taskState  }
                .sortedBy { it.position }.forEach { items.add(Pair(it.id, it)) }

        val adapter = CustomDragItemAdapter(items, R.layout.board_view_column_item, R.id.item_layout, true, context!!)
        val header = View.inflate(activity, R.layout.board_view_column_header, null)
        (header.findViewById(R.id.column_header_text_view) as TextView).text = taskState.toString().replace('_', ' ')
        boardView.addColumn(adapter, header, null, false)
    }

    inner class ColumnChangeListener: BoardView.BoardListener {
        override fun onItemDragEnded(fromColumn: Int, fromRow: Int, toColumn: Int, toRow: Int){
            if(fromColumn == toColumn && fromRow != toRow)
                Toast.makeText(context!!, "new row: $toRow", Toast.LENGTH_SHORT).show()
            else if(fromColumn != toColumn)
                Toast.makeText(context!!, "new column: $toColumn, row: $toRow", Toast.LENGTH_SHORT).show()
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