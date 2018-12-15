package com.spogss.scrumble.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import com.spogss.scrumble.R
import com.spogss.scrumble.data.User
import com.woxthebox.draglistview.DragItemAdapter
import com.woxthebox.draglistview.swipe.ListSwipeItem

class CustomSwipeItemAdapter: DragItemAdapter<Pair<Int, User>, CustomSwipeItemAdapter.ViewHolder> {
    private var res = 0
    private var mGrabHandleId = 0
    private var mDragOnLongPress = false
    private var context: Context
    private var popup = false

    constructor(list: MutableList<Pair<Int, User>>, res: Int, mGrabHandleId: Int, mDragOnLongPress: Boolean, context: Context, popup: Boolean = false) {
        this.res = res
        this.mGrabHandleId = mGrabHandleId
        this.mDragOnLongPress = mDragOnLongPress
        this.context = context
        this.popup = popup
        itemList = list
    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomSwipeItemAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(res, parent, false)
        (view.findViewById<ListSwipeItem>(R.id.swipe_list_item_add_project)).supportedSwipeDirection = ListSwipeItem.SwipeDirection.LEFT
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(@NonNull holder: CustomSwipeItemAdapter.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = mItemList[position].second

        holder.text.text = item.name

        if(popup)
            (holder.text.parent as LinearLayout).setPadding(96, 32, 96, 32)
        else
            holder.text.setTextColor(ContextCompat.getColor(context, R.color.colorAccent))

        holder.itemView.tag = mItemList[position]
    }

    override fun getUniqueItemId(position: Int): Long {
        return mItemList[position].first.toLong()
    }

    inner class ViewHolder(itemView: View) : DragItemAdapter.ViewHolder(itemView, mGrabHandleId, mDragOnLongPress) {
        var text: TextView = itemView.findViewById(R.id.list_item_text_view)
    }
}