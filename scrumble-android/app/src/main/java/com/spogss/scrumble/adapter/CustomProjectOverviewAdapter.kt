package com.spogss.scrumble.adapter

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.mikepenz.fastadapter.select.SelectExtension
import com.rengwuxian.materialedittext.MaterialEditText
import com.spogss.scrumble.R
import com.spogss.scrumble.activity.MainActivity
import com.spogss.scrumble.controller.ScrumbleController
import com.spogss.scrumble.data.Sprint
import com.spogss.scrumble.data.Task
import com.spogss.scrumble.data.User
import com.spogss.scrumble.enums.TaskState
import com.spogss.scrumble.viewItem.CustomSelectableItem
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import de.mrapp.android.dialog.MaterialDialog
import de.mrapp.android.dialog.ScrollableArea
import kotlinx.android.synthetic.main.fragment_projects.*
import java.text.SimpleDateFormat
import java.util.*

class CustomProjectOverviewAdapter<T>(private val data: MutableList<T>, private val context: Context): RecyclerView.Adapter<CustomProjectOverviewAdapter.ViewHolder>() {
    private val startCal = Calendar.getInstance()
    private val endCal = Calendar.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_list_project_overview, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        when(item) {
            is User -> {
                holder.textView1.text = item.username
                holder.textView2.visibility = View.GONE
            }
            is Sprint -> {
                val dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale("EN"))
                holder.textView1.text = "#${item.number}"
                holder.textView2.text = "${dateFormatter.format(item.startDate)} - ${dateFormatter.format(item.deadline)}"
                holder.textView2.visibility = View.VISIBLE
                holder.rlProjectOverview.setOnClickListener {
                    setupSprintPopup(item)
                }
            }
            is Task -> {
                holder.textView1.text = item.name
                holder.textView2.visibility = View.GONE
                holder.rlProjectOverview.setOnClickListener {
                    setupTaskPopup(item)
                }
            }
        }
    }

    private fun setupTaskPopup(task: Task) {
        val dialogBuilder = MaterialDialog.Builder(context)
        dialogBuilder.setTitle(task.name)
        dialogBuilder.setTitleColor(ContextCompat.getColor(context, R.color.colorAccent))
        dialogBuilder.setMessage(task.info)
        dialogBuilder.setPositiveButton(R.string.close, null)
        dialogBuilder.setButtonTextColor(ContextCompat.getColor(context, R.color.colorAccent))

        val responsible = context.resources.getString(R.string.responsible_double_dot)
        val verify = context.resources.getString(R.string.verify_double_dot)
        val rejections = context.resources.getString(R.string.rejections_double_dot, if(task.rejections != 1) "s" else "")

        val boldSpan = StyleSpan(Typeface.BOLD)
        val responsibleString = SpannableStringBuilder().append("$responsible ").append(task.responsible.username, boldSpan, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val verifyString  = SpannableStringBuilder().append("$verify ").append(task.verify.username, boldSpan, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val rejectionString = SpannableStringBuilder().append("$rejections ").append(task.rejections.toString(), boldSpan, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val customView = View.inflate(context, R.layout.popup_task, null)
        (customView.findViewById(R.id.popup_view_task_responsible) as TextView).text = responsibleString
        (customView.findViewById(R.id.popup_view_task_verify) as TextView).text = verifyString
        (customView.findViewById(R.id.popup_view_task_rejection) as TextView).text = rejectionString

        dialogBuilder.setView(customView)
        dialogBuilder.setScrollableArea(ScrollableArea.Area.CONTENT)

        val dialog = dialogBuilder.create()
        dialog.show()
    }

    private fun setupSprintPopup(sprint: Sprint) {
        val dialogBuilder = MaterialDialog.Builder(context!!)
        dialogBuilder.setTitle(context.resources.getString(R.string.sprint))
        dialogBuilder.setTitleColor(ContextCompat.getColor(context, R.color.colorAccent))
        dialogBuilder.setPositiveButton(android.R.string.ok, null)
        dialogBuilder.setNegativeButton(android.R.string.cancel, null)
        dialogBuilder.setButtonTextColor(ContextCompat.getColor(context, R.color.colorAccent))
        dialogBuilder.setCanceledOnTouchOutside(false)

        val customView = View.inflate(context, R.layout.popup_add_sprint, null)
        val sprintNumberEditText = customView.findViewById<MaterialEditText>(R.id.popup_add_sprint_number)
        val selectListTask = customView.findViewById<RecyclerView>(R.id.popup_add_sprint_tasks)

        //TODO: set next sprint number
        sprintNumberEditText.setText(sprint.number.toString())

        setupDatePicker(customView, sprint)

        val adapter = FastItemAdapter<CustomSelectableItem>()
        adapter.withSelectable(true)
        adapter.withOnPreClickListener { _, _, _, _ -> true }
        adapter.withEventHook(CustomSelectableItem(null).CheckBoxClickEvent())
        adapter.add(ScrumbleController.tasks.filter { it.state == TaskState.PRODUCT_BACKLOG || (it.sprint != null && it.sprint == sprint.id) }.map { CustomSelectableItem(it) })

        adapter.adapterItems.forEachIndexed { index, item ->
            if(item.task != null && item.task.state != TaskState.PRODUCT_BACKLOG)
                (adapter.getExtension(SelectExtension::class.java as Class<SelectExtension<CustomSelectableItem>>) as SelectExtension<CustomSelectableItem>).select(index)
        }

        selectListTask.layoutManager = LinearLayoutManager(context)
        selectListTask.itemAnimator = DefaultItemAnimator()
        selectListTask.adapter = adapter

        dialogBuilder.setScrollableArea(ScrollableArea.Area.CONTENT)
        dialogBuilder.setView(customView)

        val dialog = dialogBuilder.create()
        dialog.setOnShowListener {
            dialog.getButton(MaterialDialog.BUTTON_POSITIVE).setOnClickListener { _ ->
                if(sprintButtonClick(customView)) {
                    dialog.dismiss()
                }
            }
        }

        dialog.show()
    }
    private fun setupDatePicker(customView: View, sprint: Sprint) {
        val startDateEditText = customView.findViewById<MaterialEditText>(R.id.popup_add_sprint_start_date)
        val endDateEditText = customView.findViewById<MaterialEditText>(R.id.popup_add_sprint_deadline)

        val dateFormatter = SimpleDateFormat("EEEE, dd.MM.yyyy", Locale("EN"))
        val calender = Calendar.getInstance()

        calender.time = sprint.startDate
        startDateEditText.setText(dateFormatter.format(calender.time))
        calender.time = sprint.deadline
        endDateEditText.setText(dateFormatter.format(calender.time))

        val endPicker = DatePickerDialog.newInstance({ _, year, monthOfYear, dayOfMonth ->
            endCal.set(year, monthOfYear, dayOfMonth)
            endDateEditText.setText(dateFormatter.format(endCal.time))
        }, calender)
        endCal.set(calender.get(Calendar.YEAR), calender.get(Calendar.MONTH), calender.get(Calendar.DAY_OF_MONTH))
        endPicker.minDate = endCal
        endPicker.setTitle(context.resources.getString(R.string.deadline))
        endPicker.vibrate(false)

        calender.time = sprint.startDate
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
    private fun sprintButtonClick(customView: View): Boolean {
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
        }
        //TODO: update sprint

        return closePopup
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var rlProjectOverview = v.findViewById<View>(R.id.rl_project_overview) as RelativeLayout
        var textView1 = v.findViewById<View>(R.id.project_overview_text_view) as TextView
        var textView2 = v.findViewById<View>(R.id.project_overview_text_view_2) as TextView
    }
}