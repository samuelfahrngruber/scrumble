package com.spogss.scrumble.controller

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.spogss.scrumble.connection.ScrumbleConnection
import com.spogss.scrumble.data.*
import com.spogss.scrumble.enums.TaskState
import com.spogss.scrumble.json.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

object ScrumbleController {
    var users = mutableListOf<User>()
    var projects = mutableListOf<Project>()
    var sprints = mutableListOf<Sprint>()
    var tasks = mutableListOf<Task>()
    var dailyScrumEntries = mutableListOf<DailyScrum>()

    var currentProject: Project? = null
    lateinit var currentUser: User

    private var lastUpdate = Date()

    fun login(user: User, onSuccess: (id: Int) -> Unit, onError: (message: String) -> Unit) {
        doAsync {
            val gson = GsonBuilder().registerTypeAdapter(User::class.java, UserSerializer())
                    .serializeNulls().create()

            val response = ScrumbleConnection.post("/login", JSONObject(gson.toJson(user)))
            if (response.statusCode in 200..299) {
                uiThread { onSuccess(response.jsonObject.getInt("id")) }
            } else
                uiThread { onError(response.jsonObject.getString("details")) }
        }
    }

    fun register(user: User, onSuccess: (id: Int) -> Unit, onError: (message: String) -> Unit) {
        doAsync {
            val gson = GsonBuilder().registerTypeAdapter(User::class.java, UserSerializer())
                    .serializeNulls().create()
            val response = ScrumbleConnection.post("/register", JSONObject(gson.toJson(user)))

            if (response.statusCode in 200..299)
                uiThread { onSuccess(response.jsonObject.getInt("id")) }
            else
                uiThread { onError(response.jsonObject.getString("details")) }
        }
    }

