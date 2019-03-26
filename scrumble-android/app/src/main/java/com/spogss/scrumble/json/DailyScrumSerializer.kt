package com.spogss.scrumble.json

import com.google.gson.*
import com.spogss.scrumble.data.DailyScrum
import com.spogss.scrumble.data.Task
import com.spogss.scrumble.data.User
import org.json.JSONObject
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

class DailyScrumSerializer: JsonSerializer<DailyScrum> {
    override fun serialize(dailyScrum: DailyScrum, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val gson = GsonBuilder().registerTypeAdapter(User::class.java, UserSerializer())
                .registerTypeAdapter(Task::class.java, TaskSerializer())
                .serializeNulls().create()

        val jsonObject = JsonObject()
        val formatter = SimpleDateFormat("YYYY-MM-dd", Locale.getDefault())

        jsonObject.addProperty("_id", dailyScrum.id)
        jsonObject.addProperty("date", formatter.format(dailyScrum.date))
        jsonObject.addProperty("description", dailyScrum.description)
        jsonObject.addProperty("project", dailyScrum.project.id)

        val userJsonObject = JSONObject(gson.toJson(dailyScrum.user))
        userJsonObject.remove("password")
        jsonObject.add("user", gson.fromJson(userJsonObject.toString(), JsonElement::class.java))

        if(dailyScrum.task == null)
            jsonObject.add("task", JsonNull.INSTANCE)
        else {
            jsonObject.add("task", gson.toJsonTree(dailyScrum.task))
        }

        if(dailyScrum.sprint == null)
            jsonObject.add("sprint", JsonNull.INSTANCE)
        else
            jsonObject.addProperty("sprint", dailyScrum.sprint!!.id)

        return jsonObject
    }
}