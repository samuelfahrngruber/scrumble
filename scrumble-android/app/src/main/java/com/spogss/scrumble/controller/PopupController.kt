package com.spogss.scrumble.controller

import android.content.Context
import android.text.InputType
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.llollox.androidtoggleswitch.widgets.ToggleSwitch
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.mikepenz.fastadapter.select.SelectExtension
import com.rengwuxian.materialedittext.MaterialEditText
import com.spogss.scrumble.R
import com.spogss.scrumble.activity.MainActivity
import com.spogss.scrumble.adapter.CustomSimpleAdapter
import com.spogss.scrumble.adapter.CustomSwipeItemAdapter
import com.spogss.scrumble.data.Sprint
import com.spogss.scrumble.data.Task
import com.spogss.scrumble.data.User
import com.spogss.scrumble.enums.TaskState
import com.spogss.scrumble.viewItem.CustomSelectableItem
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner
import com.woxthebox.draglistview.DragListView
import com.woxthebox.draglistview.swipe.ListSwipeHelper
import com.woxthebox.draglistview.swipe.ListSwipeItem
import de.mrapp.android.dialog.MaterialDialog
import de.mrapp.android.dialog.ScrollableArea
import java.text.SimpleDateFormat
import java.util.*

object PopupController {
    val startCal = Calendar.getInstance()
    val endCal = Calendar.getInstance()

    fun setupTaskPopup(context: Context, callback: () -> Unit, task: Task? = null) {
        val dialogBuilder = MaterialDialog.Builder(context)
        dialogBuilder.setTitle(R.string.task)
        dialogBuilder.setTitleColor(ContextCompat.getColor(context, R.color.colorAccent))
        dialogBuilder.setPositiveButton(android.R.string.ok, null)
        dialogBuilder.setNegativeButton(android.R.string.cancel, null)
        dialogBuilder.setButtonTextColor(ContextCompat.getColor(context, R.color.colorAccent))
        dialogBuilder.setCanceledOnTouchOutside(false)

        val customView = View.inflate(context, R.layout.popup_task, null)

        val toggleSwitch = customView.findViewById<ToggleSwitch>(R.id.popup_add_task_toggle_button)
        val nameEditText = customView.findViewById<MaterialEditText>(R.id.popup_add_task_name)
        val infoEditText = customView.findViewById<MaterialEditText>(R.id.popup_add_task_info)
        val rejectionsEditText = customView.findViewById<MaterialEditText>(R.id.popup_add_task_rejections)

        if(task == null) {
            rejectionsEditText.visibility = View.GONE
            toggleSwitch.setCheckedPosition(0)
            //TODO: Disable ToggleSwitch when currentSprint = null

            setupTaskSpinner(customView, context)
        }
        else {
            nameEditText.setText(task.name)
            infoEditText.setText(task.info)
            rejectionsEditText.setText(task.rejections.toString())
            toggleSwitch.visibility = View.GONE

            setupTaskSpinner(customView, context, task)
        }

        dialogBuilder.setScrollableArea(ScrollableArea.Area.CONTENT)
        dialogBuilder.setView(customView)

        val dialog = dialogBuilder.create()
        dialog.setOnShowListener {
            dialog.getButton(MaterialDialog.BUTTON_POSITIVE).setOnClickListener { _ ->
                if(taskDialogButtonClick(customView, context)) {
                    callback()
                    dialog.dismiss()
                }
            }
        }

        dialog.show()
    }
    private fun setupTaskSpinner(customView: View, context: Context, task: Task? = null) {
        val responsibleSpinner = customView.findViewById<MaterialBetterSpinner>(R.id.popup_add_task_responsible)
        responsibleSpinner.setAdapter(ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, ScrumbleController.users))

