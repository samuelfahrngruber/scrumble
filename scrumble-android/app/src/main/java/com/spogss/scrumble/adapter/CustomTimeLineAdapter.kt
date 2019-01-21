package com.spogss.scrumble.adapter

import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.rengwuxian.materialedittext.MaterialEditText
import com.spogss.scrumble.R
import com.spogss.scrumble.controller.MiscUIController
import com.spogss.scrumble.controller.ScrumbleController
import com.spogss.scrumble.data.DailyScrum
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner

class CustomTimeLineAdapter(private val layoutInflater: LayoutInflater, private val data: MutableList<DailyScrum>,
                            @param:LayoutRes private val layoutRes: Int)
                            : RecyclerView.Adapter<CustomTimeLineAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = layoutInflater.inflate(layoutRes, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dailyScrumEntry = data[position]
        holder.name.text = dailyScrumEntry.user.toString()
        holder.description.setText(dailyScrumEntry.description)

        holder.description.setOnFocusChangeListener { _, b ->
            if(!b) {
                dailyScrumEntry.description = holder.description.text.toString().trim()
                setCardColor(dailyScrumEntry, holder)

                ScrumbleController.updateDailyScrum(dailyScrumEntry.id, dailyScrumEntry, { }, {
                    MiscUIController.showError(layoutInflater.context, it)
                })
            }
        }

        val taskList = mutableListOf("")
        taskList.addAll(ScrumbleController.tasks.map { it.toString() })
        holder.task.setAdapter(ArrayAdapter(layoutInflater.context, android.R.layout.simple_spinner_dropdown_item, taskList))

        holder.task.setOnFocusChangeListener { _, b ->
            if(!b) {
                val taskName = holder.task.text.toString().trim()
                dailyScrumEntry.task = if(taskName == "") null else ScrumbleController.tasks.find { it.toString() == taskName }
                setCardColor(dailyScrumEntry, holder)

                ScrumbleController.updateDailyScrum(dailyScrumEntry.id, dailyScrumEntry, { }, {
                    MiscUIController.showError(layoutInflater.context, it)
                })
            }
        }

        if(dailyScrumEntry.task != null)
            holder.task.setText(dailyScrumEntry.task!!.toString())

        setCardColor(dailyScrumEntry, holder)
    }

    override fun getItemCount(): Int = data.size

    private fun setCardColor(dailyScrumEntry: DailyScrum, holder: ViewHolder) {
        when {
            dailyScrumEntry.description.trim() == layoutInflater.context.resources.getString(R.string.missing) -> holder.cardView.setCardBackgroundColor(ContextCompat.getColor(layoutInflater.context, R.color.colorLightRed))
            dailyScrumEntry.description.trim() == "" -> holder.cardView.setCardBackgroundColor(ContextCompat.getColor(layoutInflater.context, R.color.colorLightOrange))
            else -> holder.cardView.setCardBackgroundColor(ContextCompat.getColor(layoutInflater.context, R.color.colorPrimary))
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardView = view.findViewById<View>(R.id.card_view) as CardView
        val name = view.findViewById<View>(R.id.time_line_name) as TextView
        val task = view.findViewById<View>(R.id.time_line_task) as MaterialBetterSpinner
        val description = view.findViewById<View>(R.id.time_line_description) as MaterialEditText
    }
}