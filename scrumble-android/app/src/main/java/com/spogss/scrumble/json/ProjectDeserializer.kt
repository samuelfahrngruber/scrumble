package com.spogss.scrumble.json

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.spogss.scrumble.controller.ScrumbleController
import com.spogss.scrumble.data.Project
import com.spogss.scrumble.data.Sprint
import com.spogss.scrumble.data.User
import java.lang.reflect.Type
import java.util.*

class ProjectDeserializer: JsonDeserializer<Project> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Project {
        val jsonObject = json.asJsonObject

        val id = jsonObject.get("id").asInt
        val name = jsonObject.get("name").asString

        var productOwner =  ScrumbleController.users.find { it.id == jsonObject.get("productowner").asInt }
        if(productOwner == null)
            productOwner = User(jsonObject.get("productowner").asInt, "", "")
            ScrumbleController.getUserById(jsonObject.get("productowner").asInt, {
                productOwner = it
            }, {})

        var currentSprint: Sprint? = null

        val project = Project(id, name, productOwner!!, currentSprint)
        val sprintJson = jsonObject.get("currentsprint")
        if(!sprintJson.isJsonNull) {
            currentSprint = ScrumbleController.sprints.find { it.id == sprintJson.asInt } ?: Sprint(sprintJson.asInt, 0, Date(), Date(), project)
            project.currentSprint = currentSprint
        }

        return project
    }
}