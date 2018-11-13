package com.spogss.scrumble.viewItem

import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.mikepenz.fastadapter.select.SelectExtension
import com.spogss.scrumble.R
import com.spogss.scrumble.data.Task


class CustomSelectableItem(val task: Task?): AbstractItem<CustomSelectableItem, CustomSelectableItem.ViewHolder>() {
    override fun getType(): Int {
        return R.id.select_item
    }

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    override fun getLayoutRes(): Int {
        return R.layout.item_list_selectable
    }

    override fun bindView(viewHolder: ViewHolder, payloads: List<Any>) {
        super.bindView(viewHolder, payloads)

        viewHolder.checkBox.isChecked = isSelected
        viewHolder.taskName.text = task?.toString() ?: ""
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val taskName = view.findViewById<View>(R.id.select_list_item_task) as TextView
        val checkBox = view.findViewById<View>(R.id.select_list_item_checkbox) as CheckBox
    }

    inner class CheckBoxClickEvent: ClickEventHook<CustomSelectableItem>() {
        override fun onBind(viewHolder: RecyclerView.ViewHolder): View? {
            if (viewHolder is CustomSelectableItem.ViewHolder) {
                return viewHolder.checkBox
            }
            return null
        }

        override fun onClick(v: View, position: Int, fastAdapter: FastAdapter<CustomSelectableItem>, item: CustomSelectableItem) {
            (fastAdapter.getExtension(SelectExtension::class.java as Class<SelectExtension<CustomSelectableItem>>) as SelectExtension<CustomSelectableItem>).toggleSelection(position)
        }
    }
}