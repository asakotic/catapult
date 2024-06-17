package com.example.catapult.users

import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Calendar

@Serializable
data class User (
    //TODO image
    val name: String,
    val nickname: String,
    val email: String,
    val guessCat: UserQuiz = UserQuiz(),
    val guessFact: UserQuiz = UserQuiz(),
    val leftRightCat: UserQuiz = UserQuiz(),
) {
    companion object {
        val EMPTY = User(name = "", email = "", nickname = "")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        return email == other.email
    }

    override fun hashCode(): Int {
        return email.hashCode()
    }


}

@Serializable
data class UserQuiz (
    val resultsHistory: List<Int> = emptyList(), //TODO Result
    val bestResult: Int = 0, //TODO float
    val bestLeaderboardPos: Int = Int.MAX_VALUE,
)

@Serializable
data class Result (
    val result: Float = 0f,
    val createdAt: Long
) {
    fun covertToDate(): String {
        return getDate(createdAt)
    }


    private fun getDate(milliSeconds: Long): String {

        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat("dd/MM/yyy hh:mm:ss")

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar: Calendar = Calendar.getInstance()
        calendar.setTimeInMillis(milliSeconds)
        return formatter.format(calendar.time)
    }}