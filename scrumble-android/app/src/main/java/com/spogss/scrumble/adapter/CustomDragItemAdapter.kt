package com.spogss.scrumble.adapter

import android.content.Context
import android.view.View
import android.widget.Toast
import android.widget.TextView
import com.spogss.scrumble.R
import com.spogss.scrumble.data.UserStory
import com.woxthebox.draglistview.DragItemAdapter
import android.support.annotation.NonNull
import android.support.v4.content.ContextCompat
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.view.LayoutInflater
import android.view.ViewGroup
import de.mrapp.android.dialog.MaterialDialog
import java.lang.StringBuilder
import android.text.style.StyleSpan
import de.mrapp.android.dialog.ScrollableArea


class CustomDragItemAdapter
    : DragItemAdapter<Pair<Int, UserStory>, CustomDragItemAdapter.ViewHolder> {
    private var res = 0
    private var mGrabHandleId = 0
    private var mDragOnLongPress = false
    private lateinit var context: Context

    constructor(list: MutableList<Pair<Int, UserStory>>, res: Int, mGrabHandleId: Int, mDragOnLongPress: Boolean, context: Context) {
        this.res = res
        this.mGrabHandleId = mGrabHandleId
        this.mDragOnLongPress = mDragOnLongPress
        this.context = context
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
        holder.itemView.tag = mItemList[position].second
    }

    override fun getUniqueItemId(position: Int): Long {
        return mItemList[position].first.toLong()
    }

    inner class ViewHolder(itemView: View) : DragItemAdapter.ViewHolder(itemView, mGrabHandleId, mDragOnLongPress) {
        var text: TextView = itemView.findViewById(R.id.column_item_text_view)

        override fun onItemClicked(view: View) {
            val userStory = view.tag as UserStory

            setupPopup(userStory)
        }

        override fun onItemLongClicked(view: View): Boolean {
            return true
        }

        private fun setupPopup(userStory: UserStory) {
            val dialogBuilder = MaterialDialog.Builder(context)
            dialogBuilder.setTitle(userStory.name)
            dialogBuilder.setTitleColor(ContextCompat.getColor(context, R.color.colorAccent))
            dialogBuilder.setMessage(userStory.info)
            dialogBuilder.setPositiveButton("Close", null)
            dialogBuilder.setButtonTextColor(ContextCompat.getColor(context, R.color.colorAccent))

            val responsible = context.resources.getString(R.string.popup_responsible)
            val verify = context.resources.getString(R.string.popup_verify)
            val rejections = context.resources.getString(R.string.popup_rejections, if(userStory.rejections != 1) "s" else "")

            val boldSpan = StyleSpan(android.graphics.Typeface.BOLD)
            val responsibleString = SpannableStringBuilder().append("$responsible ").append(userStory.responsible.username, boldSpan, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            val verifyString  = SpannableStringBuilder().append("$verify ").append(userStory.verify.username, boldSpan, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            val rejectionString = SpannableStringBuilder().append("$rejections ").append(userStory.rejections.toString(), boldSpan, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            val customView = View.inflate(context, R.layout.popup_user_story_content, null)
            (customView.findViewById(R.id.popup_user_story_text_view_responsible) as TextView).text = responsibleString
            (customView.findViewById(R.id.popup_user_story_text_view_verify) as TextView).text = verifyString
            (customView.findViewById(R.id.popup_user_story_text_view_rejections) as TextView).text = rejectionString

            dialogBuilder.setView(customView)
            dialogBuilder.setScrollableArea(ScrollableArea.Area.CONTENT)

            val dialog = dialogBuilder.create()
            dialog.show()
        }
    }
}