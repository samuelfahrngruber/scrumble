package com.spogss.scrumble.json

import com.google.gson.*
import com.spogss.scrumble.data.Task
import java.lang.reflect.Type

class TaskSerializer: JsonSerializer<Task> {
    override fun serialize(task: Task, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val jsonObject = JsonObject()

        jsonObject.addProperty("id", task.id)
        jsonObject.addProperty("responsible", task.responsible.id)
        jsonObject.addProperty("verify", task.verify.id)
        jsonObject.addProperty("name", task.name)
        jsonObject.addProperty("info", task.info)
        jsonObject.addProperty("rejections", task.rejections)
        jsonObject.addProperty("state", task.state.toString())
        jsonObject.addProperty("position", task.position)
        jsonObject.addProperty("sprint", if(task.sprint == null) null else task.sprint!!.id)
        jsonObject.addProperty("project", task.project.id)

        return jsonObject
    }
}