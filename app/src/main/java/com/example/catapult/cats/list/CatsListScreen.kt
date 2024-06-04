package com.example.catapult.cats.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.catapult.cats.domen.CatInfo
import com.example.catapult.cats.list.ICatsContract.CatsListState
import com.example.catapult.cats.list.ICatsContract.CatsListUIEvent
import com.example.catapult.core.AppIconButton
import com.example.catapult.core.ListInfo
import com.example.catapult.core.SimpleInfo

fun NavGraphBuilder.catsListScreen(
    route : String,
    navController: NavController
) = composable(route = route) {
    val catsViewModel:CatsViewModel = hiltViewModel()
    val catsState by catsViewModel.catsState.collectAsState()

    Scaffold (
        content = {
            CatsList(
                catsState = catsState,
                paddingValues = it,
                eventPublisher = {uiEvent ->  catsViewModel.setCatsEvent(uiEvent)},
                onClick = {catInfoDetail ->  navController.navigate("cats/${catInfoDetail.id}")}
            )
        }
    )
}

@Composable
fun CatsList(
    catsState : CatsListState,
    paddingValues: PaddingValues,
    eventPublisher: (uiEvent: CatsListUIEvent) -> Unit,
    onClick: (CatInfo) -> Unit
) {


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {

        TextField(
            value = catsState.searchText,
            onValueChange = { text ->
                eventPublisher(CatsListUIEvent.SearchQueryChanged(query = text))
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            placeholder = { Text(text = "Search")},
            shape = CircleShape,
            leadingIcon = { AppIconButton(imageVector = Icons.Default.Search, onClick = {  })}
        )

        if (catsState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
        else if (catsState.error != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                val errorMessage = when (catsState.error) {
                    is CatsListState.DetailsError.DataUpdateFailed ->
                        "Failed to load. Error message: ${catsState.error.cause?.message}."
                }

                Text(text = errorMessage, fontSize = 20.sp)
            }
        }
        else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = catsState.catsFiltered,
                    key = {catInfo -> catInfo.id}
                ) {catInfoDetail ->
                    CatDetails(
                        catInfo = catInfoDetail,
                        onClick = {onClick(catInfoDetail)}
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun CatDetails(
    catInfo: CatInfo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color(0xffff8141)
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            SimpleInfo(title = "Race Of Cat", description = catInfo.raceOfCat)
            Spacer(modifier = Modifier.height(16.dp))

            if (catInfo.altNames.isNotEmpty()) {
                ListInfo(title = "Alternative Names", items = catInfo.altNames)
                Spacer(modifier = Modifier.height(16.dp))
            }

            SimpleInfo(title = "Description", description = catInfo.description)
            Spacer(modifier = Modifier.height(16.dp))

            Row {
                catInfo.temperamentTraits.take(3).forEach {
                    AssistChip(
                        onClick = {  },
                        label = { Text(text = it) }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }
            }
        }
    }
}

