package com.spogss.scrumble.controller

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.text.InputType
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.llollox.androidtoggleswitch.widgets.ToggleSwitch
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.mikepenz.fastadapter.select.SelectExtension
import com.polyak.iconswitch.IconSwitch
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
import com.thebluealliance.spectrum.SpectrumDialog
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner
import com.woxthebox.draglistview.DragListView
import com.woxthebox.draglistview.swipe.ListSwipeHelper
import com.woxthebox.draglistview.swipe.ListSwipeItem
import de.mrapp.android.dialog.MaterialDialog
import de.mrapp.android.dialog.ScrollableArea
import de.mrapp.android.dialog.animation.DialogAnimation
import org.jetbrains.anko.find
import java.text.SimpleDateFormat
import java.util.*

object PopupController {
    private var dialog: MaterialDialog? = null
    private var dialogTask: MaterialDialog? = null
    private var dialogRemove: MaterialDialog? = null

    val startCal = Calendar.getInstance()!!
    val endCal = Calendar.getInstance()!!

    fun setupTaskPopup(context: Context, callback: (view: View) -> Unit, negativeCallback: () -> Unit, task: Task? = null) {
        if(dialogTask != null) return

        val dialogBuilder = MaterialDialog.Builder(context)
        dialogBuilder.setTitle(R.string.task)
        dialogBuilder.setTitleColor(ContextCompat.getColor(context, R.color.colorAccent))
        dialogBuilder.setPositiveButton(android.R.string.ok, null)
        dialogBuilder.setNegativeButton(R.string.cancel, null)
        dialogBuilder.setButtonTextColor(ContextCompat.getColor(context, R.color.colorAccent))

        if(task != null) dialogBuilder.setNeutralButton(R.string.remove, null)

        val customView = View.inflate(context, R.layout.popup_task, null)

        val toggleSwitch = customView.findViewById<ToggleSwitch>(R.id.popup_add_task_toggle_button)
        val nameEditText = customView.findViewById<MaterialEditText>(R.id.popup_add_task_name)
        val infoEditText = customView.findViewById<MaterialEditText>(R.id.popup_add_task_info)
        val rejectionsContainer = customView.findViewById<LinearLayout>(R.id.popup_add_task_rejections_container)
        val rejectionsEditText = customView.findViewById<MaterialEditText>(R.id.popup_add_task_rejections)
        val incRejectionsButton = customView.findViewById<ImageButton>(R.id.popup_add_task_increase_rejections)
        val decRejectionsButton = customView.findViewById<ImageButton>(R.id.popup_add_task_decrease_rejections)
        val colorTextView = customView.find<TextView>(R.id.popup_add_task_color)

        if (task == null) {
            rejectionsContainer.visibility = View.GONE
            toggleSwitch.setCheckedPosition(0)

            if (!ScrumbleController.isCurrentSprintSpecified())
                toggleSwitch.isEnabled = false

            (colorTextView.background as GradientDrawable).setColor(ContextCompat.getColor(context, R.color.colorPrimary))
            colorTextView.tag = "#${Integer.toHexString(ContextCompat.getColor(context, R.color.colorPrimary)).substring(2)}"

            setupTaskSpinner(customView, context)
        } else {
            nameEditText.setText(task.name)
            infoEditText.setText(task.info)
            rejectionsEditText.setText(task.rejections.toString())
            toggleSwitch.visibility = View.GONE

            (colorTextView.background as GradientDrawable).setColor(Color.parseColor(task.color))
            colorTextView.tag = task.color

            setupTaskSpinner(customView, context, task)

            incRejectionsButton.setOnClickListener { rejectionsEditText.setText((rejectionsEditText.text.toString().toInt() + 1).toString()) }
            decRejectionsButton.setOnClickListener {
                val rejections = rejectionsEditText.text.toString().toInt() - 1
                if(rejections > -1)
                    rejectionsEditText.setText(rejections.toString())
            }
        }

        colorTextView.setOnClickListener {
            setupColorPicker(context, R.array.palette,
                    if(colorTextView.tag == null) ContextCompat.getColor(context, R.color.colorPrimary)
                    else Color.parseColor(colorTextView.tag as String)
            )
            { color ->
                (colorTextView.background as GradientDrawable).setColor(color)
                colorTextView.tag = "#${Integer.toHexString(color).substring(2)}"
            }
        }

        dialogBuilder.setScrollableArea(ScrollableArea.Area.CONTENT)
        dialogBuilder.setView(customView)

        dialogTask = dialogBuilder.create()
        dialogTask?.setOnShowListener {
            dialogTask?.getButton(MaterialDialog.BUTTON_POSITIVE)?.setOnClickListener {
                if (taskDialogButtonClick(customView, context)) {
                    callback(customView)
                    dialogTask?.dismiss()
                }
            }
            if(task != null) {
                dialogTask?.getButton(MaterialDialog.BUTTON_NEUTRAL)?.setOnClickListener {
                    negativeCallback()
                }
            }
        }

        dialogTask?.setOnDismissListener { dialogTask = null }

        dialogTask?.show()
    }
    private fun setupTaskSpinner(customView: View, context: Context, task: Task? = null) {
        val users = mutableListOf(User(-1, "", ""))
        users.addAll(ScrumbleController.users)

        val responsibleSpinner = customView.findViewById<MaterialBetterSpinner>(R.id.popup_add_task_responsible)
        responsibleSpinner.setAdapter(ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, users))

