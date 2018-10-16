package com.spogss.scrumble.fragment

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.rengwuxian.materialedittext.MaterialEditText
import com.spogss.scrumble.R
import com.spogss.scrumble.adapter.CustomSwipeItemAdapter
import com.spogss.scrumble.data.Project
import com.spogss.scrumble.data.Sprint
import com.spogss.scrumble.data.Task
import com.spogss.scrumble.data.User
import com.spogss.scrumble.enums.UserStoryState
import com.woxthebox.draglistview.DragListView
import com.woxthebox.draglistview.swipe.ListSwipeHelper
import com.woxthebox.draglistview.swipe.ListSwipeItem
import de.mrapp.android.dialog.MaterialDialog
import de.mrapp.android.dialog.ScrollableArea
import kotlinx.android.synthetic.main.fragment_projects.*
import java.util.*


class ProjectsFragment: Fragment() {
    //TODO: remove when webservice call is possible
    private var users = mutableListOf<User>()
    private var projects = mutableListOf<Project>()
    private var sprints = mutableListOf<Sprint>()
    private var userStories = mutableListOf<Task>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //TODO: remove when webservice call is possible
        createTestData()
        return inflater.inflate(R.layout.fragment_projects, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpeedDial()
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
                    setupPopup(resources.getString(R.string.add_project), {
                        setupAddProjectCustomView()
                    }, {
                        addProjectButtonClick(it)
                    })
                }
                R.id.fab_add_sprint -> {

                }
                R.id.fab_add_team_member -> {

                }
                else -> retVal = false
            }
            retVal
        }
    }

    private fun setupPopup(title: String, customization: () -> View, buttonCallBack: (view: View) -> Boolean) {
        val dialogBuilder = MaterialDialog.Builder(context!!)
        dialogBuilder.setTitle(title)
        dialogBuilder.setTitleColor(ContextCompat.getColor(context!!, R.color.colorAccent))
        dialogBuilder.setPositiveButton(android.R.string.ok, null)
        dialogBuilder.setNegativeButton(android.R.string.cancel, null)
        dialogBuilder.setButtonTextColor(ContextCompat.getColor(context!!, R.color.colorAccent))
        dialogBuilder.setCanceledOnTouchOutside(false)

        val customView = customization()
        dialogBuilder.setScrollableArea(ScrollableArea.Area.CONTENT)
        dialogBuilder.setView(customView)

        val dialog = dialogBuilder.create()
        dialog.setOnShowListener {
            dialog.getButton(MaterialDialog.BUTTON_POSITIVE).setOnClickListener { _ ->
                if(buttonCallBack(customView)) {
                    dialog.dismiss()
                    speed_dial.close(true)
                }
            }
        }

        dialog.show()
    }

    private fun setupAddProjectCustomView(): View {
        val customView = View.inflate(context, R.layout.popup_add_project, null)
        val listView = customView.findViewById<DragListView>(R.id.swipe_list_add_project_team_member)
        val teamMemberEditText = customView.findViewById<MaterialEditText>(R.id.popup_add_project_team_member)

        listView.setLayoutManager(LinearLayoutManager(context))
        listView.isDragEnabled = false
        val adapter = CustomSwipeItemAdapter(mutableListOf<Pair<Int, User>>(), R.layout.swipe_list_item, R.id.item_layout, false, context!!)
        listView.setAdapter(adapter, true)

        listView.setSwipeListener(object : ListSwipeHelper.OnSwipeListener {
            override fun onItemSwipeEnded(item: ListSwipeItem, swipedDirection: ListSwipeItem.SwipeDirection?) {
                if(swipedDirection == ListSwipeItem.SwipeDirection.LEFT) {
                    val item = item.tag as Pair<Int, User>
                    val pos = listView.adapter.getPositionForItem(item)
                    listView.adapter.removeItem(pos)
                }
            }
            override fun onItemSwiping(item: ListSwipeItem?, swipedDistanceX: Float) {}
            override fun onItemSwipeStarted(item: ListSwipeItem?) {}
        })

        teamMemberEditText.setRawInputType(InputType.TYPE_CLASS_TEXT)
        teamMemberEditText.setImeActionLabel(resources.getString(R.string.add), EditorInfo.IME_ACTION_SEND)
        teamMemberEditText.imeOptions = EditorInfo.IME_ACTION_SEND
        teamMemberEditText.setOnEditorActionListener { textView, actionId, event ->
            var ret = false
            if (event == null) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    if (teamMemberEditText.text.trim().toString().trim() != "") {
                        //TODO: check if team member exists
                        listView.adapter.addItem(adapter.itemCount, Pair(adapter.itemCount, User(adapter.itemCount, teamMemberEditText.text.trim().toString(), "")))
                        teamMemberEditText.setText("")
                        teamMemberEditText.requestFocus()
                        ret = true
                    }
                }
            }
            ret
        }

        return customView
    }

    private fun addProjectButtonClick(customView: View): Boolean {
        val nameEditText = customView.findViewById<MaterialEditText>(R.id.popup_add_project_name)
        val productOwnerEditText = customView.findViewById<MaterialEditText>(R.id.popup_add_project_product_owner)

        var closePopup = true
        if(nameEditText.text.trim().isEmpty()) {
            nameEditText.error = resources.getString(R.string.error_enter_name)
            closePopup = false
        }
        if(productOwnerEditText.text.trim().isEmpty()) {
            productOwnerEditText.error = resources.getString(R.string.error_enter_product_owner)
            closePopup = false
        }

        //TODO: Check if Product Owner exist

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

}
