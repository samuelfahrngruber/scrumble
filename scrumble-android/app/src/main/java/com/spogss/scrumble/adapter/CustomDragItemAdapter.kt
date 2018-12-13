package com.spogss.scrumble.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.rengwuxian.materialedittext.MaterialEditText
import com.spogss.scrumble.R
import com.spogss.scrumble.controller.MiscUIController
import com.spogss.scrumble.controller.PopupController
import com.spogss.scrumble.controller.ScrumbleController
import com.spogss.scrumble.data.Task
import com.spogss.scrumble.fragment.MyTasksFragment
import com.spogss.scrumble.fragment.ScrumBoardFragment
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner
import com.woxthebox.draglistview.DragItemAdapter


class CustomDragItemAdapter
    : DragItemAdapter<Pair<Int, Task>, CustomDragItemAdapter.ViewHolder> {
    private var res = 0
    private var mGrabHandleId = 0
    private var mDragOnLongPress = false
    private var context: Context
    private var fragment: Fragment

    constructor(list: MutableList<Pair<Int, Task>>, res: Int, mGrabHandleId: Int, mDragOnLongPress: Boolean, context: Context, fragment: Fragment) {
        this.res = res
        this.mGrabHandleId = mGrabHandleId
        this.mDragOnLongPress = mDragOnLongPress
        this.context = context
        this.fragment = fragment
        itemList = list
    }


    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomDragItemAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(res, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val task = mItemList[position].second

        if(task.id < 0) {
            holder.text.text = task.name
            holder.text.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
            holder.text.textSize = 20f
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent))
            holder.cardView.cardElevation = 1f
            holder.cardView.radius = 0f
        }
        else {
            holder.text.text = task.toString()
            holder.cardView.foreground = ContextCompat.getDrawable(context, R.drawable.sc_card_view_selector)
            holder.cardView.setCardBackgroundColor(Color.parseColor(task.color))
        }

        holder.itemView.tag = mItemList[position].second
    }

    override fun getUniqueItemId(position: Int): Long {
        return mItemList[position].first.toLong()
    }

    inner class ViewHolder(itemView: View) : DragItemAdapter.ViewHolder(itemView, mGrabHandleId, mDragOnLongPress) {
        val text = itemView.findViewById<View>(R.id.column_item_text_view) as TextView
        val cardView = itemView.findViewById<View>(R.id.card_view_item) as CardView

        override fun onItemClicked(view: View) {
            val task = view.tag as Task

            if(task.id >= 0)
                PopupController.setupTaskPopup(context, { updateTask(task, it) }, task)
        }

        private fun updateTask(task: Task, view: View) {
            val name = view.findViewById<MaterialEditText>(R.id.popup_add_task_name).text.toString()
            val info = view.findViewById<MaterialEditText>(R.id.popup_add_task_info).text.toString()
            val responsible = view.findViewById<MaterialBetterSpinner>(R.id.popup_add_task_responsible).text.toString()
            val verify = view.findViewById<MaterialBetterSpinner>(R.id.popup_add_task_verify).text.toString()
            val color = view.findViewById<TextView>(R.id.popup_add_task_color).tag as String

            val responsibleUser = if(responsible.isNotEmpty()) ScrumbleController.users.find { it.name == responsible }!! else null
            val verifyUser = if(verify.isNotEmpty()) ScrumbleController.users.find { it.name == verify }!! else null

            val oldName = task.name
            val oldColor = task.color
            val oldRespName = task.responsible?.name?: ""
            val oldVerName = task.verify?.name?: ""

            if(task.name != name || task.info != info || task.color != color || task.responsible != responsibleUser || task.verify != verifyUser) {
                task.name = name
                task.info = info
                task.color = color
                task.responsible = responsibleUser
                task.verify = verifyUser

                ScrumbleController.updateTask(task.id, task, {}, { MiscUIController.showError(context, it) } )

                if(task.name != oldName || task.color != oldColor || task.responsible?.name?: "" != oldRespName || task.verify?.name?: "" != oldVerName)
                if(fragment is ScrumBoardFragment)
                    (fragment as ScrumBoardFragment).setupBoardView()
                else if(fragment is MyTasksFragment)
                    (fragment as MyTasksFragment).setupDragListView()
            }
        }

        override fun onItemLongClicked(view: View): Boolean {
            val task = view.tag as Task

            return task.id >= 0
        }
    }
}