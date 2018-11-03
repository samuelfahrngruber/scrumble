package com.spogss.scrumble.json

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.spogss.scrumble.controller.ScrumbleController
import com.spogss.scrumble.data.Sprint
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

class SprintDeserializer: JsonDeserializer<Sprint> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Sprint {
        val jsonObject = json.asJsonObject
        val formatter = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ssZ", Locale.ENGLISH)

        val id = jsonObject.get("id").asInt
        val number = jsonObject.get("number").asInt
        val startDate = formatter.parse(jsonObject.get("startdate").asString)
        val deadline = formatter.parse(jsonObject.get("deadline").asString)
        val project = ScrumbleController.projects.find { it.id == jsonObject.get("project").asInt }!!

        val sprint = Sprint(id, number, startDate, deadline, project)
        ScrumbleController.projects.forEach {
            if(it.currentSprint != null && it.currentSprint!!.id == sprint.id)
                it.currentSprint = sprint
        }

        return sprint
    }
}