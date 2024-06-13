package com.example.catapult.cats.quiz.guess_cat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catapult.cats.db.Cat
import com.example.catapult.cats.db.CatsService
import com.example.catapult.di.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class GuessCatViewModel @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val catsService: CatsService
) : ViewModel() {
    private val _questionState = MutableStateFlow(IGuessCatContract.GuessCatState())
    val questionState = _questionState.asStateFlow()

    private val _questionEvent = MutableSharedFlow<IGuessCatContract.GuessCatUIEvent>()

    private var timerJob: Job? = null

    private val invalid = "invalid"

    private fun setQuestionState(update: IGuessCatContract.GuessCatState.() -> IGuessCatContract.GuessCatState) =
        _questionState.getAndUpdate(update)

    fun setQuestionEvent(even: IGuessCatContract.GuessCatUIEvent) = viewModelScope.launch { _questionEvent.emit(even) }

    init {
        getAllCats()
        observeCatsEvent()
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

    private fun observeCatsEvent() {
        viewModelScope.launch {
            _questionEvent.collect {
                when (it) {
                    is IGuessCatContract.GuessCatUIEvent.QuestionAnswered -> checkAnswer(it.catAnswer)
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
        val questions: MutableList<IGuessCatContract.GuessCatQuestion> = ArrayList()
        val len = cats.size
        var skip = 0
        var i = -1
        var questionNumber = 0

        while (++i < 20 + skip) {

            //If cat doesn't have images, it shouldn't be in the game
            if (cats[i].image == null || cats[i + 1].image == null || cats[i + 2].image == null || cats[i + 3].image == null) {
                skip++
                continue
            }

            var question: String = ""
            var answer: Pair<String, String> = Pair(invalid, invalid) //(id, answer)
            for (j in 1..2) {
                questionNumber = (questionNumber + 1) % 2

                answer = giveAnswer(questionNumber, i)
                if (answer.first != invalid) {
                    question = giveQuestion(questionNumber, answer.second)
                    break
                }
            }

            if (answer.first == invalid) {
                skip++
                continue
            }

            questions.add(
                IGuessCatContract.GuessCatQuestion(
                    cats = cats.slice(i..i+3),
                    questionText = question,
                    correctAnswer = answer.first,

                )
            )
        }
        println(questions)
        setQuestionState { copy(questions = questions.shuffled())}
    }

    private fun giveAnswer(questionNumber: Int, i: Int): Pair<String, String> {
        return when (questionNumber) {
            0 -> giveTemperament(i)
            else -> giveRace(i)
        }
    }

    private fun giveTemperament(i: Int): Pair<String, String> {
        val cats = questionState.value.cats
        val allTemps = cats[i].getListOfTemperaments() +
                cats[i + 1].getListOfTemperaments() +
                cats[i + 2].getListOfTemperaments() +
                cats[i + 3].getListOfTemperaments()

        val uniqueTemps = allTemps
            .groupBy { it.lowercase() }
            .filter { it.value.size == 1 }
            .flatMap { it.value }
            .shuffled()

        var currNum = Random.nextInt(0, 4)
        for (j in 0..3) {
            if (cats[i + currNum].getListOfTemperaments().contains(uniqueTemps[0]))
                return Pair(cats[i + currNum].id, uniqueTemps[0])
            currNum = (currNum + 1) % 4
        }
        return Pair(invalid, invalid)
    }

    private fun giveRace(i: Int): Pair<String, String> {
        val cats = questionState.value.cats
        val rand = Random.nextInt(0, 4)
        return Pair(cats[i + rand].id, cats[i + rand].name)
    }


    private fun giveQuestion(num:Int, answer: String): String {
        return when (num) {
            0 -> "Which cat is $answer?"
            else -> "Which cat belongs to a race $answer?"
        }
    }


}