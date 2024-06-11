package com.example.catapult.cats.quiz.result

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.resultScreen (
    route: String,
    navController: NavController,
    arguments: List<NamedNavArgument>,
) = composable(route = route, arguments = arguments) { navBackStackEntry ->

    val resultViewModel: ResultViewModel = hiltViewModel(navBackStackEntry)
    val state by resultViewModel.resultState.collectAsState()


    Surface(
        tonalElevation = 1.dp
    ) {
        Scaffold (
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = "Here are your results!", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    }
                )
            },
            content = { paddingValues ->
                ResultScreen(
                    state = state,
                    paddingValues = paddingValues,
                    leaderboard = {category -> navController.navigate("quiz/leaderboard/${category}")},
                    home = {navController.navigate("cats")}
                )
            }
        )
    }
}

@Composable
fun ResultScreen(
    state: IResultContract.ResultState,
    paddingValues: PaddingValues,
    leaderboard: (Int) -> Unit,
    home:() -> Unit
){
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "${state.username}, your result is ${state.points}!\n" +
                "Do you want to share it with other players?")
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(20.dp)
        ) {
            Button(onClick = {
                //TODO: post it
                //leaderboard(state.category)
            },modifier = Modifier.padding(horizontal = 5.dp)
                ) {
                Text(text = "Share it")
            }
            Button(onClick = {
                home()
            },
                modifier = Modifier.padding(horizontal = 5.dp)) {
                Text(text = "Go back to home screen")
            }
        }

    }

}