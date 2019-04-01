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
import com.spogss.scrumble.R
import com.spogss.scrumble.controller.MiscUIController
import com.spogss.scrumble.controller.PopupController
import com.spogss.scrumble.controller.ScrumbleController
import com.spogss.scrumble.controller.UIToScrumbleController
import com.spogss.scrumble.data.Task
import com.spogss.scrumble.enums.TaskState
import com.spogss.scrumble.fragment.MyTasksFragment
import com.spogss.scrumble.fragment.ScrumBoardFragment
import com.woxthebox.draglistview.DragItemAdapter


class CustomDragItemAdapter: DragItemAdapter<Pair<Int, Task>, CustomDragItemAdapter.ViewHolder> {
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
                PopupController.setupTaskPopup(context, { UIToScrumbleController.updateTask(task, it, context) {
                    if(fragment is ScrumBoardFragment)
                        (fragment as ScrumBoardFragment).refresh()
                    else if(fragment is MyTasksFragment)
                        (fragment as MyTasksFragment).refresh()
                }
                }, {
                    PopupController.setupDeleteTaskPopup(context, { delete ->
                        if(delete) {
                            UIToScrumbleController.removeTask(task, context) {
                                if (fragment is ScrumBoardFragment)
                                    (fragment as ScrumBoardFragment).refresh()
                                else if (fragment is MyTasksFragment)
                                    (fragment as MyTasksFragment).refresh()
                            }
                        }
                        else {
                            task.state = TaskState.PRODUCT_BACKLOG
                            task.sprint = null
                            ScrumbleController.updateTask(task.id, task, {}, { MiscUIController.showError(context, it) } )

                            if(fragment is ScrumBoardFragment)
                                (fragment as ScrumBoardFragment).refresh()
                            else if(fragment is MyTasksFragment)
                                (fragment as MyTasksFragment).refresh()
                        }
                    })
                }, task)
        }

        override fun onItemLongClicked(view: View): Boolean {
            val task = view.tag as Task

            return task.id >= 0
        }
    }
}