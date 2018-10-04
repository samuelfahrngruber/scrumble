package com.spogss.scrumble.adapter

import android.view.View
import android.widget.Toast
import android.widget.TextView
import com.spogss.scrumble.R
import com.spogss.scrumble.data.UserStory
import com.woxthebox.draglistview.DragItemAdapter
import android.support.annotation.NonNull
import android.view.LayoutInflater
import android.view.ViewGroup





class CustomDragItemAdapter
    : DragItemAdapter<Pair<Int, UserStory>, CustomDragItemAdapter.ViewHolder> {
    private var res = 0
    private var mGrabHandleId = 0
    private var mDragOnLongPress = false

    constructor(list: MutableList<Pair<Int, UserStory>>, res: Int, mGrabHandleId: Int, mDragOnLongPress: Boolean) {
        this.res = res
        this.mGrabHandleId = mGrabHandleId
        this.mDragOnLongPress = mDragOnLongPress
        itemList = list
    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomDragItemAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(res, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val userStory = mItemList[position].second
        holder.text.text = userStory.name
        holder.itemView.tag = mItemList[position]
    }

    override fun getUniqueItemId(position: Int): Long {
        return mItemList[position].first.toLong()
    }

    inner class ViewHolder(itemView: View) : DragItemAdapter.ViewHolder(itemView, mGrabHandleId, mDragOnLongPress) {
        var text: TextView = itemView.findViewById(R.id.column_item_text_view)

        override fun onItemClicked(view: View) {
            Toast.makeText(view.context, "Item clicked", Toast.LENGTH_SHORT).show()
        }

        override fun onItemLongClicked(view: View): Boolean {
            return true
        }
    }
}