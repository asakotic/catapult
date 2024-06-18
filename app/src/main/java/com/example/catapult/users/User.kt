package com.example.catapult.users

import android.net.Uri
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Calendar

@Serializable
data class User(
    val name: String,
    val email: String,
    val image: String = "https://cdn2.thecatapi.com/images/J2PmlIizw.jpg",
    val nickname: String,
    val guessCat: UserQuiz = UserQuiz.EMPTY,
    val guessFact: UserQuiz = UserQuiz.EMPTY,
    val leftRightCat: UserQuiz = UserQuiz.EMPTY,
) {
    companion object {
        val EMPTY = User(
            name = "",
            email = "",
            nickname = "",
            image = "",
            guessCat = UserQuiz.EMPTY,
            guessFact = UserQuiz.EMPTY,
            leftRightCat = UserQuiz.EMPTY
        )
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
data class UserQuiz(
    val resultsHistory: List<Result> = emptyList(),
    val bestResult: Float = 0f,
    val bestLeaderboardPos: Int = Int.MAX_VALUE,
) {
    companion object {
        val EMPTY = UserQuiz(
            resultsHistory = emptyList(),
            bestResult = 0f,
            bestLeaderboardPos = Int.MAX_VALUE
        )
    }
}

@Serializable
data class Result(
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
    }
}