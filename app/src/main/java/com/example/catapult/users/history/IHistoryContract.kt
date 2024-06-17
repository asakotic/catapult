package com.example.catapult.users.history

import com.example.catapult.users.UsersData

interface IHistoryContract {

    data class HistoryState(
        val usersData: UsersData,
        val expandedList: List<Boolean> = listOf(false, false, false)
    )

    sealed class HistoryUIEvent {
        data class Expanded(val expandedList: List<Boolean>) : HistoryUIEvent()
    }
}