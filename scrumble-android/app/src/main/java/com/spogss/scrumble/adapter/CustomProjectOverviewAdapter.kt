package com.spogss.scrumble.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.spogss.scrumble.R
import com.spogss.scrumble.activity.MainActivity
import com.spogss.scrumble.controller.PopupController
import com.spogss.scrumble.controller.ScrumbleController
import com.spogss.scrumble.controller.UIToScrumbleController
import com.spogss.scrumble.data.Sprint
import com.spogss.scrumble.data.Task
import com.spogss.scrumble.data.User
import com.spogss.scrumble.fragment.ProjectsFragment
import kotlinx.android.synthetic.main.fragment_projects.*

class CustomProjectOverviewAdapter<T>(private val data: MutableList<T>, private val context: Context,
                                      private val customOverviewHeaderAdapter: CustomOverviewHeaderAdapter,
                                      private val fragment: ProjectsFragment) : RecyclerView.Adapter<CustomProjectOverviewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_list_project_overview, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        when (item) {
            is User -> {
                holder.textView1.text = item.toString()
                holder.textView2.visibility = View.GONE
            }
            is Sprint -> {
                holder.textView1.text = item.toString()
                holder.textView2.text = item.timeSpan()
                holder.textView2.visibility = View.VISIBLE

                holder.rlProjectOverview.setOnClickListener {
                    PopupController.setupSprintPopup(context, { view ->
                        UIToScrumbleController.updateSprint(item, view, customOverviewHeaderAdapter, context) { isCurrent ->
                            if (item == ScrumbleController.currentProject!!.currentSprint) {
                                if(isCurrent) {
                                    fragment.sprint_text_view.setText(item.toString())
                                    fragment.sprint_text_view2.setText(item.timeSpan())
                                }
                                else {
                                    fragment.sprint_text_view.setText("")
                                    fragment.sprint_text_view2.setText("")
                                    ScrumbleController.currentProject!!.currentSprint = null
                                }
                            }
                            else {
                                if(isCurrent) {
                                    fragment.sprint_text_view.setText(item.toString())
                                    fragment.sprint_text_view2.setText(item.timeSpan())
                                    ScrumbleController.currentProject!!.currentSprint = item
                                }
                            }

                            customOverviewHeaderAdapter.notifyDataSetChanged()
                            notifyDataSetChanged()
                        }
                    }, item)
                }
            }
            is Task -> {
                holder.textView1.text = item.toString()
                holder.textView2.visibility = View.GONE
                holder.rlProjectOverview.setOnClickListener {
                    PopupController.setupTaskPopup(context, { view -> UIToScrumbleController.updateTask(item, view, context) { notifyDataSetChanged() } }, item)
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