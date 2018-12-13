package com.spogss.scrumble.data

class Project(var id: Int, var name: String, var productOwner: User, var currentSprint: Sprint? = null) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Project

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }

    override fun toString(): String {
        return name
    }
}