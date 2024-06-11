package com.example.catapult.cats.quiz.left_right_cat

import com.example.catapult.cats.db.Cat
import com.example.catapult.cats.quiz.Timer

interface IUpDownCatContract {

    data class UpDownCatState(
        val isLoading: Boolean = false,
        val cats: List<Cat> = emptyList(),
        val questions: List<UpDownCatQuestion> = emptyList(),
        val points: Float = 0f,
        val questionIndex: Int = 0,
        val timer: Int = 60*Timer.MINUTES //5min
    ) {
        sealed class DetailsError {
            data class DataUpdateFailed(val cause: Throwable? = null): DetailsError()
        }
    }

    data class UpDownCatQuestion(
        val cat1: Cat,
        val cat1Image: String? = null,
        val cat2: Cat,
        val questionText: String,
        val correctAnswer: String,
        val randNumForQuestion: Int
    )

    sealed class UpDownCatUIEvent {
        data class QuestionAnswered(val catAnswer: Cat) : UpDownCatUIEvent()
    }
}