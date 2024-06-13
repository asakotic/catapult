package com.example.catapult.cats.quiz.left_right_cat

import com.example.catapult.cats.db.Cat
import com.example.catapult.cats.quiz.Timer

interface ILeftRightCatContract {

    data class LeftRightCatState(
        val isLoading: Boolean = false,
        val cats: List<Cat> = emptyList(),
        val questions: List<LeftRightCatQuestion> = emptyList(),
        val points: Float = 0f,
        val questionIndex: Int = 0,
        val timer: Int = 60*Timer.MINUTES //5min
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

    data class LeftRightCatQuestion(
        val cat1: Cat,
        val cat1Image: String? = null,
        val cat2: Cat,
        val questionText: String,
        val correctAnswer: String,
    )

    sealed class LeftRightCatUIEvent {
        data class QuestionAnswered(val catAnswer: Cat) : LeftRightCatUIEvent()
    }
}