package com.example.catapult.cats.quiz.guess_fact

import com.example.catapult.cats.db.Cat
import com.example.catapult.cats.quiz.Timer

interface IGuessFactContract {
    data class GuessFactState(
        val isLoading: Boolean = false,
        val error: DetailsError? = null,
        val cats: List<Cat> = emptyList(),
        val answers: List<String> = emptyList(),
        val rightAnswer: String = "",
        val image: String = "",
        val points: Int = 0,
        val questionIndex: Int = 0,
        val question: Int = 0,
        val answerUser: String = "",
        val timer: Int = 60* Timer.MINUTES //5min
    ) {
        sealed class DetailsError {
            data class DataUpdateFailed(val cause: Throwable? = null): DetailsError()
        }
    }
    sealed class GuessFactUIEvent {
        data class CalculatePoints(val answerUser: String, val rightAnswer: String) : GuessFactUIEvent()
        data class SetAnswer(val answer: String) : GuessFactUIEvent()
    }
}