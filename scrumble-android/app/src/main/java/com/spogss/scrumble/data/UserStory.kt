package com.spogss.scrumble.data

import com.spogss.scrumble.enums.UserStoryState

class UserStory(val id: Int, val responsible: User, val verify: User, val name: String, val info: String,
                val rejections: Int, val state: UserStoryState, val position: Int, val sprint: Int?) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserStory

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }
}