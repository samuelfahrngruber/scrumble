package com.spogss.scrumble.data

import com.spogss.scrumble.enums.TaskState

class Task(val id: Int, val responsible: User, val verify: User, val name: String, val info: String,
           val rejections: Int, val state: TaskState, val position: Int, val sprint: Int?, val project: Int) {

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
}