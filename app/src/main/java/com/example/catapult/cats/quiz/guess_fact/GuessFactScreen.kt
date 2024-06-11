package com.example.catapult.cats.quiz.guess_fact

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.room.util.appendPlaceholders
import coil.compose.SubcomposeAsyncImage
import com.example.catapult.core.AppIconButton
import com.example.catapult.core.theme.onPrimaryContainerLight

fun NavGraphBuilder.guessFactScreen(
    route: String,
    navController: NavController
) = composable(route = route) {
    val guessFactViewModel: GuessFactViewModel = hiltViewModel()
    val state by guessFactViewModel.guessFactState.collectAsState()

    GuessFactScreen(
        state = state,
        eventPublisher = { uiEvent -> guessFactViewModel.setEvent(uiEvent) },
        navController = navController,
        seeResults = {time, points ->
            //UBP = BTO * 2.5 * (1 + (PVT + 120) / MVT)
            val ubp: Float = points * 2.5F * (1 + (time + 120F) / 300)
            Log.d("poeni", ubp.toString())
            navController.navigate("quiz/result/1/randomNick/${ubp}")
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuessFactScreen(
    state: IGuessFactContract.GuessFactState,
    eventPublisher: (uiEvent: IGuessFactContract.GuessFactUIEvent) -> Unit,
    navController: NavController,
    seeResults: (Int, Int) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Quiz", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    AppIconButton(imageVector = Icons.Default.ArrowBack, onClick = { navController.navigateUp()})
                }
            )
        },
        content = {

            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            } else if (state.error != null) {
                Box(
                    modifier = Modifier
                        .padding(it)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    val errorMessage = when (state.error) {
                        is IGuessFactContract.GuessFactState.DetailsError.DataUpdateFailed ->
                            "Failed to load. Error message: ${state.error.cause?.message}."
                    }

                    Text(text = errorMessage, fontSize = 20.sp)
                }
            } else if (state.cats.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "There is no data",
                        fontSize = 20.sp
                    )
                }
            } else {

                Column (
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(it)
                ){
                    val sec = state.timer%60
                    Text(text = "Time left: " + state.timer/60 + ":" + if(sec == 0)"00" else sec)
                    MakeQuestion(
                        state.answers,
                        state.image,
                        state.question,
                        eventPublisher,
                        state.questionIndex
                    )
                    Button(
                        onClick = {
                            Log.d("kviz", state.answerUser+ " "+ state.rightAnswer)
                            eventPublisher(
                                IGuessFactContract.GuessFactUIEvent.CalculatePoints(
                                    state.answerUser,
                                    state.rightAnswer
                                )
                            )
                            if(state.timer <= 0  || state.questionIndex > 19)
                                seeResults(state.timer, state.points)
                        },
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(horizontal = 20.dp)
                    ) {
                        Text(text = "Next")
                    }
                }

            }
        }
    )

}

@Composable
fun MakeQuestion(
    answers: List<String>,
    image: String,
    question: Int,
    eventPublisher: (uiEvent: IGuessFactContract.GuessFactUIEvent) -> Unit,
    questionNumber: Int
) {
    Log.d("print", answers.toString())
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(20.dp),
    ) {
        var text = ""

        text = when (question) {
            1 -> "What's the race of this cat on the photo?"
            2 -> "Odd one out!"
            else -> "Throw out one temperament!"
        }

        Text(text = "$questionNumber. $text")
        if(image.isNotEmpty())
            SubcomposeAsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(vertical = 15.dp, horizontal = 20.dp),
                model = image,
                contentDescription = null,
                contentScale = ContentScale.Inside,
            )
        Button(onClick = {
            eventPublisher(IGuessFactContract.GuessFactUIEvent.SetAnswer(answer = answers[0]))
        },
            modifier = Modifier
                .width(200.dp)
                .align(Alignment.CenterHorizontally),

        ) {
            Text(text = answers[0])
        }
        Button(onClick = {
            eventPublisher(IGuessFactContract.GuessFactUIEvent.SetAnswer(answer = answers[1]))
        },
            modifier = Modifier
                .width(200.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(text = answers[1])
        }

        Button(onClick = {
            eventPublisher(IGuessFactContract.GuessFactUIEvent.SetAnswer(answer = answers[2]))
        },
            modifier = Modifier
                .width(200.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(text = answers[2])
        }
        Button(onClick = {
            eventPublisher(IGuessFactContract.GuessFactUIEvent.SetAnswer(answer = answers[3]))
        },
            modifier = Modifier
                .width(200.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(text = answers[3])
        }
    }
}

