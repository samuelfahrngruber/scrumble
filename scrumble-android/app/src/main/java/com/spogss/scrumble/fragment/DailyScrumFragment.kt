package com.spogss.scrumble.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.spogss.scrumble.R
import com.spogss.scrumble.adapter.CustomTimeLineAdapter
import com.spogss.scrumble.controller.ScrumbleController
import com.spogss.scrumble.data.DailyScrum
import kotlinx.android.synthetic.main.fragment_daily_scrum.*
import xyz.sangcomz.stickytimelineview.RecyclerSectionItemDecoration
import xyz.sangcomz.stickytimelineview.model.SectionInfo


class DailyScrumFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_daily_scrum, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if(ScrumbleController.isCurrentSprintSpecified())
            setupTimeLine()
        else
            text_view_no_current_project.visibility = View.VISIBLE

        super.onViewCreated(view, savedInstanceState)
    }

    private fun setupTimeLine() {
        time_line.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        time_line.addItemDecoration(getSectionCallback(ScrumbleController.dailyScrumEntries))
        time_line.adapter = CustomTimeLineAdapter(layoutInflater, ScrumbleController.dailyScrumEntries, R.layout.item_list_time_line)
    }

    private fun getSectionCallback(dailyScrumEntries: List<DailyScrum>): RecyclerSectionItemDecoration.SectionCallback {
        return object : RecyclerSectionItemDecoration.SectionCallback {
            //In your data, implement a method to determine if this is a section.
            override fun isSection(position: Int): Boolean =
                    dailyScrumEntries[position].date != dailyScrumEntries[position - 1].date

            //Implement a method that returns a SectionHeader.
            override fun getSectionHeader(position: Int): SectionInfo? {
                return SectionInfo(dailyScrumEntries[position].formattedDate(), dailyScrumEntries[position].weekday())
            }
        }
    }
}