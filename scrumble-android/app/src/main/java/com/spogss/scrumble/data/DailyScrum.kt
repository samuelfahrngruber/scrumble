package com.spogss.scrumble.data

import java.util.*

class DailyScrum(val id: Int, val teamMember: User, val date: Date, val description: String, val task: Task? = null) {
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
}