    fun loadTeam(projectId: Int, onSuccess: () -> Unit, onError: (message: String) -> Unit) {
        doAsync {
            val response = ScrumbleConnection.get("/project/$projectId/user")
            if (response.statusCode in 200..299) {
                users.clear()

                if (response.statusCode != 204) {
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

    fun getUserById(userId: Int, onSuccess: (user: User) -> Unit, onError: (message: String) -> Unit) {
        doAsync {
            val response = ScrumbleConnection.get("/user/$userId")

            if (response.statusCode in 200..299) {
                val gson = GsonBuilder().registerTypeAdapter(User::class.java, UserDeserializer())
                        .serializeNulls().create()

                val user = gson.fromJson(response.jsonObject.toString(), User::class.java)
                uiThread { onSuccess(user) }
            } else
                uiThread { onError(response.text) }
        }
    }

    fun getTeamMemberByName(name: String, onSuccess: (user: User) -> Unit, onError: (message: String) -> Unit) {
        doAsync {
            val response = ScrumbleConnection.get("/user?name=$name")

            if (response.statusCode in 200..299) {
                val gson = GsonBuilder().registerTypeAdapter(User::class.java, UserDeserializer())
                        .serializeNulls().create()

                val user = gson.fromJson(response.jsonArray[0].toString(), User::class.java)
                uiThread { onSuccess(user) }
            } else
                uiThread { onError(response.text) }
        }
    }

    fun addTeamMembers(projectId: Int, team: MutableList<User>, onSuccess: () -> Unit, onError: (message: String) -> Unit) {
        doAsync {
            val gson = GsonBuilder().registerTypeAdapter(Array<User>::class.java, UserSerializer())
                    .serializeNulls().create()
            val response = ScrumbleConnection.post("/project/$projectId/user", JSONArray(gson.toJson(team)))

            if (response.statusCode in 200..299)
                uiThread { onSuccess() }
            else
                uiThread { onError(response.text) }
        }
    }

    fun removeTeamMember(projectId: Int, userId: Int, onSuccess: () -> Unit, onError: (message: String) -> Unit) {
        doAsync {
            val response = ScrumbleConnection.delete("/project/$projectId/user/$userId")

            if (response.statusCode in 200..299)
                uiThread { onSuccess() }
            else
                uiThread { onError(response.text) }
        }
    }

    fun loadProjects(userId: Int, onSuccess: () -> Unit, onError: (message: String) -> Unit) {
        doAsync {
            val response = ScrumbleConnection.get("/user/$userId/project")
            if (response.statusCode in 200..299) {
                projects.clear()
                if(isCurrentProjectSpecified())
                    projects.add(currentProject!!)

                if (response.statusCode != 204) {
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

    fun addProject(project: Project, onSuccess: (id: Int) -> Unit, onError: (message: String) -> Unit) {
        doAsync {
            val gson = GsonBuilder().registerTypeAdapter(Project::class.java, ProjectSerializer())
                    .serializeNulls().create()
            val response = ScrumbleConnection.post("/project", JSONObject(gson.toJson(project)))

            if (response.statusCode in 200..299)
                uiThread { onSuccess(response.jsonObject.getInt("id")) }
            else
                uiThread { onError(response.text) }
        }
    }

    fun updateProject(projectId: Int, project: Project, onSuccess: () -> Unit, onError: (message: String) -> Unit) {
        doAsync {
            val gson = GsonBuilder().registerTypeAdapter(Project::class.java, ProjectSerializer())
                    .serializeNulls().create()
            val response = ScrumbleConnection.put("/project/$projectId", JSONObject(gson.toJson(project)))

            if (response.statusCode in 200..299)
                uiThread { onSuccess() }
            else
                uiThread { onError(response.text) }
        }
    }

    fun loadSprints(projectId: Int, onSuccess: () -> Unit, onError: (message: String) -> Unit) {
        doAsync {
            val response = ScrumbleConnection.get("/project/$projectId/sprint")

            if (response.statusCode in 200..299) {
                sprints.clear()

                if (response.statusCode != 204) {
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

            if (response.statusCode in 200..299)
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

            if (response.statusCode in 200..299)
                uiThread { onSuccess() }
            else
                uiThread { onError(response.text) }
        }
    }

    fun loadTasks(projectId: Int, onSuccess: () -> Unit, onError: (message: String) -> Unit) {
        doAsync {
            val response = ScrumbleConnection.get("/project/$projectId/task")

            if (response.statusCode in 200..299) {
                tasks.clear()

                if (response.statusCode != 204) {
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

            if (response.statusCode in 200..299)
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

            if (response.statusCode in 200..299)
                uiThread { onSuccess() }
            else
                uiThread { onError(response.text) }
        }
    }

    fun removeTask(taskId: Int, onSuccess: () -> Unit, onError: (message: String) -> Unit) {
        doAsync {
            val response = ScrumbleConnection.delete("/task/$taskId")

            if (response.statusCode in 200..299)
                uiThread { onSuccess() }
            else
                uiThread { onError(response.text) }
        }
    }

    fun loadDailyScrum(projectId: Int, onSuccess: () -> Unit, onError: (message: String) -> Unit) {
        doAsync {
            var url = "/project/$projectId/dailyscrum"

            if(isCurrentSprintSpecified())
                url += "?sprint=${currentProject!!.currentSprint!!.id}"

            val response = ScrumbleConnection.get(url)
            if (response.statusCode in 200..299) {
                dailyScrumEntries.clear()

                if (response.statusCode != 204) {
                    val gson = GsonBuilder().registerTypeAdapter(DailyScrum::class.java, DailyScrumDeserializer())
                            .serializeNulls().create()
                    dailyScrumEntries = gson.fromJson(response.jsonArray.toString(), Array<DailyScrum>::class.java).toMutableList()
                    dailyScrumEntries.sortWith(compareByDescending<DailyScrum> { it.date }.thenBy { it.user.name })
                }

                uiThread { onSuccess() }
            } else
                uiThread { onError(response.text) }
        }
    }

    fun updateDailyScrum(dailyScrumId: String, dailyScrum: DailyScrum, onSuccess: () -> Unit, onError: (message: String) -> Unit) {
        doAsync {
            val gson = GsonBuilder().registerTypeAdapter(DailyScrum::class.java, DailyScrumSerializer())
                    .serializeNulls().create()
            val response = ScrumbleConnection.put("/dailyscrum/$dailyScrumId", JSONObject(gson.toJson(dailyScrum)))

            if (response.statusCode in 200..299)
                uiThread { onSuccess() }
            else
                uiThread { onError(response.text) }
        }
    }

    fun loadChanges(projectId: Int, context: Context, onSuccess: (json: JSONArray, context: Context) -> Unit, onError: (message: String) -> Unit) {
        doAsync {
            val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.000'Z'", Locale.getDefault())
            formatter.timeZone = TimeZone.getTimeZone("UTC")

            val response = ScrumbleConnection.get("/project/$projectId/changes?timestamp=${formatter.format(lastUpdate)}")

            if(response.statusCode in 200..299) {

                if(response.statusCode != 204) {
                    uiThread {
                        lastUpdate = Date()
                        onSuccess(response.jsonArray, context)
                    }
                }
            }
            else if(response.statusCode != 204)
                uiThread { onError(response.text) }
        }
    }

    fun updatePositions(oldPosition: Int, newPosition: Int, oldState: TaskState, newState: TaskState, sprint: Sprint) {
        tasks.filter {
            it.sprint == sprint && (it.state == newState || it.state == oldState)
        }.forEach {
            if (oldState == newState && oldPosition > newPosition && oldPosition > it.position && newPosition <= it.position && newState == it.state)
                it.position++
            else if (oldState == newState && oldPosition < newPosition && oldPosition < it.position && newPosition >= it.position && newState == it.state)
                it.position--
            else if (oldState != newState && newPosition <= it.position && newState == it.state) {
                it.position++
            }
            else if (oldState != newState && oldPosition < it.position && oldState == it.state)
                it.position--
        }
    }

    fun updatePositions(oldPosition: Int, state: TaskState, sprint: Sprint) {
        tasks.filter { it.sprint == sprint && it.state == state && it.position > oldPosition
        }.forEach {
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