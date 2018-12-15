package com.spogss.scrumble.controller

import android.app.Activity
import android.content.Context
import com.spogss.scrumble.activity.MainActivity
import com.spogss.scrumble.data.User

object SharedPreferencesController {

    fun saveCredentialsToSharedPreferences(name: String, password: String, activity: Activity) {
        val sp = activity.getPreferences(Context.MODE_PRIVATE)
        sp.edit().putString("username", name).putString("password", password).apply()
    }
    fun deleteCredentialsFromSharedPreferences(activity: Activity) {
        val sp = activity.getPreferences(Context.MODE_PRIVATE)
        sp.edit().remove("username").remove("password").apply()
    }
    fun loadCredentialsFromSharedPreferences(activity: Activity): User? {
        val sp = activity.getPreferences(Context.MODE_PRIVATE)
        val username = sp.getString("username", null)
        val password = sp.getString("password", null)

        return if(username == null || password == null)
            null
        else
            User(-1, username, password)
    }

    fun saveCurrentProjectToSharedPreferences(projectId: Int, activity: Activity) {
        val sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE)
        sharedPreferences.edit().putInt(ScrumbleController.currentUser.id.toString(), projectId).apply()
    }
    fun deleteCurrentProjectFromSharedPreferences(user: User, activity: Activity) {
        val sp = activity.getPreferences(Context.MODE_PRIVATE)
        sp.edit().remove(user.id.toString()).apply()
    }
    fun loadCurrentProjectFromSharedPreferences(activity: Activity): Int {
        val sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE)
        return sharedPreferences.getInt(ScrumbleController.currentUser.id.toString(), -1)
    }
}