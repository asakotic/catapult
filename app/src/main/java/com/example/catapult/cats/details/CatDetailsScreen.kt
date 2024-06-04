package com.example.catapult.cats.details

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import coil.compose.SubcomposeAsyncImage
import com.example.catapult.cats.domen.CatInfo
import com.example.catapult.core.AppIconButton
import com.example.catapult.core.ListInfo
import com.example.catapult.core.SimpleInfo

fun NavGraphBuilder.catDetailsScreen (
    route: String,
    navController: NavController,
    arguments: List<NamedNavArgument>
) = composable(route = route, arguments = arguments) { navBackStackEntry ->

    val catDetailsViewModel: CatDetailsViewModel = hiltViewModel(navBackStackEntry)
    val catState by catDetailsViewModel.catDetailsState.collectAsState()
    
    Scaffold (
        content = { paddingValues ->
            CatDetailsScreen(
                catState = catState,
                paddingValues = paddingValues,
                onClose = {navController.navigateUp()}
            )
        }    
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatDetailsScreen(
    catState: ICatDetailsContract.CatDetailsState,
    paddingValues: PaddingValues,
    onClose: () -> Unit
) {
    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Catapult", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    AppIconButton(imageVector = Icons.Default.ArrowBack, onClick = onClose)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xffff8141)
                )
            )
        },
        content = {
            if (catState.isLoading) {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
            else if (catState.error != null) {
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    val errorMessage = when (catState.error) {
                        is ICatDetailsContract.CatDetailsState.DetailsError.DataUpdateFailed ->
                            "Failed to load. Error message: ${catState.error.cause?.message}."
                    }

                    Text(text = errorMessage, fontSize = 20.sp)
                }
            }
            else if (catState.data == null) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "There is no data for id ${catState.catId}",
                        fontSize = 20.sp
                    )
                }
            }
            else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopCenter
                ) {

                    val scrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .padding(it)
                            .verticalScroll(scrollState)
                    ) {
                        Card(
                            shape = RectangleShape,
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Gray
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            SubcomposeAsyncImage(
                                modifier =  Modifier.fillMaxWidth(),
                                model = catState.data.catImageUrl,
                                contentDescription = null
                            )
                        }

                        Card(
                            shape = RectangleShape,
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            CatInformation(catState.data)
                        }

                    }
                }
            }
        }
    )
}

@Composable
private fun CatInformation(
    data: CatInfo
) {
    Column(
        modifier = Modifier
            .padding(start = 16.dp, top = 16.dp, end = 16.dp)
    ) {

        SimpleInfo(
            title = "Race Of Cat",
            description = data.raceOfCat
        )

        Spacer(modifier = Modifier.height(16.dp))

        SimpleInfo(
            title = "description",
            description = data.description
        )

        Spacer(modifier = Modifier.height(16.dp))

        ListInfo(title = "Countries Of Origin", items = data.countriesOfOrigin)

        Spacer(modifier = Modifier.height(16.dp))

        ListInfo(title = "Temperament Traits", items = data.temperamentTraits)

        Spacer(modifier = Modifier.height(16.dp))

        SimpleInfo(
            title = "Average Weight",
            description = data.averageWeight
        )

        Spacer(modifier = Modifier.height(16.dp))

        SimpleInfo(
            title = "Rare",
            description = data.isRare.toString()
        )

        Spacer(modifier = Modifier.height(16.dp))

        SimpleInfo(
            title = "Life Span",
            description = data.lifeSpan
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column {
            data.mapWidgets.forEach {

                StarRatingBar(text = it.key, rating = it.value.toFloat()) {}

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        val context = LocalContext.current

        Button(
            onClick =
            {
                context.startActivity(Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(data.wikiUrl)
                ))
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xffff8141)
            )
        ) {
            Text(text = "Wiki", color = Color.Black)
        }

    }
}

@Composable
fun StarRatingBar(
    text: String,
    maxStars: Int = 5,
    rating: Float,
    onRatingChanged: (Float) -> Unit
) {
    val density = LocalDensity.current.density
    val starSize = (12f * density).dp
    val starSpacing = (0.5f * density).dp

    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {

        Text(text = "$text:")

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.selectableGroup(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 1..maxStars) {
                val isSelected = i <= rating
                val icon = if (isSelected) Icons.Filled.Star else Icons.Default.Star
                val iconTintColor = if (isSelected) Color(0xFFFFC700) else Color(0xFFbdbdbd)
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTintColor,
                    modifier = Modifier
                        .selectable(
                            selected = isSelected,
                            onClick = {
                                onRatingChanged(i.toFloat())
                            }
                        )
                        .width(starSize)
                        .height(starSize)
                )

                if (i < maxStars) {
                    Spacer(modifier = Modifier.width(starSpacing))
                }
            }
        }
    }
}
