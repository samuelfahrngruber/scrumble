package com.spogss.scrumble.data

import java.text.SimpleDateFormat
import java.util.*

class Sprint(var id: Int, var number: Int, var startDate: Date, var deadline: Date, var project: Project) {
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

    override fun toString(): String {
        return "#$number"
    }

    fun timeSpan(): String {
        val dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale("EN"))
        return "${dateFormatter.format(startDate)} - ${dateFormatter.format(deadline)}"
    }
}