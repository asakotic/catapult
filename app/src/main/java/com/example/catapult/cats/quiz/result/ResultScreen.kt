package com.example.catapult.cats.quiz.result

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.catapult.R
import com.example.catapult.cats.quiz.guess_fact.IGuessFactContract

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.resultScreen(
    route: String,
    navController: NavController,
    arguments: List<NamedNavArgument>,
) = composable(route = route, arguments = arguments) { navBackStackEntry ->

    val resultViewModel: ResultViewModel = hiltViewModel(navBackStackEntry)
    val state by resultViewModel.resultState.collectAsState()


    Surface(
        tonalElevation = 1.dp
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Here are your results!",
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                )
            },
            content = { paddingValues ->
                ResultScreen(
                    state = state,
                    paddingValues = paddingValues,
                    leaderboard =
                    { category, nick ->
                        navController.navigate("quiz/leaderboard/${category}/${nick}")
                    },
                    home = { navController.navigate("cats") },
                    eventPublisher = { uiEvent -> resultViewModel.setEvent(uiEvent) },
                )
            }
        )
    }
}

@Composable
fun ResultScreen(
    state: IResultContract.ResultState,
    paddingValues: PaddingValues,
    leaderboard: (Int, String) -> Unit,
    home: () -> Unit,
    eventPublisher: (uiEvent: IResultContract.ResultUIEvent) -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.result_cat),
            contentDescription = "result cat photo",
            modifier = Modifier
                .padding(vertical = 20.dp)
                .padding(bottom = 40.dp)
        )
        Text(
            text = "${state.username}, your result is ${state.points}!\n" +
                    "Do you want to share it with other players?"
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(20.dp)
        ) {
            Button(
                onClick = {
                    eventPublisher(IResultContract.ResultUIEvent.PostResult)
                }, modifier = Modifier.padding(horizontal = 5.dp)
            ) {
                Text(text = "Share it")
            }
            Button(
                onClick = {
                    home()
                },
                modifier = Modifier.padding(horizontal = 5.dp)
            ) {
                Text(text = "Go back to home screen")
            }
        }
        Button(
            onClick = {
                leaderboard(state.category, state.username)
            },
            modifier = Modifier
                .padding(horizontal = 5.dp, vertical = 5.dp)
                .align(Alignment.End),
            enabled = if (state.isLoading) false else true
        ) {
            Text(text = "Go to leaderboard")
        }
    }

}