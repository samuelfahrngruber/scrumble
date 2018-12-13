package com.spogss.scrumble.json

import com.google.gson.*
import com.spogss.scrumble.data.Project
import java.lang.reflect.Type

class ProjectSerializer: JsonSerializer<Project> {
    override fun serialize(project: Project, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val jsonObject = JsonObject()

        jsonObject.addProperty("id", project.id)
        jsonObject.addProperty("name", project.name)
        jsonObject.addProperty("productowner", project.productOwner.id)

        if(project.currentSprint == null)
            jsonObject.add("currentsprint", JsonNull.INSTANCE)
        else
            jsonObject.addProperty("currentsprint", project.currentSprint!!.id)

        return jsonObject
    }
}