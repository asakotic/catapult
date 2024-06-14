package com.example.catapult.cats.quiz.leaderboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.catapult.core.AppIconButton

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.leaderboardScreen(
    route : String,
    navController: NavController,
    arguments: List<NamedNavArgument>,
) = composable(route = route, arguments = arguments) {
    val leaderboardViewModel: LeaderboardViewModel = hiltViewModel()
    val state by leaderboardViewModel.leaderboardState.collectAsState()

    Surface(
        tonalElevation = 1.dp
    ) {
        Scaffold (
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = "Leaderboard", fontWeight = FontWeight.Bold)
                    },
                    navigationIcon = {
                        AppIconButton(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            onClick = {
                                navController.navigate("cats")
                            }
                        )
                    }
                )
            },
            content = {
                LeaderboardScreen(
                    state = state,
                    paddingValues = it,
                    goHome = {}
                )
            }
        )
    }
}

@Composable
fun LeaderboardScreen(
    state : ILeaderboardContract.LeaderboardState,
    paddingValues: PaddingValues,
    goHome: ()->Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
        else if (state.error != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                val errorMessage = when (state.error) {
                    is ILeaderboardContract.LeaderboardState.DetailsError.DataUpdateFailed ->
                        "Failed to load. Error message: ${state.error.cause?.message}."
                }

                Text(text = errorMessage, fontSize = 20.sp)
            }
        }
        else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(
                    items = state.results,
                ) {index,user ->
                    val repeats = state.results.count { it.nickname == user.nickname }
                    ListItem(
                        colors = if(state.nick== user.nickname)
                            ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.inversePrimary)
                        else
                            ListItemDefaults.colors(),

                        headlineContent = { Text(text = user.nickname)},
                        supportingContent = { Text("User shared result $repeats times") },
                        trailingContent = { Text("${user.result} points") },
                        leadingContent = {
                            Text(text = (index+1).toString())
                        }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}


