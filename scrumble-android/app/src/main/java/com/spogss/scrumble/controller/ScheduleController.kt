package com.spogss.scrumble.controller

import android.content.Context
import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.spogss.scrumble.activity.MainActivity
import com.spogss.scrumble.data.*
import com.spogss.scrumble.enums.TaskState
import com.spogss.scrumble.fragment.DailyScrumFragment
import com.spogss.scrumble.fragment.MyTasksFragment
import com.spogss.scrumble.fragment.ProjectsFragment
import com.spogss.scrumble.fragment.ScrumBoardFragment
import com.spogss.scrumble.json.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.concurrent.schedule

object ScheduleController {
    fun run(context: Context) {
        Timer().schedule(0, 2000) {
            if(ScrumbleController.isCurrentProjectSpecified()) {
                ScrumbleController.loadChanges(ScrumbleController.currentProject!!.id,
                        context, ::onSuccess) { MiscUIController.showError(context, it) }
            }
        }
    }

    private fun onSuccess(jsonArray: JSONArray, context: Context) {
        val gson = GsonBuilder()
                .registerTypeAdapter(Task::class.java, TaskDeserializer())
                .registerTypeAdapter(Sprint::class.java, SprintDeserializer())
                .registerTypeAdapter(Project::class.java, ProjectDeserializer())
                .registerTypeAdapter(DailyScrum::class.java, DailyScrumDeserializer())
                .registerTypeAdapter(User::class.java, UserDeserializer())
                .create()

        var updateUI = false

        for(idx in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(idx)
            val obj = jsonObject.getString("object")

            var id = when {
                jsonObject.get("change") is JSONObject && jsonObject.getJSONObject("change").has("id")
                    -> jsonObject.getJSONObject("change").get("id")
                jsonObject.get("change") is JSONObject && jsonObject.getJSONObject("change").has("_id")
                    -> jsonObject.getJSONObject("change").get("_id")
                else -> "-1"
            }

            val method = jsonObject.get("method")

            when(obj) {
                "TASK" -> {
                    id = id as Int
                    if(method == "DELETE") {
                        val task = ScrumbleController.tasks.find { it.id == id }
                        ScrumbleController.tasks.remove(task)
                        if(task != null) {
                            if (task.sprint != null)
                                ScrumbleController.updatePositions(task.position, task.state, task.sprint!!)
                            updateUI = true
                        }
                    }
                    else if(method == "POST") {
                        val task = gson.fromJson(jsonObject.get("change").toString(), Task::class.java)
                        if(!ScrumbleController.tasks.contains(task)) {
                            ScrumbleController.tasks.add(task)
                            updateUI = true
                        }
                    }
                    else if(method == "PUT") {
                        val oldTask = ScrumbleController.tasks[ ScrumbleController.tasks.indexOfFirst { it.id == id as Int }]
                        val newTask = gson.fromJson(jsonObject.get("change").toString(), Task::class.java)

                        if(oldTask.name != newTask.name || oldTask.sprint != newTask.sprint ||
                                oldTask.position != newTask.position || oldTask.state != newTask.state
                                || oldTask.color != newTask.color || oldTask.responsible != newTask.responsible
                                || oldTask.verify != newTask.verify) {

                            if(oldTask.sprint != null && oldTask.sprint == newTask.sprint && (oldTask.position != newTask.position || oldTask.state != newTask.state))
                                ScrumbleController.updatePositions(oldTask.position, newTask.position, oldTask.state, newTask.state, oldTask.sprint!!)
                            else if(newTask.sprint == null && oldTask.sprint != null)
                                ScrumbleController.updatePositions(oldTask.position, oldTask.state, oldTask.sprint!!)

                            ScrumbleController.tasks[ScrumbleController.tasks.indexOfFirst { it.id == id as Int }] = newTask
                            updateUI = true
                        }
                    }
                }
                "SPRINT" -> {
                    id = id as Int
                    if(method == "POST") {
                        val sprint = gson.fromJson(jsonObject.get("change").toString(), Sprint::class.java)
                        if(!ScrumbleController.sprints.contains(sprint) && !ScrumbleController.sprints.any { it.number == sprint.number }) {
                            ScrumbleController.sprints.add(sprint)
                            updateUI = true
                        }
                    }
                    else if(method == "PUT") {
                        val oldSprint = ScrumbleController.sprints[ScrumbleController.sprints.indexOfFirst { it.id == id }]
                        val newSprint = gson.fromJson(jsonObject.get("change").toString(), Sprint::class.java)

                        if(oldSprint.number != newSprint.number || oldSprint.startDate != newSprint.startDate || oldSprint.deadline != newSprint.deadline) {
                            ScrumbleController.sprints[ScrumbleController.sprints.indexOfFirst { it.id == id }] =
                                    newSprint

                            ScrumbleController.dailyScrumEntries.forEach {
                                if (it.sprint?.id == newSprint.id)
                                    it.sprint = newSprint
                            }

                            updateUI = true
                        }
                    }
                }
                "PROJECT" -> {
                    id = id as Int
                    if(method == "PUT") {
                        val oldProject = ScrumbleController.projects[ScrumbleController.projects.indexOfFirst { it.id == id }]
                        val newProject = gson.fromJson(jsonObject.get("change").toString(), Project::class.java)

                        if(oldProject.productOwner != newProject.productOwner || oldProject.currentSprint != newProject.currentSprint) {
                            ScrumbleController.projects[ScrumbleController.projects.indexOfFirst { it.id == id }] = newProject

                            if (ScrumbleController.currentProject == newProject)
                                ScrumbleController.currentProject = newProject

                            updateUI = true
                        }
                    }
                }
                "DAILYSCRUM" -> {
                    if(method == "POST") {
                        val dailyScrums = gson.fromJson(jsonObject.getJSONArray("change").toString(), Array<DailyScrum>::class.java)

                        dailyScrums.forEach {
                            if(!ScrumbleController.dailyScrumEntries.contains(it)) {
                                ScrumbleController.dailyScrumEntries.add(it)
                                updateUI = true
                            }
                        }
                    }
                    else if(method == "PUT") {
                        id = id as String
                        val oldDailyScrum = ScrumbleController.dailyScrumEntries[ScrumbleController.dailyScrumEntries.indexOfFirst { it.id == id }]
                        val newDailyScrum = gson.fromJson(jsonObject.get("change").toString(), DailyScrum::class.java)

                        if(oldDailyScrum.task != newDailyScrum.task || oldDailyScrum.description != newDailyScrum.description) {
                            ScrumbleController.dailyScrumEntries[ScrumbleController.dailyScrumEntries.indexOfFirst { it.id == id }] = newDailyScrum
                            updateUI = true
                        }
                    }
                    ScrumbleController.dailyScrumEntries.sortWith(compareByDescending<DailyScrum> { it.date }.thenBy { it.user.name })
                }
                "USER" -> {
                    if(method == "POST") {
                        val users = gson.fromJson(jsonObject.getJSONArray("change").toString(), Array<User>::class.java)
                        users.forEach {
                            if(!ScrumbleController.users.contains(it)) {
                                ScrumbleController.users.add(it)
                                updateUI = true
                            }
                        }
                        users.sortBy { it.name }
                    }
                    else if(method == "DELETE") {
                        id = id as Int
                        val user = ScrumbleController.users.find { it.id == id }
                        ScrumbleController.users.remove(user)

                        if(user != null) updateUI = true
                    }
                }
            }
        }
        if(updateUI && ScrumbleController.isCurrentProjectSpecified())
            updateUI(context)
    }

    private fun updateUI(context: Context) {
        val selectedFragment = (context as MainActivity).selectedFragment
        when(selectedFragment) {
            is MyTasksFragment -> selectedFragment.refresh()
            is ScrumBoardFragment -> selectedFragment.refresh()
            is DailyScrumFragment -> selectedFragment.refresh()
            is ProjectsFragment -> selectedFragment.refresh()
        }
    }
}