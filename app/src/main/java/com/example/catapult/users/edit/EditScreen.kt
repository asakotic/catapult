package com.example.catapult.users.edit

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import coil.compose.AsyncImage
import com.example.catapult.R
import com.example.catapult.core.TopBar

fun NavGraphBuilder.editScreen(
    route: String,
    navController: NavController
) = composable(route = route) {
    val editViewModel: EditViewModel = hiltViewModel()
    val editState by editViewModel.editState.collectAsState()

    Surface(
        tonalElevation = 1.dp
    ) {
        Scaffold(
            topBar = { TopBar(navController = navController) },
            content = {

                if (editState.saveUserPassed) {
                    navController.navigate("cats")
                }
                else {
                    LoginScreen(
                        paddingValues = it,
                        editState = editState,
                        onClick = { uiEvent ->
                            if (editViewModel.isInfoValid()) {
                                editViewModel.setEditEvent(uiEvent)
                            }
                        },
                        onValueChange = { uiEvent -> editViewModel.setEditEvent(uiEvent) }
                    )
                }
            }
        )
    }

}


@Composable
fun LoginScreen(
    paddingValues: PaddingValues,
    editState: IEditContract.EditState,
    onClick: (uiEvent: IEditContract.EditUIEvent) -> Unit,
    onValueChange: (uiEvent: IEditContract.EditUIEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(top = 50.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(40.dp)
    ) {
        Text(text = "Edit Profile", style = MaterialTheme.typography.headlineSmall)

        AsyncImage(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            model = "https://cdn2.thecatapi.com/images/J2PmlIizw.jpg",
            contentDescription = null
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = editState.name,
                onValueChange = {
                    onValueChange(
                        IEditContract.EditUIEvent.NameInputChanged(
                            name = it
                        )
                    )
                },
                label = { Text("Name and Surname") },
                singleLine = true
            )
            OutlinedTextField(
                value = editState.nickname,
                onValueChange = {
                    onValueChange(
                        IEditContract.EditUIEvent.NicknameInputChanged(
                            nickname = it
                        )
                    )
                },
                label = { Text("Nickname") },
                singleLine = true
            )
            OutlinedTextField(
                value = editState.email,
                onValueChange = {
                    onValueChange(
                        IEditContract.EditUIEvent.EmailInputChanged(
                            email = it
                        )
                    )
                },
                label = { Text("Email address") },
                singleLine = true
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { onClick(IEditContract.EditUIEvent.SaveChanges) },
            ) {
                Text(text = "Save")
            }
        }
    }

}