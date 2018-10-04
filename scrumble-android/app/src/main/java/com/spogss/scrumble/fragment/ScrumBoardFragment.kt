package com.spogss.scrumble.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.spogss.scrumble.R
import com.spogss.scrumble.adapter.CustomDragItemAdapter
import com.spogss.scrumble.data.Project
import com.spogss.scrumble.data.Sprint
import com.spogss.scrumble.data.User
import com.spogss.scrumble.data.UserStory
import com.spogss.scrumble.enums.UserStoryState
import com.spogss.scrumble.viewItem.CustomDragItem
import com.woxthebox.draglistview.BoardView
import kotlinx.android.synthetic.main.fragment_scrum_board.*
import java.util.*


class ScrumBoardFragment: Fragment() {
    //for testing
    private var users = mutableListOf<User>()
    private var projects = mutableListOf<Project>()
    private var sprints = mutableListOf<Sprint>()
    private var userStories = mutableListOf<UserStory>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        createTestData()
        return inflater.inflate(R.layout.fragment_scrum_board, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBoardView()
    }

    private fun setupBoardView() {
        board_view_scrum_board.setSnapToColumnsWhenScrolling(true)
        board_view_scrum_board.setSnapToColumnWhenDragging(true)
        board_view_scrum_board.setSnapDragItemToTouch(true)
        board_view_scrum_board.setCustomDragItem(CustomDragItem(context!!, R.layout.column_item))
        board_view_scrum_board.setSnapToColumnInLandscape(false)
        board_view_scrum_board.setColumnSnapPosition(BoardView.ColumnSnapPosition.CENTER)

        board_view_scrum_board.setBoardListener(ColumnChangeListener())

        addColumn(UserStoryState.SPRINT_BACKLOG)
        addColumn(UserStoryState.IN_PROGRESS)
        addColumn(UserStoryState.TO_VERIFY)
        addColumn(UserStoryState.DONE)
    }

    private fun addColumn(userStoryState: UserStoryState) {
        val items = mutableListOf<Pair<Int, UserStory>>()
        userStories.filter { it.state == userStoryState &&
                ((it.sprint == null && userStoryState == UserStoryState.SPRINT_BACKLOG) || (it.sprint != null && userStoryState != UserStoryState.SPRINT_BACKLOG)) }
                .forEach { items.add(Pair(it.id, it)) }

        val adapter = CustomDragItemAdapter(items, R.layout.column_item, R.id.item_layout, true)
        val header = View.inflate(activity, R.layout.column_header, null)
        (header.findViewById(R.id.column_header_text_view) as TextView).text = userStoryState.toString().replace('_', ' ')
        board_view_scrum_board.addColumn(adapter, header, null, false)
    }

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

        val scrumbleGUI = UserStory(0, pauli, sam, "GUI Programmieren", "Hierbei handelt es sich um die Android GUI",
                0, UserStoryState.IN_PROGRESS, 0, 0)
        val scrumbleDB = UserStory(1, webi, simon, "Datenbank aufsetzten", "Inklusive Trigger, Sequenzen usw...",
                0, UserStoryState.TO_VERIFY, 1, 0)
        val scrumbleWebservice = UserStory(2, webi, pauli, "Webservice implementieren", "Der Webservice connected Client mit Datenbank",
                0, UserStoryState.SPRINT_BACKLOG, 0, null)
        val scrumbleTesting = UserStory(3, simon, sam, "Testen", "Sollen wir Unit-Tests machen? Keine Ahnung",
                0, UserStoryState.SPRINT_BACKLOG, 0, null)
        val scrumblePlaning = UserStory(4, sam, pauli, "Plaung", "Geplant wird immer, weil wir mit Scrum arbeiten, lol",
                0, UserStoryState.IN_PROGRESS, 2, 0)
        userStories.add(scrumbleGUI)
        userStories.add(scrumbleDB)
        userStories.add(scrumbleWebservice)
        userStories.add(scrumbleTesting)
        userStories.add(scrumblePlaning)
    }


    inner class ColumnChangeListener: BoardView.BoardListener {
        override fun onItemDragEnded(fromColumn: Int, fromRow: Int, toColumn: Int, toRow: Int){
            if(fromColumn == toColumn)
                Toast.makeText(context!!, "new row: $toRow", Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(context!!, "new column: $toColumn", Toast.LENGTH_SHORT).show()
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