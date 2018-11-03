package com.spogss.scrumble.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.spogss.scrumble.R
import com.spogss.scrumble.controller.PopupController
import com.spogss.scrumble.data.Sprint
import com.spogss.scrumble.data.Task
import com.spogss.scrumble.data.User

class CustomProjectOverviewAdapter<T>(private val data: MutableList<T>, private val context: Context): RecyclerView.Adapter<CustomProjectOverviewAdapter.ViewHolder>() {
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
                holder.textView1.text = item.toString()
                holder.textView2.visibility = View.GONE
            }
            is Sprint -> {
                holder.textView1.text = item.toString()
                holder.textView2.text = item.timeSpan()
                holder.textView2.visibility = View.VISIBLE
                holder.rlProjectOverview.setOnClickListener {
                    PopupController.setupSprintPopup(context, { }, item)
                }
            }
            is Task -> {
                holder.textView1.text = item.name
                holder.textView2.visibility = View.GONE
                holder.rlProjectOverview.setOnClickListener {
                    PopupController.setupTaskPopup(context, { }, item)
                }
            }
        }
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var rlProjectOverview = v.findViewById<View>(R.id.rl_project_overview) as RelativeLayout
        var textView1 = v.findViewById<View>(R.id.project_overview_text_view) as TextView
        var textView2 = v.findViewById<View>(R.id.project_overview_text_view_2) as TextView
    }
}