        val verifySpinner = customView.findViewById<MaterialBetterSpinner>(R.id.popup_add_task_verify)
        verifySpinner.setAdapter(ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, ScrumbleController.users))

        if(task != null) {
            responsibleSpinner.setText(task.responsible.username)
            verifySpinner.setText(task.verify.username)
        }
    }
    private fun taskDialogButtonClick(customView: View, context: Context): Boolean {
        val nameEditText = customView.findViewById<MaterialEditText>(R.id.popup_add_task_name)
        val infoEditText = customView.findViewById<MaterialEditText>(R.id.popup_add_task_info)
        val responsibleSpinner = customView.findViewById<MaterialBetterSpinner>(R.id.popup_add_task_responsible)
        val verifySpinner = customView.findViewById<MaterialBetterSpinner>(R.id.popup_add_task_verify)

        var closePopup = true
        if(nameEditText.text.trim().isEmpty()) {
            nameEditText.error = context.resources.getString(R.string.error_enter_name)
            closePopup = false
        }
        if(infoEditText.text.trim().isEmpty()) {
            infoEditText.error = context.resources.getString(R.string.error_enter_info)
            closePopup = false
        }
        if(responsibleSpinner.text.trim().isEmpty()) {
            responsibleSpinner.error = context.resources.getString(R.string.error_select_team_member)
            closePopup = false
        }
        if(verifySpinner.text.trim().isEmpty()) {
            verifySpinner.error = context.resources.getString(R.string.error_select_team_member)
            closePopup = false
        }

        return closePopup
    }

    fun setupProjectPopup(context: Context, callback: () -> Unit) {
        setupSpeedDialPopups(context.resources.getString(R.string.project), context, {
            setupAddProjectAddTeamMemberCustomView(R.layout.popup_project, context)
        }, {
            addProjectButtonClick(it, context)
        }, callback)
    }
    fun setupTeamMemberPopup(context: Context, callback: () -> Unit) {
        setupSpeedDialPopups(context.resources.getString(R.string.team_member), context, {
            setupAddProjectAddTeamMemberCustomView(R.layout.popup_team_member, context)
        }, {
            addTeamMemberButtonClick(it)
        }, callback)
    }
    fun setupSprintPopup(context: Context, callback: () -> Unit, sprint: Sprint? = null) {
        setupSpeedDialPopups(context.resources.getString(R.string.sprint), context, {
            setupAddSprintCustomView(context, sprint)
        }, {
            addSprintButtonClick(it, context)
        }, callback)
    }

    private fun setupSpeedDialPopups(title: String, context: Context, customization: () -> View,
                                     buttonCallBack: (view: View) -> Boolean, callback: () -> Unit) {
        val dialogBuilder = MaterialDialog.Builder(context)
        dialogBuilder.setTitle(title)
        dialogBuilder.setTitleColor(ContextCompat.getColor(context, R.color.colorAccent))
        dialogBuilder.setPositiveButton(android.R.string.ok, null)
        dialogBuilder.setNegativeButton(android.R.string.cancel, null)
        dialogBuilder.setButtonTextColor(ContextCompat.getColor(context, R.color.colorAccent))
        dialogBuilder.setCanceledOnTouchOutside(false)

        val customView = customization()
        dialogBuilder.setScrollableArea(ScrollableArea.Area.CONTENT)
        dialogBuilder.setView(customView)

        val dialog = dialogBuilder.create()
        dialog.setOnShowListener {
            dialog.getButton(MaterialDialog.BUTTON_POSITIVE).setOnClickListener { _ ->
                if(buttonCallBack(customView)) {
                    dialog.dismiss()
                    callback()
                }
            }
        }

        dialog.show()
    }

    private fun setupAddProjectAddTeamMemberCustomView(res: Int, context: Context): View {
        val customView = View.inflate(context, res, null)
        val listView = customView.findViewById<DragListView>(R.id.swipe_list_add_team_member)
        val teamMemberEditText = customView.findViewById<MaterialEditText>(R.id.popup_add_team_member)

        listView.setLayoutManager(LinearLayoutManager(context))
        listView.isDragEnabled = false
        val adapter = CustomSwipeItemAdapter(mutableListOf<Pair<Int, User>>(), R.layout.item_list_swipeable, R.id.item_layout, false, context!!)
        listView.setAdapter(adapter, true)

        listView.setSwipeListener(object : ListSwipeHelper.OnSwipeListener {
            override fun onItemSwipeEnded(item: ListSwipeItem, swipedDirection: ListSwipeItem.SwipeDirection?) {
                if(swipedDirection == ListSwipeItem.SwipeDirection.LEFT) {
                    val userItem = item.tag as Pair<Int, User>
                    val pos = listView.adapter.getPositionForItem(userItem)
                    listView.adapter.removeItem(pos)
                }
            }
            override fun onItemSwiping(item: ListSwipeItem?, swipedDistanceX: Float) {}
            override fun onItemSwipeStarted(item: ListSwipeItem?) {}
        })

        teamMemberEditText.setRawInputType(InputType.TYPE_CLASS_TEXT)
        teamMemberEditText.setImeActionLabel(context.resources.getString(R.string.add), EditorInfo.IME_ACTION_SEND)
        teamMemberEditText.imeOptions = EditorInfo.IME_ACTION_SEND
        teamMemberEditText.setOnEditorActionListener { _, actionId, event ->
            var ret = false
            if (event == null) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    if (teamMemberEditText.text.trim().toString().trim() != "") {
                        //TODO: check if team member exists
                        //TODO: check if team member does not already work at the current project

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
    private fun addProjectButtonClick(customView: View, context: Context): Boolean {
        val nameEditText = customView.findViewById<MaterialEditText>(R.id.popup_add_project_name)
        val productOwnerEditText = customView.findViewById<MaterialEditText>(R.id.popup_add_project_product_owner)

        var closePopup = true
        if(nameEditText.text.trim().isEmpty()) {
            nameEditText.error = context.resources.getString(R.string.error_enter_name)
            closePopup = false
        }
        if(productOwnerEditText.text.trim().isEmpty()) {
            productOwnerEditText.error = context.resources.getString(R.string.error_enter_product_owner)
            closePopup = false
        }

        //TODO: check if Product Owner exist
        //TODO: add project

        return closePopup
    }
    private fun addTeamMemberButtonClick(customView: View): Boolean {
        //TODO: add team members
        return true
    }

    private fun setupAddSprintCustomView(context: Context, sprint: Sprint? = null): View {
        val customView = View.inflate(context, R.layout.popup_sprint, null)
        val sprintNumberEditText = customView.findViewById<MaterialEditText>(R.id.popup_add_sprint_number)
        val selectListTask = customView.findViewById<RecyclerView>(R.id.popup_add_sprint_tasks)

        val adapter = FastItemAdapter<CustomSelectableItem>()

        if(sprint == null) {
            //TODO: set next sprint number
            sprintNumberEditText.setText("1")
            setupDatePicker(customView, context)
            adapter.add(ScrumbleController.tasks.filter { it.state == TaskState.PRODUCT_BACKLOG }.map { CustomSelectableItem(it) })
        }
        else {
            sprintNumberEditText.setText(sprint.number.toString())
            setupDatePicker(customView, context, sprint)
            adapter.add(ScrumbleController.tasks.filter { it.state == TaskState.PRODUCT_BACKLOG || (it.sprint != null && it.sprint == sprint.id) }.map { CustomSelectableItem(it) })
        }

        adapter.withSelectable(true)
        adapter.withOnPreClickListener { _, _, _, _ -> true }
        adapter.withEventHook(CustomSelectableItem(null).CheckBoxClickEvent())

        adapter.adapterItems.forEachIndexed { index, item ->
            if(item.task != null && item.task.state != TaskState.PRODUCT_BACKLOG)
                (adapter.getExtension(SelectExtension::class.java as Class<SelectExtension<CustomSelectableItem>>) as SelectExtension<CustomSelectableItem>).select(index)
        }

        selectListTask.layoutManager = LinearLayoutManager(context)
        selectListTask.itemAnimator = DefaultItemAnimator()
        selectListTask.adapter = adapter

        return customView
    }
    private fun setupDatePicker(customView: View, context: Context, sprint: Sprint? = null) {
        val startDateEditText = customView.findViewById<MaterialEditText>(R.id.popup_add_sprint_start_date)
        val endDateEditText = customView.findViewById<MaterialEditText>(R.id.popup_add_sprint_deadline)

        val calender = Calendar.getInstance()
        val dateFormatter = SimpleDateFormat("EEEE, dd.MM.yyyy", Locale("EN"))

        if(sprint != null) calender.time = sprint.startDate
        startDateEditText.setText(dateFormatter.format(calender.time))
        if(sprint != null) calender.time = sprint.deadline else calender.add(Calendar.DAY_OF_MONTH, 1)
        endDateEditText.setText(dateFormatter.format(calender.time))

        val endPicker = DatePickerDialog.newInstance({ _, year, monthOfYear, dayOfMonth ->
            endCal.set(year, monthOfYear, dayOfMonth)
            endDateEditText.setText(dateFormatter.format(endCal.time))
        }, calender)
        endCal.set(calender.get(Calendar.YEAR), calender.get(Calendar.MONTH), calender.get(Calendar.DAY_OF_MONTH))
        endPicker.minDate = endCal
        endPicker.setTitle(context.resources.getString(R.string.deadline))
        endPicker.vibrate(false)

        if(sprint != null) calender.time = sprint.startDate else calender.add(Calendar.DAY_OF_MONTH, -1)
        val startPicker = DatePickerDialog.newInstance { _, year, monthOfYear, dayOfMonth ->
            startCal.set(year, monthOfYear, dayOfMonth)
            startDateEditText.setText(dateFormatter.format(startCal.time))

            startCal.add(Calendar.DAY_OF_MONTH, 1)
            endPicker.minDate = startCal
            startCal.add(Calendar.DAY_OF_MONTH, -1)

            if(startCal.time > endCal.time) {
                endCal.set(endPicker.minDate.get(Calendar.YEAR), endPicker.minDate.get(Calendar.MONTH), endPicker.minDate.get(Calendar.DAY_OF_MONTH))
                endDateEditText.setText(dateFormatter.format(endCal.time))
            }
        }
        startCal.set(calender.get(Calendar.YEAR), calender.get(Calendar.MONTH), calender.get(Calendar.DAY_OF_MONTH))
        startPicker.setTitle(context.resources.getString(R.string.start_date))
        startPicker.vibrate(false)

        startDateEditText.setOnClickListener {
            if(!startPicker.isAdded)
                startPicker.show((context as MainActivity).supportFragmentManager, context.resources.getString(R.string.start_date))
        }
        endDateEditText.setOnClickListener {
            if(!endPicker.isAdded)
                endPicker.show((context as MainActivity).supportFragmentManager, context.resources.getString(R.string.deadline))
        }
    }
    private fun addSprintButtonClick(customView: View, context: Context): Boolean {
        val sprintNumberEditText = customView.findViewById<MaterialEditText>(R.id.popup_add_sprint_number)

        var closePopup = true
        if(sprintNumberEditText.text.trim().isEmpty()) {
            sprintNumberEditText.error = context.resources.getString(R.string.error_enter_sprint_number)
            closePopup = false
        }
        else if(!sprintNumberEditText.text.matches("\\d+".toRegex())) {
            sprintNumberEditText.error = context.resources.getString(R.string.error_sprint_number_integer)
            closePopup = false
        }
        else {
            //TODO: check if sprint number already exists
            println("${startCal.time} - ${endCal.time}")
        }

        return closePopup
    }

    fun <T>setupRecyclerViewPopup(context: Context, callback: (item: T) -> Unit, title: String, data: MutableList<T>) {
        val dialogBuilder = MaterialDialog.Builder(context)
        dialogBuilder.setTitle(title)
        dialogBuilder.setTitleColor(ContextCompat.getColor(context, R.color.colorAccent))

        val customView = View.inflate(context, R.layout.popup_recycler_view, null)
        val recyclerView = customView.findViewById<RecyclerView>(R.id.popup_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        dialogBuilder.setScrollableArea(ScrollableArea.Area.CONTENT)
        dialogBuilder.setView(customView)

        val dialog = dialogBuilder.create()
        recyclerView.adapter = CustomSimpleAdapter(data, context) { callback(it); dialog.dismiss() }
        dialog.show()
    }
}