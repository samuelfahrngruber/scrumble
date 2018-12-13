package com.spogss.scrumble.json

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.spogss.scrumble.controller.ScrumbleController
import com.spogss.scrumble.data.Task
import com.spogss.scrumble.enums.TaskState
import java.lang.reflect.Type

class TaskDeserializer: JsonDeserializer<Task> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Task {
        val jsonObject = json.asJsonObject

        val id = jsonObject.get("id").asInt

        val responsible = if(jsonObject.get("responsible").isJsonNull) null
            else ScrumbleController.users.find { it.id == jsonObject.get("responsible").asInt }!!

        val verify = if(jsonObject.get("verify").isJsonNull) null
            else ScrumbleController.users.find { it.id == jsonObject.get("verify").asInt }!!

        val name = jsonObject.get("name").asString
        val info = jsonObject.get("info").asString
        val rejections = jsonObject.get("rejections").asInt
        val state = TaskState.valueOf(jsonObject.get("state").asString)
        val position = jsonObject.get("position").asInt
        val color = jsonObject.get("color").asString
        val sprint = if(jsonObject.get("sprint").isJsonNull) null else ScrumbleController.sprints.find { it.id == jsonObject.get("sprint").asInt }
        val project = ScrumbleController.projects.find { it.id == jsonObject.get("project").asInt }?: ScrumbleController.currentProject!!

        return Task(id, responsible, verify, name, info, rejections, state, position, color, sprint, project)
    }
}