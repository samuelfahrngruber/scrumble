package com.spogss.scrumble.json

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.spogss.scrumble.controller.ScrumbleController
import com.spogss.scrumble.data.DailyScrum
import com.spogss.scrumble.data.Sprint
import com.spogss.scrumble.data.Task
import com.spogss.scrumble.data.User
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

class DailyScrumDeserializer: JsonDeserializer<DailyScrum> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): DailyScrum {
        val gson = GsonBuilder().registerTypeAdapter(User::class.java, UserDeserializer())
                .registerTypeAdapter(Task::class.java, TaskDeserializer())
                .serializeNulls().create()

        val jsonObject = json.asJsonObject
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val id = jsonObject.get("_id").asString
        val date = formatter.parse(jsonObject.get("date").asString)
        val description = jsonObject.get("description").asString
        val project = ScrumbleController.projects.find { it.id == jsonObject.get("project").asInt }?: ScrumbleController.currentProject!!

        val user = gson.fromJson<User>(jsonObject.get("user"), User::class.java)

        val task = if(jsonObject.get("task").isJsonNull) null
            else gson.fromJson<Task>(jsonObject.get("task"), Task::class.java)

        val sprint = if(jsonObject.get("sprint").isJsonNull) null
            else ScrumbleController.sprints.find { it.id == jsonObject.get("sprint").asInt }

        return DailyScrum(id, user, date, description, project, sprint, task)
    }
}