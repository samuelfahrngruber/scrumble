package com.spogss.scrumble.data

class Project(val id: Int, val name: String, val productOwner: User, val team: MutableList<User>, currentSprint: Sprint?) {
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
}