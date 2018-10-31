package com.spogss.scrumble.data

import java.text.SimpleDateFormat
import java.util.*

class DailyScrum(val id: Int, val teamMember: User, val date: Date, var description: String, var task: Task? = null) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DailyScrum

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }

    fun weekday(): String {
        val weekdayFormatter = SimpleDateFormat("EEEE", Locale("EN"))
        return weekdayFormatter.format(date)
    }

    fun formattedDate(): String {
        val dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale("EN"))
        return dateFormatter.format(date)
    }
}