package com.example.catapult.cats.quiz.guess_cat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catapult.cats.db.Cat
import com.example.catapult.cats.db.CatsService
import com.example.catapult.cats.list.ICatsContract
import com.example.catapult.core.seeResults
import com.example.catapult.di.DispatcherProvider
import com.example.catapult.users.Result
import com.example.catapult.users.UsersDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class GuessCatViewModel @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val usersDataStore: UsersDataStore,
    private val catsService: CatsService
) : ViewModel() {
    private val _questionState =
        MutableStateFlow(IGuessCatContract.GuessCatState(usersData = usersDataStore.data.value))
    val questionState = _questionState.asStateFlow()

    private val _questionEvent = MutableSharedFlow<IGuessCatContract.GuessCatUIEvent>()

    private var timerJob: Job? = null

    private val invalid = "invalid"

    private fun setQuestionState(update: IGuessCatContract.GuessCatState.() -> IGuessCatContract.GuessCatState) =
        _questionState.getAndUpdate(update)

    fun setQuestionEvent(even: IGuessCatContract.GuessCatUIEvent) =
        viewModelScope.launch { _questionEvent.emit(even) }

    init {
        getAllCats()
        observeCatsEvent()
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                setQuestionState { copy(timer = timer - 1) }

                if (questionState.value.timer <= 0)
                    pauseTimer()
            }

        }
    }

    private fun addResult(result: Result) {
        viewModelScope.launch {
            usersDataStore.addGuessCatResult(result)
            setQuestionState { copy(result = result) }
        }
    }

    fun isCorrectAnswer(catId: String): Boolean {
        val questionIndex = questionState.value.questionIndex
        val question = questionState.value.questions[questionIndex]
        return catId == question.correctAnswer
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
                    is IGuessCatContract.GuessCatUIEvent.AddResult -> addResult(it.result)
                }
            }
        }
    }

    private fun checkAnswer(catAnswer: Cat) {
        var questionIndex = questionState.value.questionIndex
        val question = questionState.value.questions[questionIndex]
        var points = questionState.value.points
        if (catAnswer.id == question.correctAnswer)
            points++

        if (questionIndex < 19)
            questionIndex++
        else { //End Screen
            pauseTimer()
            addResult(
                Result(
                    result = seeResults(questionState.value.timer, points.toInt()),
                    createdAt = System.currentTimeMillis()
                )
            )
        }
        setQuestionState {
            copy(
                questionIndex = questionIndex,
                points = points,
            )
        }

    }

    private suspend fun getPictures(id: String): List<String> {
        val photos = catsService.getAllCatImagesByIdFlow(id = id).first()

        if (photos.isNotEmpty())
            return photos

        withContext(dispatcherProvider.io()) {
            catsService.fetchAllCatsFromApi()
        }

        return catsService.getAllCatImagesByIdFlow(id = id).first()
    }

    /**
     * Creates 20 random questions and saves them in a list
     */
    private suspend fun createQuestions() {
        val cats = questionState.value.cats
        val questions: MutableList<IGuessCatContract.GuessCatQuestion> = ArrayList()
        val len = cats.size
        var skip = 0
        var i = 2
        var questionNumber = 0
        var pictureIndex = -1

        var cat1photos = getPictures(cats[0].id)
        var cat2photos = getPictures(cats[1].id)
        var cat3photos = getPictures(cats[2].id)

        while (++i < 23 + skip) {
            pictureIndex++

            //This way more cats combination are possible
            val cat4photos = getPictures(cats[i].id)
            if (cat4photos.isEmpty()) {
                skip++
                continue
            }

            //If cat doesn't have images, it shouldn't be in the game
            if (cat1photos.isEmpty() || cat2photos.isEmpty() || cat3photos.isEmpty()) {
                skip++
                cat1photos = cat2photos
                cat2photos = cat3photos
                cat3photos = cat4photos
                continue
            }

            var question = ""
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
                cat1photos = cat2photos
                cat2photos = cat3photos
                cat3photos = cat4photos
                continue
            }

            questions.add(
                IGuessCatContract.GuessCatQuestion(
                    cats = cats.slice(i - 3..i),
                    images = listOf(
                        cat1photos[pictureIndex % cat1photos.size],
                        cat2photos[pictureIndex % cat2photos.size],
                        cat3photos[pictureIndex % cat3photos.size],
                        cat4photos[pictureIndex % cat4photos.size],
                    ),
                    questionText = question,
                    correctAnswer = answer.first,

                    )
            )

            cat1photos = cat2photos
            cat2photos = cat3photos
            cat3photos = cat4photos
        }
        setQuestionState { copy(questions = questions.shuffled()) }
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


    private fun giveQuestion(num: Int, answer: String): String {
        return when (num) {
            0 -> "Which cat is $answer?"
            else -> "Which cat belongs to a race $answer?"
        }
    }


}