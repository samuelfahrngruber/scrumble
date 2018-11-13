package com.spogss.scrumble.controller

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.spogss.scrumble.connection.ScrumbleConnection
import com.spogss.scrumble.data.*
import com.spogss.scrumble.enums.TaskState
import com.spogss.scrumble.json.*
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

    fun login(user: User, onSuccess: (id: Int) -> Unit, onError: (message: String) -> Unit) {
        doAsync {
            val response = ScrumbleConnection.post("/login", JSONObject(Gson().toJson(user)))
            if(response.statusCode == 201)
                uiThread { onSuccess(response.jsonObject.getInt("id")) }
            else
                uiThread { onError(response.text) }
        }
    }

    fun register(user: User, onSuccess: (id: Int) -> Unit, onError: (message: String) -> Unit) {
        doAsync {
            val response = ScrumbleConnection.post("/register", JSONObject(Gson().toJson(user)))
            if(response.statusCode == 201)
                uiThread { onSuccess(response.jsonObject.getInt("id")) }
            else
                uiThread { onError(response.text) }
        }
    }

    fun loadTeam(projectId: Int, onSuccess: () -> Unit, onError: (message: String) -> Unit) {
        doAsync {
            val response = ScrumbleConnection.get("/project/$projectId/user")
            if (response.statusCode == 200) {
                users = Gson().fromJson(response.jsonArray.toString(), Array<User>::class.java)
                        .sortedBy { it.name }.toMutableList()
                uiThread { onSuccess() }
            } else
                uiThread { onError(response.text) }
        }
    }

    fun loadProjects(userId: Int, onSuccess: () -> Unit, onError: (message: String) -> Unit) {
        doAsync {
            val response = ScrumbleConnection.get("/user/$userId/project")
            if (response.statusCode == 200) {
                val gson = GsonBuilder().registerTypeAdapter(Project::class.java, ProjectDeserializer()).create()
                projects = gson.fromJson(response.jsonArray.toString(), Array<Project>::class.java)
                        .sortedBy { it.name }.toMutableList()

                uiThread { onSuccess() }
            } else
                uiThread { onError(response.text) }
        }
    }

    fun loadSprints(projectId: Int, onSuccess: () -> Unit, onError: (message: String) -> Unit) {
        doAsync {
            val response = ScrumbleConnection.get("/project/$projectId/sprint")
            if (response.statusCode == 200) {
                val gson = GsonBuilder().registerTypeAdapter(Sprint::class.java, SprintDeserializer()).create()
                sprints = gson.fromJson(response.jsonArray.toString(), Array<Sprint>::class.java)
                        .sortedBy { it.number }.toMutableList()

                uiThread { onSuccess() }
            } else
                uiThread { onError(response.text) }
        }
    }
    fun addSprint(sprint: Sprint, onSuccess: (id: Int) -> Unit, onError: (message: String) -> Unit) {
        doAsync {
            val gson = GsonBuilder().registerTypeAdapter(Sprint::class.java, SprintSerializer())
                    .serializeNulls().create()
            val response = ScrumbleConnection.post("/sprint", JSONObject(gson.toJson(sprint)))

            if(response.statusCode == 201)
                uiThread { onSuccess(response.jsonObject.getInt("id")) }
            else
                uiThread { onError(response.text) }
        }
    }
    fun updateSprint(sprintId: Int, sprint: Sprint, onSuccess: () -> Unit, onError: (message: String) -> Unit) {
        doAsync {
            val gson = GsonBuilder().registerTypeAdapter(Sprint::class.java, SprintSerializer())
                    .serializeNulls().create()
            val response = ScrumbleConnection.put("/sprint/$sprintId", JSONObject(gson.toJson(sprint)))

            if (response.statusCode == 200)
                uiThread { onSuccess() }
            else
                uiThread { onError(response.text) }
        }
    }

    fun loadTasks(projectId: Int, onSuccess: () -> Unit, onError: (message: String) -> Unit) {
        doAsync {
            val response = ScrumbleConnection.get("/project/$projectId/task")
            if (response.statusCode == 200) {
                val gson = GsonBuilder().registerTypeAdapter(Task::class.java, TaskDeserializer()).create()
                tasks = gson.fromJson(response.jsonArray.toString(), Array<Task>::class.java)
                        .sortedBy { it.id }.toMutableList()

                uiThread { onSuccess() }
            } else
                uiThread { onError(response.text) }
        }
    }
    fun addTask(task: Task, onSuccess: (id: Int) -> Unit, onError: (message: String) -> Unit) {
        doAsync {
            val gson = GsonBuilder().registerTypeAdapter(Task::class.java, TaskSerializer())
                    .serializeNulls().create()
            val response = ScrumbleConnection.post("/task", JSONObject(gson.toJson(task)))

            if (response.statusCode == 201)
                uiThread { onSuccess(response.jsonObject.getInt("id")) }
            else
                uiThread { onError(response.text) }
        }
    }
    fun updateTask(taskId: Int, task: Task, onSuccess: () -> Unit, onError: (message: String) -> Unit) {
        doAsync {
            val gson = GsonBuilder().registerTypeAdapter(Task::class.java, TaskSerializer())
                    .serializeNulls().create()
            val response = ScrumbleConnection.put("/task/$taskId", JSONObject(gson.toJson(task)))

            if (response.statusCode == 200)
                uiThread { onSuccess() }
            else
                uiThread { onError(response.text) }
        }
    }

    fun updatePositions(oldPosition: Int, newPosition: Int, newState: TaskState, oldState: TaskState, sprint: Sprint) {
        tasks.filter { it.sprint != null && it.sprint!!.id == sprint.id
                && (it.state == newState || it.state == oldState) }.forEach {

            if(oldState == newState && oldPosition > newPosition && oldPosition > it.position && newPosition <= it.position && newState == it.state)
                it.position++
            else if(oldState == newState && oldPosition < newPosition && oldPosition < it.position && newPosition >= it.position && newState == it.state)
                it.position--
            else if(oldState != newState && newPosition <= it.position && newState == it.state)
                it.position++
            else if(oldState != newState && oldPosition < it.position && oldState == it.state)
                it.position--
        }
    }
}