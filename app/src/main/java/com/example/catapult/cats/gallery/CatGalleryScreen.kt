package com.example.catapult.cats.gallery

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import coil.compose.SubcomposeAsyncImage
import com.example.catapult.cats.details.ICatDetailsContract
import com.example.catapult.core.AppIconButton

fun NavGraphBuilder.catGalleryScreen (
    route: String,
    navController: NavController,
    arguments: List<NamedNavArgument>
) = composable(route = route, arguments = arguments) { navBackStackEntry ->

    val catGalleryViewModel: CatGalleryViewModel = hiltViewModel(navBackStackEntry)
    val catState by catGalleryViewModel.catGalleryState.collectAsState()


    Surface(
        tonalElevation = 1.dp
    ) {
        Scaffold (
            content = { paddingValues ->
                CatGalleryScreen(
                    catState = catState,
                    paddingValues = paddingValues,
                    onClose = {navController.navigateUp()}
                )
            }
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatGalleryScreen(
    catState: ICatGalleryContract.CatGalleryState,
    paddingValues: PaddingValues,
    onClose: () -> Unit
){
    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Photo gallery", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    AppIconButton(imageVector = Icons.Default.ArrowBack, onClick = onClose)
                }
            )
        },
        content = {
            if (catState.isLoading) {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(it)) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            } else if (catState.error != null) {
                Box(
                    modifier = Modifier
                        .padding(it)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    val errorMessage = when (catState.error) {
                        is ICatGalleryContract.CatGalleryState.DetailsError.DataUpdateFailed ->
                            "Failed to load. Error message: ${catState.error.cause?.message}."
                    }

                    Text(text = errorMessage, fontSize = 20.sp)
                }
            } else if (catState.photos.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "There is no data for that cat",
                        fontSize = 20.sp
                    )
                }
            } else {
                LazyVerticalGrid(columns = GridCells.Fixed(2),
                    contentPadding = it) {

                    itemsIndexed(items = catState.photos,
                        key = { index: Int, item: String ->
                            item
                        }){index: Int, item: String ->
                        SubcomposeAsyncImage(
                            modifier =  Modifier.fillMaxWidth(),
                            model = item,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    )
}