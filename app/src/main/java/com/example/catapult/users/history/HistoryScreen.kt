package com.example.catapult.users.history

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.catapult.core.AppIconButton
import com.example.catapult.core.TopBar
import com.example.catapult.users.UserQuiz

fun NavGraphBuilder.historyScreen(
    route: String,
    navController: NavController,
) = composable(route = route) {

    val historyViewModel: HistoryViewModel = hiltViewModel()
    val historyState by historyViewModel.historyState.collectAsState()

    Surface(
        tonalElevation = 1.dp
    ) {
        Scaffold(
            topBar = { TopBar(navController = navController) }
        ) {
            Column(
                modifier = Modifier
                    .padding(it)
                    .padding(20.dp)
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceEvenly

                ) {
                    Card {
                        CardContent(
                            bestResult = historyViewModel.getBestResult("guessFact"),
                            historyState = historyState,
                            index = 0,
                            title = "Guess Fact",
                            items = historyState.usersData.users[historyState.usersData.pick].guessFact,
                            onClick = { uiEvent -> historyViewModel.setHistoryEvent(uiEvent) }
                        )
                    }
                    Card {
                        CardContent(
                            bestResult = historyViewModel.getBestResult("guessCat"),
                            historyState = historyState,
                            index = 1,
                            title = "Guess Cat",
                            items = historyState.usersData.users[historyState.usersData.pick].guessCat,
                            onClick = { uiEvent -> historyViewModel.setHistoryEvent(uiEvent) }
                        )
                    }
                    Card {
                        CardContent(
                            bestResult = historyViewModel.getBestResult("leftRightCat"),
                            historyState = historyState,
                            title = "Left Right Cat",
                            items = historyState.usersData.users[historyState.usersData.pick].leftRightCat,
                            index = 2,
                            onClick = { uiEvent -> historyViewModel.setHistoryEvent(uiEvent) }
                        )
                    }
                }
            }

        }
    }
}

@Composable
private fun CardContent(
    bestResult: String,
    historyState: IHistoryContract.HistoryState,
    title: String,
    items: UserQuiz,
    index: Int,
    onClick: (uiEvent: IHistoryContract.HistoryUIEvent) -> Unit
) {

    Column(
        modifier = Modifier
            .padding(20.dp)
            .animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(40.dp)
    ) {
        Row(
            modifier = Modifier.clickable {
                val list = historyState.expandedList.toMutableList()
                list[index] = !list[index]
                onClick(
                    IHistoryContract.HistoryUIEvent.Expanded(index = index)
                )
            },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(3 / 4f),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(text = "Best Result: $bestResult", style = MaterialTheme.typography.labelLarge)

                Text(text = title, style = MaterialTheme.typography.headlineSmall)
            }

            AppIconButton(
                imageVector = if (historyState.expandedList[index]) Icons.Filled.ExpandMore else Icons.Filled.ExpandLess,
                onClick = {
                    onClick(
                        IHistoryContract.HistoryUIEvent.Expanded(index = index)
                    )
                }
            )
        }

        if (historyState.expandedList[index]) {
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Points", style = MaterialTheme.typography.titleSmall)
                    Text(text = "Date", style = MaterialTheme.typography.titleSmall)
                }

                LazyColumn(
                    modifier = Modifier.heightIn(max = 200.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    items(items = items.resultsHistory) { result ->


                        Column(
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            HorizontalDivider()

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = result.result.toString())
                                Text(text = result.covertToDate())
                            }
                        }
                    }
                }
            }
        }
    }
}
