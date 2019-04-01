package com.spogss.scrumble.json

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.spogss.scrumble.data.Sprint
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

class SprintSerializer: JsonSerializer<Sprint> {
    override fun serialize(sprint: Sprint, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val jsonObject = JsonObject()
        val formatter = SimpleDateFormat("YYYY-MM-dd", Locale.getDefault())

        jsonObject.addProperty("id", sprint.id)
        jsonObject.addProperty("number", sprint.number)
        jsonObject.addProperty("startdate", formatter.format(sprint.startDate))
        jsonObject.addProperty("deadline", formatter.format(sprint.deadline))
        jsonObject.addProperty("project", sprint.project.id)

        return jsonObject
    }
}