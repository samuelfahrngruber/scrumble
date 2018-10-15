package com.spogss.scrumble.adapter

import android.content.Context
import android.support.annotation.LayoutRes
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.spogss.scrumble.R
import com.spogss.scrumble.data.DailyScrum
import com.spogss.scrumble.enums.DailyScrumState

class CustomTimeLineAdapter(private val layoutInflater: LayoutInflater, private val data: MutableList<DailyScrum>,
                            @param:LayoutRes private val layoutRes: Int)
                            : RecyclerView.Adapter<CustomTimeLineAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = layoutInflater.inflate(layoutRes, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dailyScrumEntry = data[position]
        holder.name.text = dailyScrumEntry.teamMember.username

        if(dailyScrumEntry.task != null)
            holder.task.text = layoutInflater.context.resources.getString(R.string.task_double_dot, dailyScrumEntry.task.name)
        else
            holder.task.visibility = View.GONE

        if(dailyScrumEntry.description != null)
            holder.description.text = dailyScrumEntry.description
        else
            holder.description.visibility = View.GONE

        if(dailyScrumEntry.state == DailyScrumState.ABSENT)
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(layoutInflater.context, R.color.colorLightRed))
        else
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(layoutInflater.context, R.color.colorPrimary))
    }

    override fun getItemCount(): Int = data.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardView = view.findViewById<View>(R.id.card_view) as CardView
        val name = view.findViewById<View>(R.id.time_line_name) as TextView
        val task = view.findViewById<View>(R.id.time_line_task) as TextView
        val description = view.findViewById<View>(R.id.time_line_description) as TextView
    }
}