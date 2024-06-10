package com.example.catapult.cats.quiz.left_right_cat

import com.example.catapult.cats.db.Cat

interface IUpDownCatContract {

    data class UpDownCatState(
        val isLoading: Boolean = false,
        val cats: List<Cat> = emptyList(),
        val questions: List<UpDownCatQuestion> = emptyList(),
        val points: Float = 0f,
        val questionIndex: Int = 0
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
        val correctAnswer: Int,
        val randNumForQuestion: Int
    )

    sealed class UpDownCatUIEvent {
        data class QuestionAnswered(val catAnswer: Cat) : UpDownCatUIEvent()
    }
}