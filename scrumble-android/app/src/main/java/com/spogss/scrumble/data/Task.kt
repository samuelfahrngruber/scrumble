package com.spogss.scrumble.data

import com.spogss.scrumble.enums.TaskState

class Task(var id: Int, var responsible: User, var verify: User, var name: String, var info: String,
           var rejections: Int, var state: TaskState, var position: Int, var sprint: Sprint?, val project: Project) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Task

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }

    override fun toString(): String {
        return "[#$id] $name"
    }
}