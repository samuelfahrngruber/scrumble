package com.spogss.scrumble.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.spogss.scrumble.R
import com.spogss.scrumble.adapter.CustomTimeLineAdapter
import com.spogss.scrumble.controller.PopupController
import com.spogss.scrumble.controller.ScrumbleController
import com.spogss.scrumble.data.DailyScrum
import com.spogss.scrumble.data.Sprint
import kotlinx.android.synthetic.main.fragment_daily_scrum.*
import xyz.sangcomz.stickytimelineview.RecyclerSectionItemDecoration
import xyz.sangcomz.stickytimelineview.TimeLineRecyclerView
import xyz.sangcomz.stickytimelineview.model.SectionInfo


class DailyScrumFragment : Fragment() {
    private var whichDailyScums: Any = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_daily_scrum, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if(whichDailyScums == "")
            whichDailyScums = resources.getString(R.string.whole_project)

        if (ScrumbleController.isCurrentProjectSpecified())
            setupTimeLine()
        else {
            daily_scrum_container.visibility = View.GONE
            text_view_no_current_project.visibility = View.VISIBLE
        }

        //val list = mutableListOf<Any>(resources.getString(R.string.whole_project), resources.getString(R.string.no_sprint))
        setupPopup()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun setupTimeLine() {
        time_line.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        time_line.addItemDecoration(getSectionCallback(getValidDailyScrums(whichDailyScums)))
        time_line.adapter = CustomTimeLineAdapter(layoutInflater, getValidDailyScrums(whichDailyScums) as MutableList<DailyScrum>, R.layout.item_list_time_line)

    }

    private fun setupPopup() {
        val list = mutableListOf<Any>(resources.getString(R.string.whole_project))
        list.addAll(ScrumbleController.sprints)

        which_daily_scrums_text_view.setOnClickListener {
            PopupController.setupRecyclerViewPopup(context!!, { item ->
                whichDailyScums = item
                which_daily_scrums_text_view.setText(whichDailyScums.toString())
                refresh()
            }, resources.getString(R.string.show_daily_scrum_of), list)
        }
    }

    private fun getValidDailyScrums(item: Any): List<DailyScrum> {
        val dailyScums = if(item is Sprint) {
            ScrumbleController.dailyScrumEntries.filter { ds ->
                ds.sprint == item && ds.date in item.startDate..item.deadline
            }
        }
        else {
            ScrumbleController.dailyScrumEntries.filter {
                it.sprint != null && it.date in it.sprint!!.startDate..it.sprint!!.deadline
            }
        }
        return dailyScums
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

    fun refresh() {
        if(!ScrumbleController.isCurrentProjectSpecified()) return

        val item = whichDailyScums

        for(idx in 0 until time_line.itemDecorationCount)
            time_line.removeItemDecoration(time_line.getItemDecorationAt(idx))

        time_line.addItemDecoration(getSectionCallback(getValidDailyScrums(item)))
        time_line.adapter = CustomTimeLineAdapter(layoutInflater, getValidDailyScrums(item) as MutableList<DailyScrum>, R.layout.item_list_time_line)
        time_line.adapter!!.notifyDataSetChanged()

        setupPopup()
    }
}