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

object ScrumbleController {
    var users = mutableListOf<User>()
    var projects = mutableListOf<Project>()
    var sprints = mutableListOf<Sprint>()
    var tasks = mutableListOf<Task>()
    var dailyScrumEntries = mutableListOf<DailyScrum>()

    var currentProject: Project? = null
    lateinit var currentUser: User

    fun login(user: User, onSuccess: (id: Int) -> Unit, onError: (message: String) -> Unit) {
        doAsync {
            val gson = GsonBuilder().registerTypeAdapter(User::class.java, UserSerializer())
                    .serializeNulls().create()

            val response = ScrumbleConnection.post("/login", JSONObject(gson.toJson(user)))
            if(response.statusCode in 200..299)
                { uiThread { onSuccess(response.jsonObject.getInt("id")) } }
            else
                uiThread { onError(response.jsonObject.getString("details")) }
        }
    }
    fun register(user: User, onSuccess: (id: Int) -> Unit, onError: (message: String) -> Unit) {
        doAsync {
            val gson = GsonBuilder().registerTypeAdapter(User::class.java, UserSerializer())
                    .serializeNulls().create()
            val response = ScrumbleConnection.post("/register", JSONObject(gson.toJson(user)))

            if(response.statusCode in 200..299)
                uiThread { onSuccess(response.jsonObject.getInt("id")) }
            else
                uiThread { onError(response.jsonObject.getString("details")) }
        }
    }

    fun loadTeam(projectId: Int, onSuccess: () -> Unit, onError: (message: String) -> Unit) {
        doAsync {
            val response = ScrumbleConnection.get("/project/$projectId/user")
            if(response.statusCode in 200..299)  {
                users.clear()

                if(response.statusCode != 204) {
                    val gson = GsonBuilder().registerTypeAdapter(User::class.java, UserDeserializer())
                            .serializeNulls().create()

                    users = gson.fromJson(response.jsonArray.toString(), Array<User>::class.java)
                            .sortedBy { it.name }.toMutableList()
                }
                uiThread { onSuccess() }
            } else
                uiThread { onError(response.text) }
        }
    }

    fun loadProjects(userId: Int, onSuccess: () -> Unit, onError: (message: String) -> Unit) {
        doAsync {
            val response = ScrumbleConnection.get("/user/$userId/project")
            if(response.statusCode in 200..299) {
                projects.clear()

                if(response.statusCode != 204) {
                    val gson = GsonBuilder().registerTypeAdapter(Project::class.java, ProjectDeserializer())
                            .serializeNulls().create()
                    gson.fromJson(response.jsonArray.toString(), Array<Project>::class.java)
                            .sortedBy { it.name }.forEach {
                                if (!ScrumbleController.isCurrentProjectSpecified()
                                        || ScrumbleController.isCurrentProjectSpecified() && ScrumbleController.currentProject!!.id != it.id)
                                    projects.add(it)
                            }
                }

                uiThread { onSuccess() }
            } else
                uiThread { onError(response.text) }
        }
    }
    fun loadCurrentProject(projectId: Int, onSuccess: () -> Unit, onError: (message: String) -> Unit) {
        doAsync {
            val response = ScrumbleConnection.get("/project/$projectId")
            if (response.statusCode in 200..299) {
                val gson = GsonBuilder().registerTypeAdapter(Project::class.java, ProjectDeserializer())
                        .serializeNulls().create()
                currentProject = gson.fromJson(response.jsonObject.toString(), Project::class.java)

                uiThread { onSuccess() }
            } else
                uiThread { onError(response.text) }
        }

    }

    fun loadSprints(projectId: Int, onSuccess: () -> Unit, onError: (message: String) -> Unit) {
        doAsync {
            val response = ScrumbleConnection.get("/project/$projectId/sprint")

            if(response.statusCode in 200..299)  {
                sprints.clear()

                if(response.statusCode != 204) {
                    val gson = GsonBuilder().registerTypeAdapter(Sprint::class.java, SprintDeserializer())
                            .serializeNulls().create()

                    sprints = gson.fromJson(response.jsonArray.toString(), Array<Sprint>::class.java)
                            .sortedBy { it.number }.toMutableList()
                }

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

            if(response.statusCode in 200..299)
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

            if(response.statusCode in 200..299)
                uiThread { onSuccess() }
            else
                uiThread { onError(response.text) }
        }
    }

    fun loadTasks(projectId: Int, onSuccess: () -> Unit, onError: (message: String) -> Unit) {
        doAsync {
            val response = ScrumbleConnection.get("/project/$projectId/task")

            if(response.statusCode in 200..299)  {
                tasks.clear()

                if(response.statusCode != 204) {
                    val gson = GsonBuilder().registerTypeAdapter(Task::class.java, TaskDeserializer())
                            .serializeNulls().create()
                    tasks = gson.fromJson(response.jsonArray.toString(), Array<Task>::class.java)
                            .sortedBy { it.id }.toMutableList()
                }

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

            if(response.statusCode in 200..299)
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

            if(response.statusCode in 200..299)
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

    fun isCurrentProjectSpecified(): Boolean {
        return currentProject != null
    }
    fun isCurrentSprintSpecified(): Boolean {
        return currentProject != null && currentProject!!.currentSprint != null
    }
}