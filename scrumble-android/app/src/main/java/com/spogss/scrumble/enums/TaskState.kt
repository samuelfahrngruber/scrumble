package com.spogss.scrumble.enums

enum class TaskState {
    PRODUCT_BACKLOG,
    SPRINT_BACKLOG,
    IN_PROGRESS,
    TO_VERIFY,
    DONE;

    fun getShortForm(): String {
        return when(this) {
            PRODUCT_BACKLOG -> "PRBL"
            SPRINT_BACKLOG -> "SPBL"
            IN_PROGRESS -> "PROG"
            TO_VERIFY -> "TEST"
            DONE -> "DONE"
        }
    }
}