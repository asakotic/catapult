package com.example.catapult.cats.user


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.catapult.R
import com.example.catapult.cats.list.CatsViewModel
import com.example.catapult.core.AppIconButton
import com.example.catapult.core.theme.CatapultTheme

fun NavGraphBuilder.loginScreen(
    route : String,
    navController: NavController,
    goToQuiz: () -> Unit,
) = composable(route = route) {
    val catsViewModel: CatsViewModel = hiltViewModel()
    val catsState by catsViewModel.catsState.collectAsState()

    Surface(
        tonalElevation = 1.dp
    ) {
        LoginScreen(
            //loginState = loginState,
            //onClick = {catInfoDetail ->  navController.navigate("cats/${catInfoDetail.id}")}
        )
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(

){
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Hello, please enter your info", fontWeight = FontWeight.Bold)
                },
            )
        },
        content = {pv ->
            var name by rememberSaveable { mutableStateOf("") }
            var nick by rememberSaveable { mutableStateOf("") }
            var email by rememberSaveable { mutableStateOf("") }
            Column(
                modifier = Modifier.padding(pv).fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.login_cat),
                    contentDescription = "login cat photo",
                    modifier = Modifier
                        .padding(vertical = 20.dp)
                        .padding(bottom = 40.dp)
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name and Surname") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = nick,
                    onValueChange = { nick = it },
                    label = { Text("Nickname") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email address") },
                    singleLine = true
                )
                Button(onClick = { /*TODO*/ },modifier = Modifier.padding(vertical = 20.dp)) {
                    Text(text = "Log In")
                }
            }
        }
    )

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CatapultTheme {
        LoginScreen()
    }
}
