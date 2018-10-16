package com.spogss.scrumble.fragment

import android.graphics.PorterDuff
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import android.view.*
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.llollox.androidtoggleswitch.widgets.ToggleSwitch
import com.rengwuxian.materialedittext.MaterialEditText
import com.spogss.scrumble.R
import com.spogss.scrumble.adapter.CustomDragItemAdapter
import com.spogss.scrumble.data.Project
import com.spogss.scrumble.data.Sprint
import com.spogss.scrumble.data.Task
import com.spogss.scrumble.data.User
import com.spogss.scrumble.enums.UserStoryState
import com.spogss.scrumble.viewItem.CustomDragItem
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner
import com.woxthebox.draglistview.BoardView
import de.mrapp.android.dialog.MaterialDialog
import de.mrapp.android.dialog.ScrollableArea
import kotlinx.android.synthetic.main.fragment_scrum_board.*
import java.util.*


class ScrumBoardFragment: Fragment() {
    //TODO: remove when webservice call is possible
    private var users = mutableListOf<User>()
    private var projects = mutableListOf<Project>()
    private var sprints = mutableListOf<Sprint>()
    private var userStories = mutableListOf<Task>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //TODO: remove when webservice call is possible
        createTestData()
        return inflater.inflate(R.layout.fragment_scrum_board, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBoardView()
        fab_add_task.setOnClickListener {
            setupPopup()
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
        addColumn(UserStoryState.SPRINT_BACKLOG)
        addColumn(UserStoryState.IN_PROGRESS)
        addColumn(UserStoryState.TO_VERIFY)
        addColumn(UserStoryState.DONE)
    }

    //TODO: replace with data from webservice
    private fun addColumn(userStoryState: UserStoryState) {
        val items = mutableListOf<Pair<Int, Task>>()
        userStories.filter { it.state == userStoryState &&
                ((it.sprint == null && userStoryState == UserStoryState.SPRINT_BACKLOG) || (it.sprint != null && userStoryState != UserStoryState.SPRINT_BACKLOG)) }
                .sortedBy { task -> task.position }.forEach { items.add(Pair(it.id, it)) }

        val adapter = CustomDragItemAdapter(items, R.layout.board_view_column_item, R.id.item_layout, true, context!!)
        val header = View.inflate(activity, R.layout.board_view_column_header, null)
        (header.findViewById(R.id.column_header_text_view) as TextView).text = userStoryState.toString().replace('_', ' ')
        board_view.addColumn(adapter, header, null, false)
    }

    private fun setupPopup() {
        val dialogBuilder = MaterialDialog.Builder(context!!)
        dialogBuilder.setTitle(R.string.add_task)
        dialogBuilder.setTitleColor(ContextCompat.getColor(context!!, R.color.colorAccent))
        dialogBuilder.setPositiveButton(android.R.string.ok, null)
        dialogBuilder.setNegativeButton(android.R.string.cancel, null)
        dialogBuilder.setButtonTextColor(ContextCompat.getColor(context!!, R.color.colorAccent))
        dialogBuilder.setCanceledOnTouchOutside(false)

        val customView = View.inflate(context, R.layout.popup_add_task, null)

        val toggleSwitch = customView.findViewById<ToggleSwitch>(R.id.popup_add_task_toggle_button)
        toggleSwitch.setCheckedPosition(0)
        //TODO: Disable ToggleSwitch when currentSprint = null

        setupSpinner(customView)

        dialogBuilder.setScrollableArea(ScrollableArea.Area.CONTENT)
        dialogBuilder.setView(customView)

        val dialog = dialogBuilder.create()
        dialog.setOnShowListener {
            dialog.getButton(MaterialDialog.BUTTON_POSITIVE).setOnClickListener { _ ->
                if(dialogButtonClick(customView))
                    dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun setupSpinner(customView: View) {
        val responsibleSpinner = customView.findViewById<MaterialBetterSpinner>(R.id.popup_add_task_responsible)
        responsibleSpinner.setAdapter(ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, users))

        val verifySpinner = customView.findViewById<MaterialBetterSpinner>(R.id.popup_add_task_verify)
        verifySpinner.setAdapter(ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, users))
    }

    private fun dialogButtonClick(customView: View): Boolean {
        val nameEditText = customView.findViewById<MaterialEditText>(R.id.popup_add_task_name)
        val infoEditText = customView.findViewById<MaterialEditText>(R.id.popup_add_task_info)
        val responsibleSpinner = customView.findViewById<MaterialBetterSpinner>(R.id.popup_add_task_responsible)
        val verifySpinner = customView.findViewById<MaterialBetterSpinner>(R.id.popup_add_task_verify)

        var closePopup = true
        if(nameEditText.text.trim().isEmpty()) {
            nameEditText.error = resources.getString(R.string.error_enter_name)
            closePopup = false
        }
        if(infoEditText.text.trim().isEmpty()) {
            infoEditText.error = resources.getString(R.string.error_enter_info)
            closePopup = false
        }
        if(responsibleSpinner.text.trim().isEmpty()) {
            responsibleSpinner.error = resources.getString(R.string.error_select_team_member)
            closePopup = false
        }
        if(verifySpinner.text.trim().isEmpty()) {
            verifySpinner.error = resources.getString(R.string.error_select_team_member)
            closePopup = false
        }

        return closePopup
    }


    //TODO: remove when webservice call is possible
    private fun createTestData() {
        users.clear()
        projects.clear()
        sprints.clear()
        userStories.clear()

        val sam = User(0, "Sam", "entersamspassworthere")
        val pauli = User(1, "Pauli", "phaulson")
        val simon = User(2, "Simon", "hamsterbacke")
        val webi = User(3, "Webi", "livepoolftw")
        val savan = User(4, "Savan", "chelseafcgothistory")
        users.add(sam)
        users.add(pauli)
        users.add(simon)
        users.add(webi)
        users.add(savan)

        val scrumble = Project(0, "Scrumble", sam, mutableListOf(pauli, simon, webi), null)
        val footballsimulator = Project(1, "Football Simulator", savan, mutableListOf(webi), null)
        projects.add(scrumble)
        projects.add(footballsimulator)

        val scrumble1 = Sprint(0, 1, Date(2018, 10, 3), Date(2018, 10, 12), 0)
        val scrumble2 = Sprint(1, 2, Date(2018, 10, 12), Date(2018, 11, 1), 0)
        val scrumble3 = Sprint(2, 3, Date(2018, 11, 3), Date(2018, 11, 22), 0)
        val footballsimulator1 = Sprint(3, 1, Date(2018, 10, 10), Date(2018, 10, 30), 1)
        val footballsimulator2 = Sprint(4, 2, Date(2018, 11, 1), Date(2018, 11, 15), 1)
        sprints.add(scrumble1)
        sprints.add(scrumble2)
        sprints.add(scrumble3)
        sprints.add(footballsimulator1)
        sprints.add(footballsimulator2)

        val scrumbleGUI = Task(0, pauli, sam, "GUI Programmieren", "Hierbei handelt es sich um die Android GUI",
                0, UserStoryState.IN_PROGRESS, 1, 0, 0)
        val scrumbleDB = Task(1, webi, simon, "Datenbank aufsetzten", "Inklusive Trigger, Sequenzen usw...",
                0, UserStoryState.TO_VERIFY, 1, 0, 0)
        val scrumbleWebservice = Task(2, webi, pauli, "Webservice implementieren", "Der Webservice connected Client mit Datenbank",
                0, UserStoryState.SPRINT_BACKLOG, 1, null, 0)
        val scrumbleTesting = Task(3, simon, sam, "Testen", "Sollen wir Unit-Tests machen? Keine Ahnung",
                0, UserStoryState.SPRINT_BACKLOG, 2, null, 0)
        val scrumblePlaning = Task(4, sam, pauli, "Planung", "Geplant wird immer, weil wir mit Scrum arbeiten, lol. Hier steht auch noch mehr Text",
                0, UserStoryState.IN_PROGRESS, 2, 0, 0)
        userStories.add(scrumbleGUI)
        userStories.add(scrumbleDB)
        userStories.add(scrumbleWebservice)
        userStories.add(scrumbleTesting)
        userStories.add(scrumblePlaning)
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