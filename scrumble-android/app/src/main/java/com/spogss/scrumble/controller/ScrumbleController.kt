package com.spogss.scrumble.controller

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.spogss.scrumble.connection.ScrumbleConnection
import com.spogss.scrumble.data.*
import com.spogss.scrumble.enums.TaskState
import com.spogss.scrumble.json.ProjectDeserializer
import com.spogss.scrumble.json.SprintDeserializer
import com.spogss.scrumble.json.TaskDeserializer
import com.spogss.scrumble.json.TaskSerializer
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONObject
import java.util.*

object ScrumbleController {
    var users = mutableListOf<User>()
    var projects = mutableListOf<Project>()
    var sprints = mutableListOf<Sprint>()
    var tasks = mutableListOf<Task>()
    var dailyScrumEntries = mutableListOf<DailyScrum>()

    var currentProject: Project? = null
    lateinit var currentUser: User

    init {
        currentUser = User(23, "Paul", "lol")
        currentProject = Project(22, "Scrumble", currentUser)
        val sprint = Sprint(10, 1, Date(), Date(), currentProject!!)
        currentProject!!.currentSprint = sprint
        //createTestData()
    }

    fun loadTeam(projectId: Int, onSuccess: () -> Unit, onError: (message: String) -> Unit) {
        doAsync {
            val response = ScrumbleConnection.get("/project/$projectId/user")
            if(response.statusCode == 200) {
                users = Gson().fromJson(response.jsonArray.toString(), Array<User>::class.java).toMutableList()
                uiThread { onSuccess() }
            }
            else
                uiThread { onError(response.text) }
        }
    }

    fun loadProjects(userId: Int, onSuccess: () -> Unit, onError: (message: String) -> Unit) {
        doAsync {
            val response = ScrumbleConnection.get("/user/$userId/project")
            if(response.statusCode == 200) {
                val gson = GsonBuilder().registerTypeAdapter(Project::class.java, ProjectDeserializer()).create()
                projects = gson.fromJson(response.jsonArray.toString(), Array<Project>::class.java).toMutableList()

                uiThread { onSuccess() }
            }
            else
                uiThread { onError(response.text) }
        }
    }

    fun loadSprints(projectId: Int, onSuccess: () -> Unit, onError: (message: String) -> Unit) {
        doAsync {
            val response = ScrumbleConnection.get("/project/$projectId/sprint")
            if(response.statusCode == 200) {
                val gson = GsonBuilder().registerTypeAdapter(Sprint::class.java, SprintDeserializer()).create()
                sprints = gson.fromJson(response.jsonArray.toString(), Array<Sprint>::class.java).toMutableList()

                uiThread { onSuccess() }
            }
            else
                uiThread { onError(response.text) }
        }
    }

    fun loadTasks(projectId: Int, onSuccess: () -> Unit, onError: (message: String) -> Unit) {
        doAsync {
            val response = ScrumbleConnection.get("/project/$projectId/task")
            if(response.statusCode == 200) {
                val gson = GsonBuilder().registerTypeAdapter(Task::class.java, TaskDeserializer()).create()
                tasks = gson.fromJson(response.jsonArray.toString(), Array<Task>::class.java).toMutableList()

                uiThread { onSuccess() }
            }
            else
                uiThread { onError(response.text) }
        }
    }

    fun addTask(task: Task, onSuccess: (id: Int) -> Unit, onError: (message: String) -> Unit) {
        doAsync {
            val gson = GsonBuilder().registerTypeAdapter(Task::class.java, TaskSerializer())
                    .serializeNulls().create()
            val response = ScrumbleConnection.post("/task", JSONObject(gson.toJson(task)))

            if(response.statusCode == 201)
                uiThread { onSuccess(response.text.toInt()) }
            else
                uiThread { onError(response.text) }
        }
    }

    fun updateTask(taskId: Int, task: Task, onSuccess: () -> Unit, onError: (message: String) -> Unit) {
        doAsync {
            val gson = GsonBuilder().registerTypeAdapter(Task::class.java, TaskSerializer())
                    .serializeNulls().create()
            val response = ScrumbleConnection.put("/task/$taskId", JSONObject(gson.toJson(task)))

            if(response.statusCode == 200)
                uiThread { onSuccess() }
            else
                uiThread { onError(response.text) }
        }
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

        val scrumble = Project(0, "Scrumble", sam, null)
        val footballsimulator = Project(1, "Football Simulator", savan, null)
        projects.add(scrumble)
        projects.add(footballsimulator)


        val start = Calendar.getInstance()
        val end = Calendar.getInstance()

        end.add(Calendar.DAY_OF_MONTH, 14)
        val scrumble1 = Sprint(0, 1, start.time, end.time, scrumble)

        start.time = end.time
        end.add(Calendar.DAY_OF_MONTH, 14)
        val scrumble2 = Sprint(1, 2, start.time, end.time, scrumble)

        start.time = end.time
        end.add(Calendar.DAY_OF_MONTH, 14)
        val scrumble3 = Sprint(2, 3, start.time,end.time, scrumble)

        start.time = end.time
        end.add(Calendar.DAY_OF_MONTH, 14)
        val footballsimulator1 = Sprint(3, 1, start.time, end.time, footballsimulator)

        start.time = end.time
        end.add(Calendar.DAY_OF_MONTH, 14)
        val footballsimulator2 = Sprint(4, 2, start.time, end.time, footballsimulator)
        sprints.add(scrumble1)
        sprints.add(scrumble2)
        sprints.add(scrumble3)
        sprints.add(footballsimulator1)
        sprints.add(footballsimulator2)

        val scrumbleGUI = Task(0, pauli, sam, "GUI Programmieren", "Hierbei handelt es sich um die Android GUI",
                0, TaskState.IN_PROGRESS, 1, scrumble1, scrumble)
        val scrumbleDB = Task(1, webi, simon, "Datenbank aufsetzten", "Inklusive Trigger, Sequenzen usw...",
                0, TaskState.TO_VERIFY, 1, scrumble1, scrumble)
        val scrumbleWebservice = Task(2, webi, pauli, "Webservice implementieren", "Der Webservice connected Client mit Datenbank",
                0, TaskState.SPRINT_BACKLOG, 1, scrumble1, scrumble)
        val scrumbleTesting = Task(3, simon, sam, "Testen", "Sollen wir Unit-Tests machen? Keine Ahnung",
                0, TaskState.SPRINT_BACKLOG, 2, scrumble1, scrumble)
        val scrumblePlaning = Task(4, sam, pauli, "Planung", "Geplant wird immer, weil wir mit Scrum arbeiten, lol. Hier steht auch noch mehr Text",
                0, TaskState.IN_PROGRESS, 2, scrumble1, scrumble)
        val scrumbleFinish = Task(4, sam, pauli, "Abschließen", "Abschluss des Projektes",
                0, TaskState.PRODUCT_BACKLOG, 0, null, scrumble)
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
    }
}