        val verifySpinner = customView.findViewById<MaterialBetterSpinner>(R.id.popup_add_task_verify)
        verifySpinner.setAdapter(ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, users))

        if (task != null) {
            if(task.responsible != null)
                responsibleSpinner.setText(task.responsible!!.toString())
            if(task.verify != null)
                verifySpinner.setText(task.verify!!.toString())
        }
    }
    private fun taskDialogButtonClick(customView: View, context: Context): Boolean {
        val nameEditText = customView.findViewById<MaterialEditText>(R.id.popup_add_task_name)
        val infoEditText = customView.findViewById<MaterialEditText>(R.id.popup_add_task_info)

        var closePopup = true
        if (nameEditText.text.trim().isEmpty()) {
            nameEditText.error = context.resources.getString(R.string.error_enter_name)
            closePopup = false
        }
        if (infoEditText.text.trim().isEmpty()) {
            infoEditText.error = context.resources.getString(R.string.error_enter_info)
            closePopup = false
        }

        return closePopup
    }

    fun setupProjectPopup(context: Context, callback: (view: View) -> Unit) {
        setupSpeedDialPopups(context.resources.getString(R.string.project), context, {
            setupAddProjectAddTeamMemberCustomView(R.layout.popup_project, context, true)
        }, {
            addProjectButtonClick(it, context)
        }, callback)
    }
    fun setupTeamMemberPopup(context: Context, callback: (view: View) -> Unit) {
        setupSpeedDialPopups(context.resources.getString(R.string.team_member), context, {
            setupAddProjectAddTeamMemberCustomView(R.layout.popup_team_member, context, false)
        }, { true }, callback)
    }
    fun setupSprintPopup(context: Context, callback: (view: View) -> Unit, sprint: Sprint? = null) {
        setupSpeedDialPopups(context.resources.getString(R.string.sprint), context, {
            setupAddSprintCustomView(context, sprint)
        }, {
            addSprintButtonClick(it, context, sprint)
        }, callback)
    }
    private fun setupSpeedDialPopups(title: String, context: Context, customization: () -> View,
                                     buttonCallBack: (view: View) -> Boolean, callback: (view: View) -> Unit) {

        if(dialog != null) return

        val dialogBuilder = MaterialDialog.Builder(context)
        dialogBuilder.setTitle(title)
        dialogBuilder.setTitleColor(ContextCompat.getColor(context, R.color.colorAccent))
        dialogBuilder.setPositiveButton(android.R.string.ok, null)
        dialogBuilder.setNegativeButton(R.string.cancel, null)
        dialogBuilder.setButtonTextColor(ContextCompat.getColor(context, R.color.colorAccent))

        val customView = customization()
        dialogBuilder.setScrollableArea(ScrollableArea.Area.CONTENT)
        dialogBuilder.setView(customView)

        dialog = dialogBuilder.create()
        dialog?.setOnShowListener {
            dialog?.getButton(MaterialDialog.BUTTON_POSITIVE)?.setOnClickListener {
                if (buttonCallBack(customView)) {
                    dialog?.dismiss()
                    callback(customView)
                }
            }
        }

        dialog?.setOnDismissListener { dialog = null }

        dialog?.show()
    }
    private fun setupAddProjectAddTeamMemberCustomView(res: Int, context: Context, isProject: Boolean): View {
        val customView = View.inflate(context, res, null)
        val listView = customView.findViewById<DragListView>(R.id.swipe_list_add_team_member)
        val teamMemberEditText = customView.findViewById<MaterialEditText>(R.id.popup_add_team_member)

        customView.findViewById<MaterialEditText>(R.id.popup_add_project_product_owner)?.setText(ScrumbleController.currentUser.toString())

        listView.setLayoutManager(LinearLayoutManager(context))
        listView.isDragEnabled = false

        val adapter = CustomSwipeItemAdapter(mutableListOf(), R.layout.item_list_swipeable, R.id.item_layout, false, context, true)
        listView.setAdapter(adapter, true)

        listView.setSwipeListener(object : ListSwipeHelper.OnSwipeListener {
            override fun onItemSwipeEnded(item: ListSwipeItem, swipedDirection: ListSwipeItem.SwipeDirection?) {
                if (swipedDirection == ListSwipeItem.SwipeDirection.LEFT) {
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
                    val name = teamMemberEditText.text.toString().trim()
                    if (name != "") {
                        if(name.contains(" "))
                            MiscUIController.showError(context, context.resources.getString(R.string.error_whitespaces))
                        else {
                            MiscUIController.startLoadingAnimation(customView, context)
                            dialog?.getButton(MaterialDialog.BUTTON_POSITIVE)?.isEnabled = false

                            ScrumbleController.getTeamMemberByName(name, { user ->
                                try {
                                    if (ScrumbleController.users.contains(user) && !isProject)
                                        MiscUIController.showError(context, context.resources.getString(R.string.error_user_already_involved))
                                    else {
                                        if (!listView.adapter.itemList.contains(Pair(adapter.itemCount - 1, user)))
                                            listView.adapter.addItem(adapter.itemCount, Pair(adapter.itemCount, user))

                                        teamMemberEditText.setText("")
                                        teamMemberEditText.clearFocus()
                                    }

                                    MiscUIController.stopLoadingAnimation(customView, context)
                                    dialog?.getButton(MaterialDialog.BUTTON_POSITIVE)?.isEnabled = true
                                } catch (ex: Exception) {
                                    ex.printStackTrace()
                                }

                            }, {
                                try {
                                    MiscUIController.showError(context, context.resources.getString(R.string.error_username_does_not_exist))
                                    MiscUIController.stopLoadingAnimation(customView, context)
                                    dialog?.getButton(MaterialDialog.BUTTON_POSITIVE)?.isEnabled = true
                                } catch (ex: Exception) {
                                    ex.printStackTrace()
                                }
                            })
                        }
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
        if (nameEditText.text.trim().isEmpty()) {
            nameEditText.error = context.resources.getString(R.string.error_enter_name)
            closePopup = false
        }
        if (productOwnerEditText.text.trim().isEmpty()) {
            productOwnerEditText.error = context.resources.getString(R.string.error_enter_product_owner)
            closePopup = false
        }

        return closePopup
    }

    private fun setupAddSprintCustomView(context: Context, sprint: Sprint? = null): View {
        val customView = View.inflate(context, R.layout.popup_sprint, null)
        val sprintNumberEditText = customView.findViewById<MaterialEditText>(R.id.popup_add_sprint_number)
        val selectListTask = customView.findViewById<RecyclerView>(R.id.popup_add_sprint_tasks)
        val currentSprintSwitch = customView.findViewById<IconSwitch>(R.id.popup_add_sprint_current_sprint)

        val adapter = FastItemAdapter<CustomSelectableItem>()

        if (sprint == null) {
            val sprintNumber = if (ScrumbleController.sprints.size > 0) ScrumbleController.sprints.maxBy { it.number }!!.number + 1 else 1
            sprintNumberEditText.setText(sprintNumber.toString())
            setupDatePicker(customView, context)
            adapter.add(ScrumbleController.tasks.filter { it.state == TaskState.PRODUCT_BACKLOG }.map { CustomSelectableItem(it) })
        } else {
            sprintNumberEditText.setText(sprint.number.toString())
            setupDatePicker(customView, context, sprint)

            if(ScrumbleController.currentProject!!.currentSprint == sprint)
                currentSprintSwitch.checked = IconSwitch.Checked.RIGHT

            adapter.add(ScrumbleController.tasks.filter { it.state == TaskState.PRODUCT_BACKLOG || (it.sprint != null && it.sprint!!.id == sprint.id) }.sortedByDescending { it.state }.map { CustomSelectableItem(it) })
        }

        adapter.withSelectable(true)
        adapter.withEventHook(CustomSelectableItem(null).CheckBoxClickEvent())

        adapter.withOnPreClickListener { _, _, item, position ->
            if(item.task != null)
                PopupController.setupTaskPopup(context, {
                    UIToScrumbleController.updateTask(item.task, it, context) {
                        adapter.notifyAdapterItemChanged(position)
                    }
                }, {
                    PopupController.setupDeleteTaskPopup(context, { delete ->
                        if(delete) {
                            UIToScrumbleController.removeTask(item.task, context) {
                                adapter.adapterItems.removeAt(position)
                                adapter.notifyAdapterDataSetChanged()
                            }
                        }
                        else {
                            item.task.state = TaskState.PRODUCT_BACKLOG
                            item.task.sprint = null
                            ScrumbleController.updateTask(item.task.id, item.task, {}, { MiscUIController.showError(context, it) } )
                            (adapter.getExtension(SelectExtension::class.java as Class<SelectExtension<CustomSelectableItem>>) as SelectExtension<CustomSelectableItem>).toggleSelection(position)
                            adapter.notifyDataSetChanged()
                        }
                    }, item.task.state != TaskState.PRODUCT_BACKLOG)
                }, item.task)
            true
        }

        adapter.adapterItems.forEachIndexed { index, item ->
            if (item.task != null && item.task.state != TaskState.PRODUCT_BACKLOG)
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

        if (sprint != null) calender.time = sprint.startDate
        startDateEditText.setText(dateFormatter.format(calender.time))
        if (sprint != null) calender.time = sprint.deadline else calender.add(Calendar.DAY_OF_MONTH, 1)
        endDateEditText.setText(dateFormatter.format(calender.time))

        val endPicker = DatePickerDialog.newInstance({ _, year, monthOfYear, dayOfMonth ->
            endCal.set(year, monthOfYear, dayOfMonth)
            endDateEditText.setText(dateFormatter.format(endCal.time))
        }, calender)
        endCal.set(calender.get(Calendar.YEAR), calender.get(Calendar.MONTH), calender.get(Calendar.DAY_OF_MONTH))
        if (sprint != null) {
            val tempCal = Calendar.getInstance()
            tempCal.time = sprint.startDate
            tempCal.add(Calendar.DAY_OF_MONTH, 1)
            endPicker.minDate = tempCal
        } else endPicker.minDate = endCal
        endPicker.setTitle(context.resources.getString(R.string.deadline))
        endPicker.vibrate(false)

        if (sprint != null) calender.time = sprint.startDate else calender.add(Calendar.DAY_OF_MONTH, -1)
        val startPicker = DatePickerDialog.newInstance({ _, year, monthOfYear, dayOfMonth ->
            startCal.set(year, monthOfYear, dayOfMonth)
            startDateEditText.setText(dateFormatter.format(startCal.time))

            startCal.add(Calendar.DAY_OF_MONTH, 1)
            endPicker.minDate = startCal
            startCal.add(Calendar.DAY_OF_MONTH, -1)

            if (startCal.time > endCal.time) {
                endCal.set(endPicker.minDate.get(Calendar.YEAR), endPicker.minDate.get(Calendar.MONTH), endPicker.minDate.get(Calendar.DAY_OF_MONTH))
                endDateEditText.setText(dateFormatter.format(endCal.time))
            }
        }, calender)
        startCal.set(calender.get(Calendar.YEAR), calender.get(Calendar.MONTH), calender.get(Calendar.DAY_OF_MONTH))
        startPicker.setTitle(context.resources.getString(R.string.start_date))
        startPicker.vibrate(false)

        startDateEditText.setOnClickListener {
            if (!startPicker.isAdded)
                startPicker.show((context as MainActivity).supportFragmentManager, context.resources.getString(R.string.start_date))
        }
        endDateEditText.setOnClickListener {
            if (!endPicker.isAdded)
                endPicker.show((context as MainActivity).supportFragmentManager, context.resources.getString(R.string.deadline))
        }
    }
    private fun addSprintButtonClick(customView: View, context: Context, sprint: Sprint? = null): Boolean {
        val sprintNumberEditText = customView.findViewById<MaterialEditText>(R.id.popup_add_sprint_number)
        val startDateEditText = customView.findViewById<MaterialEditText>(R.id.popup_add_sprint_start_date)
        val deadlineEditText = customView.findViewById<MaterialEditText>(R.id.popup_add_sprint_deadline)

        var closePopup = true
        if (sprintNumberEditText.text.trim().isEmpty()) {
            sprintNumberEditText.error = context.resources.getString(R.string.error_enter_sprint_number)
            closePopup = false
        } else if (!sprintNumberEditText.text.matches("\\d+".toRegex())) {
            sprintNumberEditText.error = context.resources.getString(R.string.error_sprint_number_integer)
            closePopup = false
        } else if(ScrumbleController.sprints.find { it.number == sprintNumberEditText.text.toString().toInt() } != null
            && sprint == null) {
            sprintNumberEditText.error = context.resources.getString(R.string.error_sprint_number_exists)
            closePopup = false
        }
        else if(ScrumbleController.sprints.any { it != sprint && (startCal.time in it.startDate..it.deadline || endCal.time in it.startDate..it.deadline) } ) {
            startDateEditText.error = context.resources.getString(R.string.error_sprint_collision)
            deadlineEditText.error = context.resources.getString(R.string.error_sprint_collision)
            closePopup = false
        }

        return closePopup
    }

    fun <T> setupRecyclerViewPopup(context: Context, callback: (item: T) -> Unit, title: String, data: MutableList<T>) {
        if(dialog != null) return

        val dialogBuilder = MaterialDialog.Builder(context)
        dialogBuilder.setTitle(title)
        dialogBuilder.setTitleColor(ContextCompat.getColor(context, R.color.colorAccent))

        val customView = View.inflate(context, R.layout.popup_recycler_view, null)
        val recyclerView = customView.findViewById<RecyclerView>(R.id.popup_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        dialogBuilder.setScrollableArea(ScrollableArea.Area.CONTENT)
        dialogBuilder.setView(customView)

        dialog = dialogBuilder.create()
        recyclerView.adapter = CustomSimpleAdapter(data, context) { callback(it); dialog?.dismiss() }

        dialog?.setOnDismissListener { dialog = null }

        dialog?.show()
    }

    private fun setupColorPicker(context: Context, colorsRes: Int, selectedColor: Int, onColorSelected: (color: Int) -> Unit) {
        SpectrumDialog.Builder(context)
                .setColors(colorsRes)
                .setSelectedColor(selectedColor)
                .setDismissOnColorSelected(true)
                .setOutlineWidth(2)
                .setNegativeButtonText("")
                .setOnColorSelectedListener { positiveResult, color ->
                    if(positiveResult)
                        onColorSelected(color)
                }
                .build().show((context as MainActivity).supportFragmentManager, context.resources.getString(R.string.select_color))
    }

    fun setupConfirmPopup(context: Context, title: String, message: String, callback: () -> Unit) {
        if(dialog != null) return

        val dialogBuilder = MaterialDialog.Builder(context)
        dialogBuilder.setTitle(title)
        dialogBuilder.setTitleColor(ContextCompat.getColor(context, R.color.colorAccent))
        dialogBuilder.setMessage(message)
        dialogBuilder.setPositiveButton(android.R.string.ok, null)
        dialogBuilder.setNegativeButton(R.string.cancel, null)
        dialogBuilder.setButtonTextColor(ContextCompat.getColor(context, R.color.colorAccent))

        dialog = dialogBuilder.create()
        dialog?.setOnShowListener {
            dialog?.getButton(MaterialDialog.BUTTON_POSITIVE)?.setOnClickListener {
                callback()
                dialog?.dismiss()
            }
        }

        dialog?.setOnDismissListener { dialog = null }

        dialog?.show()
    }

    fun setupDeleteTaskPopup(context: Context, callback: (delete: Boolean) -> Unit, isInSprint: Boolean = true) {
        if(dialogRemove != null) return

        val dialogBuilder = MaterialDialog.Builder(context)
        dialogBuilder.setTitle(context.resources.getString(R.string.remove_task))
        dialogBuilder.setTitleColor(ContextCompat.getColor(context, R.color.colorAccent))
        dialogBuilder.setPositiveButton(android.R.string.ok, null)
        dialogBuilder.setNegativeButton(R.string.cancel, null)
        dialogBuilder.setButtonTextColor(ContextCompat.getColor(context, R.color.colorAccent))

        val customView = View.inflate(context, R.layout.popup_delete, null)
        val toggleSwitch = customView.findViewById<ToggleSwitch>(R.id.popup_delete_task_toggle_button)

        if(isInSprint) toggleSwitch.setCheckedPosition(0)
        else {
            toggleSwitch.setCheckedPosition(1)
            toggleSwitch.isEnabled = false
        }

        dialogBuilder.setView(customView)

        dialogRemove = dialogBuilder.create()
        dialogRemove?.setOnShowListener {
            dialogRemove?.getButton(MaterialDialog.BUTTON_POSITIVE)?.setOnClickListener {
                callback(toggleSwitch.checkedPosition == 1)
                dialogRemove?.dismiss()
                dialogTask?.dismiss()
            }
        }

        dialogRemove?.setOnDismissListener { dialogRemove = null }

        dialogRemove?.show()
    }
}