package com.example.catapult.cats.quiz.left_right_cat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catapult.cats.db.Cat
import com.example.catapult.cats.db.CatsService
import com.example.catapult.di.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import com.example.catapult.cats.quiz.left_right_cat.IUpDownCatContract.UpDownCatState
import com.example.catapult.cats.quiz.left_right_cat.IUpDownCatContract.UpDownCatQuestion
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

@HiltViewModel
class LeftRightCatViewModel @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val catsService: CatsService
) : ViewModel() {

    private val _questionState = MutableStateFlow(UpDownCatState())
    val questionState = _questionState.asStateFlow()

    private val _questionEvent = MutableSharedFlow<IUpDownCatContract.UpDownCatUIEvent>()

    private fun setUpDownCatState(update: UpDownCatState.() -> UpDownCatState) =
        _questionState.getAndUpdate(update)

    fun setQuestionEvent(even: IUpDownCatContract.UpDownCatUIEvent) = viewModelScope.launch { _questionEvent.emit(even) }

    init {
        getAllCats()
        observeUpDownCatEvent()
    }

    private fun getAllCats() {
        viewModelScope.launch {
            setUpDownCatState { copy(isLoading = true) }
            val cats = catsService.getAllCatsFlow().first().shuffled()
            setUpDownCatState { copy(cats = cats) }
            createQuestions()
            setUpDownCatState { copy(isLoading = false) }
        }
    }

    private fun observeUpDownCatEvent() {
        viewModelScope.launch {
            _questionEvent.collect {
                when (it) {
                    is IUpDownCatContract.UpDownCatUIEvent.QuestionAnswered -> checkAnswer(it.answer)
                }
            }
        }
    }

    private fun checkAnswer(answer: Int) {
        var questionIndex= questionState.value.questionIndex
        val question = questionState.value.questions[questionIndex]
        var points = questionState.value.points
        if (answer == question.correctAnswer)
            points++

        if (questionIndex < 20)
            questionIndex++
        setUpDownCatState {
            copy(
                questionIndex = questionIndex,
                points = points,
            )
        }
    }

    private fun createQuestions() {
        //todo random images of cat
        setUpDownCatState { copy(isLoading = true) }
        val cats = questionState.value.cats
        val questions: MutableList<UpDownCatQuestion> = ArrayList()
        for (i in 0..<7) {
            val cat1 = cats[i]
            val randomQuestion = Random.nextInt(1, 5)
            val photos: List<String>
            for (j in i + 1..i + 3) {
                val cat2 = cats[j]
                questions.add(
                    UpDownCatQuestion(
                        cat1 = cat1,
//                            cat1Image = photos[j - i - 1],
                        cat2 = cat2,
                        questionText = giveQuestion(randomQuestion),
                        correctAnswer = giveAnswer(randomQuestion, cat1, cat2)
                    )
                )
            }
        }
        setUpDownCatState { copy(questions = questions.shuffled(), isLoading = false) }
    }

    private fun giveQuestion(num: Int): String {
        return when (num) {
            1 -> "Which cat is more energetic?"
            2 -> "Which cat has more health issues?"
            3 -> "Which cat is more dog friendly?"
            else -> "Which cat is more child friendly?"
        }
    }

    private fun giveAnswer(num: Int, cat1: Cat, cat2: Cat): Int {
        val randNum = Random.nextInt(1, 3)
        val cat = when (randNum) {
            1 -> cat1
            else -> cat2
        }
        return when (num) {
            1 -> cat.energyLevel
            2 -> cat.healthIssues
            3 -> cat.dogFriendly
            else -> cat.childFriendly
        }
    }
}