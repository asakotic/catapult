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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import kotlin.random.Random

@HiltViewModel
class LeftRightCatViewModel @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val catsService: CatsService
) : ViewModel() {

    private val _questionState = MutableStateFlow(UpDownCatState())
    val questionState = _questionState.asStateFlow()

    private val _questionEvent = MutableSharedFlow<IUpDownCatContract.UpDownCatUIEvent>()

    private var timerJob: Job? = null

    private fun setQuestionState(update: UpDownCatState.() -> UpDownCatState) =
        _questionState.getAndUpdate(update)

    fun setQuestionEvent(even: IUpDownCatContract.UpDownCatUIEvent) = viewModelScope.launch { _questionEvent.emit(even) }

    init {
        getAllCats()
        observeUpDownCatEvent()
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob =  viewModelScope.launch {
            while (true) {
                delay(1000)
                setQuestionState { copy(timer = timer - 1) }

                if (questionState.value.timer <= 0)
                    pauseTimer()
            }

        }
    }

    private fun pauseTimer() {
        timerJob?.cancel()
    }

    private fun getAllCats() {
        viewModelScope.launch {
            setQuestionState { copy(isLoading = true) }
            val cats = catsService.getAllCatsFlow().first().shuffled()
            setQuestionState { copy(cats = cats) }
            createQuestions()
            setQuestionState { copy(isLoading = false) }
        }
    }

    private fun observeUpDownCatEvent() {
        viewModelScope.launch {
            _questionEvent.collect {
                when (it) {
                    is IUpDownCatContract.UpDownCatUIEvent.QuestionAnswered -> checkAnswer(it.catAnswer)
                }
            }
        }
    }

    private fun checkAnswer(catAnswer: Cat) {
        var questionIndex= questionState.value.questionIndex
        val question = questionState.value.questions[questionIndex]
        var points = questionState.value.points
        if (catAnswer.id == question.correctAnswer)
            points++

        if (questionIndex < 19)
            questionIndex++
        else { //End Screen
            pauseTimer()
        }
        setQuestionState {
            copy(
                questionIndex = questionIndex,
                points = points,
            )
        }
    }

    /**
     * Creates 20 random questions and saves them in a list
     */
    private fun createQuestions() {
        //todo random images of cat
        val cats = questionState.value.cats
        val questions: MutableList<UpDownCatQuestion> = ArrayList()
        val len = cats.size
        var skip = 0
        var i = -1

        while (++i < 20 + skip) {
            val cat1 = cats[i]
            val cat2 = cats[len - i - 1]

            //If cat doesn't have images, it shouldn't be in the game
            if (cat1.image == null || cat2.image == null) {
                skip++
                continue
            }

            val randomQuestion = Random.nextInt(1, 3)
            val photos: List<String>
            questions.add(
                UpDownCatQuestion(
                    cat1 = cat1,
//                            cat1Image = photos[j - i - 1],
                    cat2 = cat2,
                    questionText = giveQuestion(randomQuestion),
                    correctAnswer = giveAnswer(randomQuestion, cat1, cat2),
                    randNumForQuestion = randomQuestion
                )
            )
        }
        setQuestionState { copy(questions = questions.shuffled())}
    }

    private fun giveQuestion(num: Int): String {
        return when (num) {
            1 -> "Which cat weights more on average?"
            else -> "Which cat has longer life span on average?"
        }
    }

    private fun giveAnswer(num: Int, cat1: Cat, cat2: Cat): String {
        return when (num) {
            1 -> if (cat1.averageWeight() > cat2.averageWeight()) cat1.id else cat2.id
            else -> if (cat1.averageLifeSpan() > cat2.averageLifeSpan()) cat1.id else cat2.id
        }
    }
}