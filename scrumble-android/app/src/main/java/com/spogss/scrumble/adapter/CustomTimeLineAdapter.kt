package com.spogss.scrumble.adapter

import android.util.Log
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.rengwuxian.materialedittext.MaterialEditText
import com.spogss.scrumble.R
import com.spogss.scrumble.controller.MiscUIController
import com.spogss.scrumble.controller.ScrumbleController
import com.spogss.scrumble.data.DailyScrum
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner
import es.dmoral.toasty.Toasty

class CustomTimeLineAdapter(private val layoutInflater: LayoutInflater, private var data: MutableList<DailyScrum>,
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

        holder.finish.setOnClickListener {
            val descr = holder.description.text.toString().trim()
            if(dailyScrumEntry.description != descr) {
                dailyScrumEntry.description = descr
                setCardColor(dailyScrumEntry, holder)

                ScrumbleController.updateDailyScrum(dailyScrumEntry.id, dailyScrumEntry, { }, {
                    MiscUIController.showError(layoutInflater.context, it)
                })
            }
        }

        val taskList = mutableListOf("")
        taskList.addAll(ScrumbleController.tasks.map { it.toString() })
        holder.task.setAdapter(ArrayAdapter(layoutInflater.context, android.R.layout.simple_spinner_item, taskList))
        holder.task.setOnItemClickListener { parent, view, position, id ->
            val taskName = taskList[position]
            if (dailyScrumEntry.task?.name != taskName) {
                dailyScrumEntry.task = if (taskName == "") null else ScrumbleController.tasks.find { it.toString() == taskName }
                setCardColor(dailyScrumEntry, holder)

                ScrumbleController.updateDailyScrum(dailyScrumEntry.id, dailyScrumEntry, { }, {
                    MiscUIController.showError(layoutInflater.context, it)
                })
            }
        }

        holder.task.setText(dailyScrumEntry.task?.toString())

        setCardColor(dailyScrumEntry, holder)
    }

    override fun getItemCount(): Int = data.size

    fun setItems(items: List<DailyScrum>) {
        data = items as MutableList<DailyScrum>
        data.sortWith(compareByDescending<DailyScrum> { it.date }.thenBy { it.user.name })
        notifyDataSetChanged()
    }

    private fun setCardColor(dailyScrumEntry: DailyScrum, holder: ViewHolder) {
        when {
            dailyScrumEntry.description.trim() == layoutInflater.context.resources.getString(R.string.missing) -> holder.cardView.setCardBackgroundColor(ContextCompat.getColor(layoutInflater.context, R.color.colorLightRed))
            dailyScrumEntry.description.trim() == "" -> holder.cardView.setCardBackgroundColor(ContextCompat.getColor(layoutInflater.context, R.color.colorLightOrange))
            else -> holder.cardView.setCardBackgroundColor(ContextCompat.getColor(layoutInflater.context, R.color.colorLightGreen))
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardView = view.findViewById<View>(R.id.card_view) as CardView
        val name = view.findViewById<View>(R.id.time_line_name) as TextView
        val task = view.findViewById<View>(R.id.time_line_task) as MaterialBetterSpinner
        val description = view.findViewById<View>(R.id.time_line_description) as MaterialEditText
        val finish = view.findViewById<ImageButton>(R.id.time_line_finish) as ImageButton
    }
}