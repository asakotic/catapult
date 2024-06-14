package com.example.catapult.users

import kotlinx.serialization.Serializable

@Serializable
data class User (
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
    val resultsHistory: List<Int> = emptyList(),
    val bestResult: Int = 0,
    val bestLeaderboardPos: Int = Int.MAX_VALUE,
)