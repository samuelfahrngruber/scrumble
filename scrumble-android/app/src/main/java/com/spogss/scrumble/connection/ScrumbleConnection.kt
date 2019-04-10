package com.spogss.scrumble.connection

import khttp.responses.Response
import org.json.JSONArray
import org.json.JSONObject

object ScrumbleConnection {
    private var baseUrl = "https://scrumble-api.herokuapp.com/scrumble" //http://10.0.0.9:8080/scrumble //https://scrumble-api.herokuapp.com/scrumble

    fun get(url: String): Response {
        return khttp.get(baseUrl + url)
    }

    fun post(url: String, body: JSONObject): Response {
        return khttp.post(baseUrl + url, json = body)
    }

    fun post(url: String, body: JSONArray): Response {
        return khttp.post(baseUrl + url, json = body)
    }

    fun put(url: String, body: JSONObject): Response {
        return khttp.put(baseUrl + url, json = body)
    }

    fun delete(url: String): Response {
        return khttp.delete(baseUrl + url)
    }
}