package com.spogss.scrumble.adapter

import android.animation.ObjectAnimator
import android.content.Context
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.aakira.expandablelayout.ExpandableLayoutListenerAdapter
import com.github.aakira.expandablelayout.ExpandableLinearLayout
import com.github.aakira.expandablelayout.Utils
import com.spogss.scrumble.R
import com.spogss.scrumble.controller.ScrumbleController
import com.spogss.scrumble.data.Sprint
import com.spogss.scrumble.data.Task
import com.spogss.scrumble.data.User


class CustomOverviewHeaderAdapter(private val headers: MutableList<String>, private val team: MutableList<User>, private val sprints: MutableList<Sprint>,
                                  val backlog: MutableList<Task>, private val context: Context): RecyclerView.Adapter<CustomOverviewHeaderAdapter.ViewHolder>() {
    private val expandState = SparseBooleanArray()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_list_overview_header, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val header = headers[position]
        holder.setIsRecyclable(false)
        holder.textView.text = header
        holder.expandableLayout.setInRecyclerView(true)
        holder.expandableLayout.setInterpolator(Utils.createInterpolator(Utils.FAST_OUT_SLOW_IN_INTERPOLATOR))
        holder.expandableLayout.isExpanded = expandState.get(position)
        holder.expandableLayout.setListener(object : ExpandableLayoutListenerAdapter() {
            override fun onPreOpen() {
                holder.createRotateAnimator(holder.rlTriangle, 0f, 180f).start()
                expandState.put(position, true)
            }

            override fun onPreClose() {
                holder.createRotateAnimator(holder.rlTriangle, 180f, 0f).start()
                expandState.put(position, false)
            }
        })

        holder.rlTriangle.rotation = if (expandState.get(position)) 180f else 0f
        holder.rlExpand.setOnClickListener { holder.expandableLayout.toggle() }

        setupRecyclerView(holder, header)
    }

    private fun setupRecyclerView(holder: CustomOverviewHeaderAdapter.ViewHolder, header: String) {
        //holder.listProjectOverview.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        holder.listProjectOverview.layoutManager = LinearLayoutManager(context)
        when(header) {
            context.resources.getString(R.string.team) -> {
                if(ScrumbleController.isCurrentProjectSpecified()) {
                    val teamAdapter = CustomProjectOverviewAdapter(team, context, this)
                    holder.listProjectOverview.adapter = teamAdapter
                }
            }
            context.resources.getString(R.string.sprints) -> {
                val sprintAdapter = CustomProjectOverviewAdapter(sprints, context, this)
                holder.listProjectOverview.adapter = sprintAdapter
            }
            context.resources.getString(R.string.product_backlog) -> {
                val backlogAdapter = CustomProjectOverviewAdapter(backlog, context, this)
                holder.listProjectOverview.adapter = backlogAdapter
            }
        }
    }


    override fun getItemCount(): Int {
        return headers.size
    }


    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var textView = v.findViewById<View>(R.id.text_view_header) as TextView
        var rlExpand = v.findViewById<View>(R.id.rl_expand_header) as RelativeLayout
        var rlTriangle = v.findViewById<View>(R.id.rl_triangle) as RelativeLayout
        var expandableLayout = v.findViewById<View>(R.id.expandableLayout_header) as ExpandableLinearLayout
        var listProjectOverview = v.findViewById<View>(R.id.list_project_overview) as RecyclerView

        fun createRotateAnimator(target: View, from: Float, to: Float): ObjectAnimator {
            val animator = ObjectAnimator.ofFloat(target, "rotation", from, to)
            animator.duration = 300
            animator.interpolator = Utils.createInterpolator(Utils.LINEAR_INTERPOLATOR)
            return animator
        }
    }
}