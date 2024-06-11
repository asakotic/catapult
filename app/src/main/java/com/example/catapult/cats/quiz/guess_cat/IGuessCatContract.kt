package com.example.catapult.cats.quiz.guess_cat

import com.example.catapult.cats.db.Cat
import com.example.catapult.cats.quiz.Timer

interface IGuessCatContract {
    data class GuessCatState(
        val isLoading: Boolean = false,
        val cats: List<Cat> = emptyList(),
        val questions: List<GuessCatQuestion> = emptyList(),
        val points: Float = 0f,
        val questionIndex: Int = 0,
        val timer: Int = 60* Timer.MINUTES //5min
    ) {
        sealed class DetailsError {
            data class DataUpdateFailed(val cause: Throwable? = null): DetailsError()
        }

        fun getTimeAsFormat(): String {
            val min = timer/60
            val sec = if (timer%60 < 10) "0${timer%60}" else timer%60
            return "${min}:${sec}"
        }
    }

    data class GuessCatQuestion(
        val cats: List<Cat> = emptyList(),
        val questionText: String,
        val correctAnswer: String,
    )

    sealed class GuessCatUIEvent {
        data class QuestionAnswered(val catAnswer: Cat) : GuessCatUIEvent()
    }
}