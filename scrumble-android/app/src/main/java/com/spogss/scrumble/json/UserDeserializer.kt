package com.spogss.scrumble.json

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.spogss.scrumble.controller.ScrumbleController
import com.spogss.scrumble.data.User
import java.lang.reflect.Type

class UserDeserializer: JsonDeserializer<User> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): User {
        val jsonObject = json.asJsonObject

        val id = jsonObject.get("id").asInt
        val name = jsonObject.get("name").asString

        val user = User(id, name, "")

        if(ScrumbleController.currentProject!!.productOwner.id == user.id && ScrumbleController.currentProject!!.productOwner.name.isBlank())
            ScrumbleController.currentProject!!.productOwner = user

        if(ScrumbleController.currentUser.id == user.id)
            ScrumbleController.currentUser = user

        return user
    }
}