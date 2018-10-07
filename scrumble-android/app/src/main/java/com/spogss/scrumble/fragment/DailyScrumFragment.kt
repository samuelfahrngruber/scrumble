package com.spogss.scrumble.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.spogss.scrumble.R
import com.spogss.scrumble.adapter.CustomTimeLineAdapter
import com.spogss.scrumble.data.DailyScrum
import com.spogss.scrumble.data.Task
import com.spogss.scrumble.data.User
import com.spogss.scrumble.enums.DailyScrumState
import com.spogss.scrumble.enums.UserStoryState
import kotlinx.android.synthetic.main.fragment_daily_scrum.*
import xyz.sangcomz.stickytimelineview.RecyclerSectionItemDecoration
import xyz.sangcomz.stickytimelineview.model.SectionInfo
import java.text.SimpleDateFormat
import java.util.*


class DailyScrumFragment: Fragment() {
    private val dailyScrumEntries = mutableListOf<DailyScrum>()
    private val users = mutableListOf<User>()
    private var userStories = mutableListOf<Task>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        createTestData()
        return inflater.inflate(R.layout.fragment_daily_scrum, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupTimeLine()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setupTimeLine() {
        time_line.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        time_line.addItemDecoration(getSectionCallback(dailyScrumEntries))
        time_line.adapter = CustomTimeLineAdapter(layoutInflater, dailyScrumEntries, R.layout.time_line_item)
    }

    private fun getSectionCallback(dailyScrumEntries: List<DailyScrum>): RecyclerSectionItemDecoration.SectionCallback {
        return object : RecyclerSectionItemDecoration.SectionCallback {
            //In your data, implement a method to determine if this is a section.
            override fun isSection(position: Int): Boolean =
                    dailyScrumEntries[position].date != dailyScrumEntries[position - 1].date

            //Implement a method that returns a SectionHeader.
            override fun getSectionHeader(position: Int): SectionInfo? {
                val dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale("EN"))
                val weekdayFormatter = SimpleDateFormat("EEEE", Locale("EN"))

                return SectionInfo(dateFormatter.format(dailyScrumEntries[position].date),
                        weekdayFormatter.format(dailyScrumEntries[position].date))
            }
        }
    }



    private fun createTestData() {
        users.clear()
        dailyScrumEntries.clear()

        val sam = User(0, "Sam", "entersamspassworthere")
        val pauli = User(1, "Pauli", "phaulson")
        val simon = User(2, "Simon", "hamsterbacke")
        val webi = User(3, "Webi", "livepoolftw")
        val savan = User(4, "Savan", "chelseafcgothistory")

        users.add(sam)
        users.add(pauli)
        users.add(simon)
        users.add(webi)
        users.add(savan)


        val scrumbleGUI = Task(0, pauli, sam, "GUI Programmieren", "Hierbei handelt es sich um die Android GUI",
                0, UserStoryState.IN_PROGRESS, 0, 0, 0)
        val scrumbleDB = Task(1, webi, simon, "Datenbank aufsetzten", "Inklusive Trigger, Sequenzen usw...",
                0, UserStoryState.TO_VERIFY, 1, 0, 0)
        val scrumbleWebservice = Task(2, webi, pauli, "Webservice implementieren", "Der Webservice connected Client mit Datenbank",
                0, UserStoryState.SPRINT_BACKLOG, 0, null, 0)
        val scrumbleTesting = Task(3, simon, sam, "Testen", "Sollen wir Unit-Tests machen? Keine Ahnung",
                0, UserStoryState.SPRINT_BACKLOG, 0, null, 0)
        val scrumblePlaning = Task(4, sam, pauli, "Planung", "Geplant wird immer, weil wir mit Scrum arbeiten, lol. Hier steht auch noch mehr Text",
                0, UserStoryState.IN_PROGRESS, 2, 0, 0)
        userStories.add(scrumbleGUI)
        userStories.add(scrumbleDB)
        userStories.add(scrumbleWebservice)
        userStories.add(scrumbleTesting)
        userStories.add(scrumblePlaning)


        val today = Calendar.getInstance()

        val ds1 = DailyScrum(0, sam, today.time, DailyScrumState.PRESENT, scrumbleGUI, "doing nothing, lol")
        val ds2 = DailyScrum(1, pauli, today.time, DailyScrumState.PRESENT, scrumbleGUI, "webservice")
        val ds3 = DailyScrum(2, simon, today.time, DailyScrumState.ABSENT, null, "implementierung des clientseitigen was weiß ich was")
        val ds4 = DailyScrum(3, webi, today.time, DailyScrumState.PRESENT, scrumblePlaning)

        today.add(Calendar.DAY_OF_YEAR, -1)
        val ds5 = DailyScrum(4, sam, today.time, DailyScrumState.PRESENT, scrumbleDB, "hier steht text zum testen")
        val ds6 = DailyScrum(5, pauli, today.time, DailyScrumState.ABSENT, scrumbleTesting, "lali bringt die milch zum grinsen")
        val ds7 = DailyScrum(6, simon, today.time, DailyScrumState.PRESENT, scrumbleDB, "datenbank scripts schreiben")
        val ds8 = DailyScrum(7, webi, today.time, DailyScrumState.ABSENT, null, "1, 2, sxtn kommt vorbei, 3, 4, klopft an deine tür")

        today.add(Calendar.DAY_OF_YEAR, -1)
        val ds9 = DailyScrum(4, sam, today.time, DailyScrumState.PRESENT, null, "5, 6, jetzt gibts wieder stress")
        val ds10 = DailyScrum(5, pauli, today.time, DailyScrumState.PRESENT, scrumblePlaning)
        val ds11 = DailyScrum(6, simon, today.time, DailyScrumState.PRESENT, scrumblePlaning, "blablabla hahah lol rofl xd lmao")
        val ds12 = DailyScrum(7, webi, today.time, DailyScrumState.PRESENT, scrumbleDB, "hallo leute")

        dailyScrumEntries.add(ds1)
        dailyScrumEntries.add(ds2)
        dailyScrumEntries.add(ds3)
        dailyScrumEntries.add(ds4)
        dailyScrumEntries.add(ds5)
        dailyScrumEntries.add(ds6)
        dailyScrumEntries.add(ds7)
        dailyScrumEntries.add(ds8)
        dailyScrumEntries.add(ds9)
        dailyScrumEntries.add(ds10)
        dailyScrumEntries.add(ds11)
        dailyScrumEntries.add(ds12)
    }
}