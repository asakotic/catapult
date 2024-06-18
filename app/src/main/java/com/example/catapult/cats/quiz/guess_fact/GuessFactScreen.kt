package com.example.catapult.cats.quiz.guess_fact

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import coil.compose.SubcomposeAsyncImage
import com.example.catapult.core.AppIconButton
import com.example.catapult.core.CustomRippleTheme
import com.example.catapult.core.ProgressBarOurs
import com.example.catapult.core.getTimeAsFormat

fun NavGraphBuilder.guessFactScreen(
    route: String,
    navController: NavController
) = composable(route = route) {
    val guessFactViewModel: GuessFactViewModel = hiltViewModel()
    val state by guessFactViewModel.guessFactState.collectAsState()

    if (state.questionIndex == 20 && state.result != null) {
        val user = state.usersData.users[state.usersData.pick]
        navController.navigate("quiz/result/1/${state.result?.result ?: 0}")
    }
    else {
        GuessFactScreen(
            state = state,
            eventPublisher = { uiEvent -> guessFactViewModel.setEvent(uiEvent) },
            navController = navController,
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuessFactScreen(
    state: IGuessFactContract.GuessFactState,
    eventPublisher: (uiEvent: IGuessFactContract.GuessFactUIEvent) -> Unit,
    navController: NavController,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Quiz", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    AppIconButton(imageVector = Icons.AutoMirrored.Filled.ArrowBack, onClick = { navController.navigateUp()})
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
                    MakeQuestion(
                        state,
                        eventPublisher,
                    )
                }

            }
        }
    )

}

@Composable
fun MakeQuestion(
    state: IGuessFactContract.GuessFactState,
    eventPublisher: (uiEvent: IGuessFactContract.GuessFactUIEvent) -> Unit,
) {

    ProgressBarOurs(index = state.questionIndex, size = 20)
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(20.dp),
    ) {

        val text: String = when (state.question) {
            1 -> "What's the race of this cat on the photo?"
            2 -> "Odd one out!"
            else -> "Throw out one temperament!"
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Card(
                modifier = Modifier.padding(10.dp)
            ) {
                Text(
                    text = text,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                )
                if(state.image.isNotEmpty())
                    SubcomposeAsyncImage(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .padding(vertical = 15.dp, horizontal = 20.dp),
                        model = state.image,
                        contentDescription = null,
                        contentScale = ContentScale.Inside,
                    )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.Absolute.SpaceBetween
            ) {
                Text(text = getTimeAsFormat(state.timer), style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = "${state.questionIndex}/20",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                CompositionLocalProvider(
                    LocalRippleTheme provides CustomRippleTheme(
                        color =
                        if (state.rightAnswer == state.answers[0])
                            MaterialTheme.colorScheme.tertiary
                        else
                            MaterialTheme.colorScheme.error
                    )
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                            .clickable {
                                eventPublisher(
                                    IGuessFactContract.GuessFactUIEvent.CalculatePoints(
                                        state.answers[0]
                                    )
                                )
                            },
                    ){
                        Text(
                            modifier = Modifier
                                .fillMaxSize()
                                .wrapContentHeight(Alignment.CenterVertically)
                                .align(Alignment.CenterHorizontally),
                            text = state.answers[0],
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }
                CompositionLocalProvider(
                    LocalRippleTheme provides CustomRippleTheme(
                        color =
                        if (state.rightAnswer == state.answers[1])
                            MaterialTheme.colorScheme.tertiary
                        else
                            MaterialTheme.colorScheme.error
                    )
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                            .clickable {
                                eventPublisher(
                                    IGuessFactContract.GuessFactUIEvent.CalculatePoints(
                                        state.answers[1]
                                    )
                                )
                            },
                    ){
                        Text(
                            modifier = Modifier
                                .fillMaxSize()
                                .wrapContentHeight(Alignment.CenterVertically)
                                .align(Alignment.CenterHorizontally),
                            text = state.answers[1],
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            ) {
                CompositionLocalProvider(
                    LocalRippleTheme provides CustomRippleTheme(
                        color =
                        if (state.rightAnswer == state.answers[2])
                            MaterialTheme.colorScheme.tertiary
                        else
                            MaterialTheme.colorScheme.error
                    )
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                            .clickable {
                                eventPublisher(
                                    IGuessFactContract.GuessFactUIEvent.CalculatePoints(
                                        state.answers[2]
                                    )
                                )
                            },
                    ){
                        Text(
                            modifier = Modifier
                                .fillMaxSize()
                                .wrapContentHeight(Alignment.CenterVertically)
                                .align(Alignment.CenterHorizontally),
                            text = state.answers[2],
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }

                CompositionLocalProvider(
                    LocalRippleTheme provides CustomRippleTheme(
                        color =
                        if (state.rightAnswer == state.answers[3])
                            MaterialTheme.colorScheme.tertiary
                        else
                            MaterialTheme.colorScheme.error
                    )
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                            .clickable {
                                eventPublisher(
                                    IGuessFactContract.GuessFactUIEvent.CalculatePoints(
                                        state.answers[3]
                                    )
                                )
                            },
                    ){
                        Text(
                            modifier = Modifier
                                .fillMaxSize()
                                .wrapContentHeight(Alignment.CenterVertically)
                                .align(Alignment.CenterHorizontally),
                            text = state.answers[3],
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }
            }
        }
    }
}

