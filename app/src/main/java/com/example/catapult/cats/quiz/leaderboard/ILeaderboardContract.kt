package com.example.catapult.cats.quiz.leaderboard

import com.example.catapult.cats.network.dto.ResultDTO

interface ILeaderboardContract {
    data class LeaderboardState(
        val isLoading: Boolean = false,
        val error: DetailsError? = null,
        val results: List<ResultDTO> = emptyList(),
        val nick: String = ""
    ) {
        sealed class DetailsError {
            data class DataUpdateFailed(val cause: Throwable? = null): DetailsError()
        }
    }
}