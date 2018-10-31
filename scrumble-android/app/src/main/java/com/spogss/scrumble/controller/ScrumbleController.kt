package com.spogss.scrumble.controller

import com.spogss.scrumble.data.*
import com.spogss.scrumble.enums.TaskState
import java.util.*

object ScrumbleController {
    val users = mutableListOf<User>()
    val projects = mutableListOf<Project>()
    val sprints = mutableListOf<Sprint>()
    val tasks = mutableListOf<Task>()
    val dailyScrumEntries = mutableListOf<DailyScrum>()

    var currentProject: Project? = null
    var currentSprint: Sprint? = null

    init {
        createTestData()
    }

    //TODO: remove when webservice call is possible
    private fun createTestData() {
        users.clear()
        projects.clear()
        sprints.clear()
        tasks.clear()

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

        val scrumble = Project(0, "Scrumble", sam, mutableListOf(pauli, simon, webi), null)
        val footballsimulator = Project(1, "Football Simulator", savan, mutableListOf(webi), null)
        projects.add(scrumble)
        projects.add(footballsimulator)


        val start = Calendar.getInstance()
        val end = Calendar.getInstance()

        end.add(Calendar.DAY_OF_MONTH, 14)
        val scrumble1 = Sprint(0, 1, start.time, end.time, 0)

        start.time = end.time
        end.add(Calendar.DAY_OF_MONTH, 14)
        val scrumble2 = Sprint(1, 2, start.time, end.time, 0)

        start.time = end.time
        end.add(Calendar.DAY_OF_MONTH, 14)
        val scrumble3 = Sprint(2, 3, start.time,end.time, 0)

        start.time = end.time
        end.add(Calendar.DAY_OF_MONTH, 14)
        val footballsimulator1 = Sprint(3, 1, start.time, end.time, 1)

        start.time = end.time
        end.add(Calendar.DAY_OF_MONTH, 14)
        val footballsimulator2 = Sprint(4, 2, start.time, end.time, 1)
        sprints.add(scrumble1)
        sprints.add(scrumble2)
        sprints.add(scrumble3)
        sprints.add(footballsimulator1)
        sprints.add(footballsimulator2)

        val scrumbleGUI = Task(0, pauli, sam, "GUI Programmieren", "Hierbei handelt es sich um die Android GUI",
                0, TaskState.IN_PROGRESS, 1, 0, 0)
        val scrumbleDB = Task(1, webi, simon, "Datenbank aufsetzten", "Inklusive Trigger, Sequenzen usw...",
                0, TaskState.TO_VERIFY, 1, 0, 0)
        val scrumbleWebservice = Task(2, webi, pauli, "Webservice implementieren", "Der Webservice connected Client mit Datenbank",
                0, TaskState.SPRINT_BACKLOG, 1, 0, 0)
        val scrumbleTesting = Task(3, simon, sam, "Testen", "Sollen wir Unit-Tests machen? Keine Ahnung",
                0, TaskState.SPRINT_BACKLOG, 2, 0, 0)
        val scrumblePlaning = Task(4, sam, pauli, "Planung", "Geplant wird immer, weil wir mit Scrum arbeiten, lol. Hier steht auch noch mehr Text",
                0, TaskState.IN_PROGRESS, 2, 0, 0)
        val scrumbleFinish = Task(4, sam, pauli, "Abschließen", "Abschluss des Projektes",
                0, TaskState.PRODUCT_BACKLOG, 0, null, 0)
        tasks.add(scrumbleGUI)
        tasks.add(scrumbleDB)
        tasks.add(scrumbleWebservice)
        tasks.add(scrumbleTesting)
        tasks.add(scrumblePlaning)
        tasks.add(scrumbleFinish)

        val today = Calendar.getInstance()

        val ds1 = DailyScrum(0, sam, today.time, "")
        val ds2 = DailyScrum(1, pauli, today.time, "MISSING")
        val ds3 = DailyScrum(2, simon, today.time, "database scripts schreiben", scrumbleDB)
        val ds4 = DailyScrum(3, webi, today.time, "backend programmieren. Um genau zu sein, eigentlich gar nichts tun")

        today.add(Calendar.DAY_OF_YEAR, -1)
        val ds5 = DailyScrum(4, sam, today.time, "webservce schnittstelle schreiben", scrumblePlaning)
        val ds6 = DailyScrum(5, pauli, today.time, "chillen")
        val ds7 = DailyScrum(6, simon, today.time, "auch irgendwas im backend", scrumbleWebservice)
        val ds8 = DailyScrum(7, webi, today.time, "connection testen", scrumbleTesting)

        today.add(Calendar.DAY_OF_YEAR, -1)
        val ds9 = DailyScrum(4, sam, today.time, "chef-arbeiten erledigen")
        val ds10 = DailyScrum(5, pauli, today.time, "")
        val ds11 = DailyScrum(6, simon, today.time, "MISSING")
        val ds12 = DailyScrum(7, webi, today.time, "boardview designen", scrumbleGUI)

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

        currentProject = projects[0]
        currentSprint = sprints[0]
    }
}