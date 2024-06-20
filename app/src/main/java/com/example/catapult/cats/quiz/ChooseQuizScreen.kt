package com.example.catapult.cats.quiz

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.catapult.R
import com.example.catapult.core.AppIconButton

fun NavGraphBuilder.chooseQuizScreen(
    route: String,
    navController: NavController
) = composable(route = route) {

    Surface(
        tonalElevation = 1.dp
    ) {
        ChooseQuizScreen(
            onClose = { navController.navigateUp() },
            factsQuiz = {navController.navigate("quiz/guess-fact")},
            guessCatQuiz = {navController.navigate("quiz/guess-cat")},
            leftRightCatQuiz = {navController.navigate("quiz/left-right-cat")}
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseQuizScreen(
    onClose: () -> Unit,
    guessCatQuiz: () -> Unit,
    factsQuiz: () ->Unit,
    leftRightCatQuiz: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Choose Quiz Category", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    AppIconButton(imageVector = Icons.Default.ArrowBack, onClick = onClose)
                }
            )
        },
        content = {
            Box(
                modifier = Modifier.padding(it),
                contentAlignment = Alignment.TopCenter
            ) {
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .verticalScroll(scrollState)
                        .align(Alignment.Center).padding(top = 60.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(30.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.quiz_pick),
                        contentDescription = "quiz picker photo",
                        modifier = Modifier
                    )

                    Column(
                        modifier = Modifier.padding(40.dp),
                        verticalArrangement = Arrangement.spacedBy(30.dp)
                    ) {
                        Button(
                            onClick = {
                                      factsQuiz()
                            },
                            modifier =  Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Guess the Fact Quiz")
                        }
                        Button(
                            onClick = { guessCatQuiz() },
                            modifier =  Modifier.fillMaxWidth()
                            ) {
                            Text(text = "Guess the Cat Quiz")
                        }
                        Button(
                            onClick = { leftRightCatQuiz() },
                            modifier =  Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Left or Right Cat Quiz")
                        }
                    }

                }
            }
        }
    )
}
/*
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CatapultTheme {
        ChooseQuizScreen() {

        }
    }
}

 */
