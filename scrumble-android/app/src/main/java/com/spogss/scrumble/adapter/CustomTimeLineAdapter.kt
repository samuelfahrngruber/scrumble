package com.spogss.scrumble.adapter

import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.spogss.scrumble.R
import com.spogss.scrumble.data.DailyScrum

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
        holder.description.text = dailyScrumEntry.description

        if(dailyScrumEntry.task != null)
            holder.task.text = layoutInflater.context.resources.getString(R.string.task_double_dot, dailyScrumEntry.task.name)
        else
            holder.task.visibility = View.GONE

        when {
            dailyScrumEntry.description.trim() == layoutInflater.context.resources.getString(R.string.missing) -> holder.cardView.setCardBackgroundColor(ContextCompat.getColor(layoutInflater.context, R.color.colorLightRed))
            dailyScrumEntry.description.trim() == "" -> holder.cardView.setCardBackgroundColor(ContextCompat.getColor(layoutInflater.context, R.color.colorLightOrange))
            else -> holder.cardView.setCardBackgroundColor(ContextCompat.getColor(layoutInflater.context, R.color.colorPrimary))
        }
    }

    override fun getItemCount(): Int = data.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardView = view.findViewById<View>(R.id.card_view) as CardView
        val name = view.findViewById<View>(R.id.time_line_name) as TextView
        val task = view.findViewById<View>(R.id.time_line_task) as TextView
        val description = view.findViewById<View>(R.id.time_line_description) as TextView
    }
}