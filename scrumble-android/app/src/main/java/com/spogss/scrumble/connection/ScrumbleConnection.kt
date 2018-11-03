package com.spogss.scrumble.connection

import khttp.responses.Response

object ScrumbleConnection {
    var baseUrl = "https://scrumble-api.herokuapp.com/scrumble"

    fun get(url: String): Response {
        return khttp.get(baseUrl + url)
    }
}