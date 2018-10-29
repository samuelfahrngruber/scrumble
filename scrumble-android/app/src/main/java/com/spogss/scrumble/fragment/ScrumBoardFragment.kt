package com.spogss.scrumble.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.llollox.androidtoggleswitch.widgets.ToggleSwitch
import com.rengwuxian.materialedittext.MaterialEditText
import com.spogss.scrumble.R
import com.spogss.scrumble.adapter.CustomDragItemAdapter
import com.spogss.scrumble.controller.PopupController
import com.spogss.scrumble.controller.ScrumbleController
import com.spogss.scrumble.data.Task
import com.spogss.scrumble.enums.TaskState
import com.spogss.scrumble.viewItem.CustomDragItem
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner
import com.woxthebox.draglistview.BoardView
import de.mrapp.android.dialog.MaterialDialog
import de.mrapp.android.dialog.ScrollableArea
import kotlinx.android.synthetic.main.fragment_scrum_board.*


class ScrumBoardFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_scrum_board, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBoardView()
        fab_add_task.setOnClickListener {
            PopupController.setupTaskPopup(context!!, {})
        }
    }

    private fun setupBoardView() {
        board_view.setSnapToColumnsWhenScrolling(true)
        board_view.setSnapToColumnWhenDragging(true)
        board_view.setSnapDragItemToTouch(true)
        board_view.setCustomDragItem(CustomDragItem(context!!, R.layout.board_view_column_item))
        board_view.setSnapToColumnInLandscape(false)
        board_view.setColumnSnapPosition(BoardView.ColumnSnapPosition.CENTER)

        board_view.setBoardListener(ColumnChangeListener())

        //TODO: remove when webservice call is possible
        addColumn(TaskState.SPRINT_BACKLOG)
        addColumn(TaskState.IN_PROGRESS)
        addColumn(TaskState.TO_VERIFY)
        addColumn(TaskState.DONE)
    }

    //TODO: replace with data from webservice
    private fun addColumn(taskState: TaskState) {
        val items = mutableListOf<Pair<Int, Task>>()
        ScrumbleController.tasks.filter { it.state == taskState  }
                .sortedBy { task -> task.position }.forEach { items.add(Pair(it.id, it)) }

        val adapter = CustomDragItemAdapter(items, R.layout.board_view_column_item, R.id.item_layout, true, context!!)
        val header = View.inflate(activity, R.layout.board_view_column_header, null)
        (header.findViewById(R.id.column_header_text_view) as TextView).text = taskState.toString().replace('_', ' ')
        board_view.addColumn(adapter, header, null, false)
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