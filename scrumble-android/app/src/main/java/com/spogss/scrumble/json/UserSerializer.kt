package com.spogss.scrumble.json

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.spogss.scrumble.data.User
import java.lang.reflect.Type

class UserSerializer: JsonSerializer<User> {
    override fun serialize(user: User, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val jsonObject = JsonObject()

        jsonObject.addProperty("id", user.id)
        jsonObject.addProperty("username", user.name)
        jsonObject.addProperty("password", user.password)

        return jsonObject
    }
}