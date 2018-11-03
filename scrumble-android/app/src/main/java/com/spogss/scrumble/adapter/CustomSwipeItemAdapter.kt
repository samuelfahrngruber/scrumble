package com.spogss.scrumble.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import com.spogss.scrumble.R
import com.spogss.scrumble.data.Sprint
import com.spogss.scrumble.data.Task
import com.spogss.scrumble.data.User
import com.woxthebox.draglistview.DragItemAdapter
import com.woxthebox.draglistview.swipe.ListSwipeItem
import java.text.SimpleDateFormat
import java.util.*

class CustomSwipeItemAdapter<T>: DragItemAdapter<Pair<Int, T>, CustomSwipeItemAdapter<T>.ViewHolder> {
    private var res = 0
    private var mGrabHandleId = 0
    private var mDragOnLongPress = false
    private var context: Context

    constructor(list: MutableList<Pair<Int, T>>, res: Int, mGrabHandleId: Int, mDragOnLongPress: Boolean, context: Context) {
        this.res = res
        this.mGrabHandleId = mGrabHandleId
        this.mDragOnLongPress = mDragOnLongPress
        this.context = context
        itemList = list
    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomSwipeItemAdapter<T>.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(res, parent, false)
        (view.findViewById<ListSwipeItem>(R.id.swipe_list_item_add_project)).supportedSwipeDirection = ListSwipeItem.SwipeDirection.LEFT
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(@NonNull holder: CustomSwipeItemAdapter<T>.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = mItemList[position].second

        when (item) {
            is User -> holder.text.text = item.toString()
            is Task -> holder.text.text = item.name
            is Sprint -> holder.text.text = item.timeSpan()
        }

        holder.itemView.tag = mItemList[position]
    }

    override fun getUniqueItemId(position: Int): Long {
        return mItemList[position].first.toLong()
    }

    inner class ViewHolder(itemView: View) : DragItemAdapter.ViewHolder(itemView, mGrabHandleId, mDragOnLongPress) {
        var text: TextView = itemView.findViewById(R.id.list_item_text_view)
    }
}