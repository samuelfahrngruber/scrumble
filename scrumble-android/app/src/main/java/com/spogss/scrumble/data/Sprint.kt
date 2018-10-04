package com.spogss.scrumble.data

import java.util.*

class Sprint(val id: Int, val number: Int, val startDate: Date, val deadline: Date, project: Int) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Sprint

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }
}