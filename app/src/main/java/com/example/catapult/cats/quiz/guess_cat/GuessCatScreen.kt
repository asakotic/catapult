package com.example.catapult.cats.quiz.guess_cat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import coil.compose.SubcomposeAsyncImage
import com.example.catapult.cats.quiz.left_right_cat.IUpDownCatContract
import com.example.catapult.cats.quiz.left_right_cat.LeftRightCatViewModel
import com.example.catapult.core.AppIconButton

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.guessCatScreen(
    route : String,
    navController: NavController
) = composable(route = route) {
    val quizViewModel: GuessCatViewModel = hiltViewModel()
    val quizState by quizViewModel.questionState.collectAsState()

    Surface(
        tonalElevation = 1.dp
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = "Quiz", style = MaterialTheme.typography.labelLarge)
                    },
                    navigationIcon = {
                        AppIconButton(
                            imageVector = Icons.Default.ArrowBack,
                            onClick = { navController.navigateUp() })
                    }
                )
            },
            content = {
                if (quizState.isLoading) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
                else {
                    UpDownScreen(
                        paddingValues = it,
                        quizState = quizState,
                        onClickImage = { uiEvent -> quizViewModel.setQuestionEvent(uiEvent) }
                    )
                }
            }
        )
    }
}

@Composable
fun UpDownScreen(
    paddingValues: PaddingValues,
    quizState: IGuessCatContract.GuessCatState,
    onClickImage: (uiEvent: IGuessCatContract.GuessCatUIEvent) -> Unit
) {

    val question = quizState.questions[quizState.questionIndex]
    Column(
        modifier = Modifier.padding(paddingValues)
    ) {
        Column(
            modifier = Modifier.padding(top = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Card(
                modifier = Modifier.padding(10.dp)
            ) {
                Text(
                    text = question.questionText,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.Absolute.SpaceBetween
            ) {
                Text(text = quizState.getTimeAsFormat(), style = MaterialTheme.typography.bodyLarge)
                Text(text = "${quizState.questionIndex + 1}/20", style = MaterialTheme.typography.bodyLarge)
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            ) {
                SubcomposeAsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clickable {
                            onClickImage(IGuessCatContract.GuessCatUIEvent.QuestionAnswered(catAnswer = question.cats[0]))
                        },
                    model = question.cats[0].image?.url ?: "",
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(modifier = Modifier.fillMaxSize()) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                )
                SubcomposeAsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clickable {
                            onClickImage(IGuessCatContract.GuessCatUIEvent.QuestionAnswered(catAnswer = question.cats[1]))
                        },
                    model = question.cats[1].image?.url ?: "",
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(modifier = Modifier.fillMaxSize()) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            ) {
                SubcomposeAsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clickable {
                            onClickImage(IGuessCatContract.GuessCatUIEvent.QuestionAnswered(catAnswer = question.cats[2]))
                        },
                    model = question.cats[2].image?.url ?: "",
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(modifier = Modifier.fillMaxSize()) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                )
                SubcomposeAsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clickable {
                            onClickImage(IGuessCatContract.GuessCatUIEvent.QuestionAnswered(catAnswer = question.cats[3]))
                        },
                    model = question.cats[3].image?.url ?: "",
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(modifier = Modifier.fillMaxSize()) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                )
            }
        }
    }
}