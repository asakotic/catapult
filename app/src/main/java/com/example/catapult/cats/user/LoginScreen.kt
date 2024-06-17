package com.example.catapult.cats.user


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.catapult.R
import com.example.catapult.cats.list.CatsViewModel
import com.example.catapult.core.AppIconButton
import com.example.catapult.core.theme.CatapultTheme

fun NavGraphBuilder.loginScreen(
    route: String,
    navController: NavController,
    arguments: List<NamedNavArgument>
) = composable(route = route, arguments = arguments) { navBackStackEntry ->
    val loginViewModel: LoginViewModel = hiltViewModel(navBackStackEntry)
    val loginState by loginViewModel.loginState.collectAsState()

    Surface(
        tonalElevation = 1.dp
    ) {
        if (loginViewModel.hasAccount()) {
            navController.navigate("cats")
        }
        else {
            LoginScreen(
                loginState = loginState,
                onClick = { uiEvent ->
                    if (loginViewModel.isInfoValid()) {
                        loginViewModel.setLoginEvent(uiEvent)
                        navController.navigate("cats")
                    }
                },
                onValueChange = { uiEvent -> loginViewModel.setLoginEvent(uiEvent) }
            )
        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    loginState: ILoginContract.LoginState,
    onClick: (uiEvent: ILoginContract.LoginUIEvent) -> Unit,
    onValueChange: (uiEvent: ILoginContract.LoginUIEvent) -> Unit
) {
    Scaffold(
        content = { pv ->
            Column(
                modifier = Modifier
                    .padding(pv)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text(text = "Hello, please enter your info", style = MaterialTheme.typography.headlineSmall)

                Image(
                    painter = painterResource(id = R.drawable.login_cat),
                    contentDescription = "login cat photo",
                    modifier = Modifier
                        .padding(vertical = 20.dp)
                        .padding(bottom = 40.dp)
                )

                OutlinedTextField(
                    value = loginState.name,
                    onValueChange = {
                        onValueChange(
                            ILoginContract.LoginUIEvent.NameInputChanged(
                                name = it
                            )
                        )
                    },
                    label = { Text("Name and Surname") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = loginState.nickname,
                    onValueChange = {
                        onValueChange(
                            ILoginContract.LoginUIEvent.NicknameInputChanged(
                                nickname = it
                            )
                        )
                    },
                    label = { Text("Nickname") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = loginState.email,
                    onValueChange = {
                        onValueChange(
                            ILoginContract.LoginUIEvent.EmailInputChanged(
                                email = it
                            )
                        )
                    },
                    label = { Text("Email address") },
                    singleLine = true
                )
                Button(onClick = { onClick(ILoginContract.LoginUIEvent.AddUser)}, modifier = Modifier.padding(vertical = 20.dp)) {
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
    }
}
