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
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import coil.compose.SubcomposeAsyncImage
import com.example.catapult.cats.db.Cat
import com.example.catapult.core.AppIconButton
import com.example.catapult.core.ListInfo
import com.example.catapult.core.SimpleInfo

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.catDetailsScreen (
    route: String,
    navController: NavController,
    arguments: List<NamedNavArgument>
) = composable(route = route, arguments = arguments) { navBackStackEntry ->

    val catDetailsViewModel: CatDetailsViewModel = hiltViewModel(navBackStackEntry)
    val catState by catDetailsViewModel.catDetailsState.collectAsState()

    Surface(
        tonalElevation = 1.dp
    ) {
        Scaffold (
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = "Catapult", style = MaterialTheme.typography.labelLarge)
                    },
                    navigationIcon = {
                        AppIconButton(imageVector = Icons.Default.ArrowBack, onClick = {navController.navigateUp()})
                    },
                    actions = {
                        //Icons.Outlined.LightMode -- dark mode
                        //Icons.Filled.LightMode -- light mode
                        AppIconButton(imageVector = Icons.Filled.LightMode, onClick = { /*TODO*/ })
                        AppIconButton(imageVector = Icons.Default.Menu, onClick = { /*TODO*/ })
                    }
                )
            },
            content = { paddingValues ->
                CatDetailsScreen(
                    catState = catState,
                    paddingValues = paddingValues,
                    openGallery = {id ->  navController.navigate("images/${id}")}
                )
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatDetailsScreen(
    catState: ICatDetailsContract.CatDetailsState,
    paddingValues: PaddingValues,
    openGallery: (String)-> Unit,
) {
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
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
            ) {
                Card(
                    shape = RectangleShape,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    SubcomposeAsyncImage(
                        modifier =  Modifier.fillMaxWidth(),
                        model = catState.data.image?.url ?: "",
                        contentDescription = null
                    )
                    Button(
                        onClick = {
                            openGallery(catState.catId)
                        }
                    ) {
                        Text(text = "Gallery")
                    }
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

@Composable
private fun CatInformation(
    data: Cat
) {
    Column(
        modifier = Modifier
            .padding(start = 16.dp, top = 16.dp, end = 16.dp)
    ) {

        SimpleInfo(
            title = "Race Of Cat",
            description = data.name
        )

        Spacer(modifier = Modifier.height(16.dp))

        SimpleInfo(
            title = "description",
            description = data.description
        )

        Spacer(modifier = Modifier.height(16.dp))

        ListInfo(title = "Countries Of Origin", items = data.origin.replace(" ", "").split(","))

        Spacer(modifier = Modifier.height(16.dp))

        ListInfo(title = "Temperament Traits", items = data.temperament.replace(" ", "").split(","))

        Spacer(modifier = Modifier.height(16.dp))

        SimpleInfo(
            title = "Average Weight",
            description = data.weight.metric
        )

        Spacer(modifier = Modifier.height(16.dp))

        SimpleInfo(
            title = "Rare",
            description = if (data.rare == 1) "Yes" else "No"
        )

        Spacer(modifier = Modifier.height(16.dp))

        SimpleInfo(
            title = "Life Span",
            description = data.lifeSpan
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column {
            StarRatingBar(text = "affectionLevel", rating = data.affectionLevel.toFloat()) {}
            Spacer(modifier = Modifier.height(16.dp))
            StarRatingBar(text = "childFriendly", rating = data.childFriendly.toFloat()) {}
            Spacer(modifier = Modifier.height(16.dp))
            StarRatingBar(text = "dogFriendly", rating = data.dogFriendly.toFloat()) {}
            Spacer(modifier = Modifier.height(16.dp))
            StarRatingBar(text = "energyLevel", rating = data.energyLevel.toFloat()) {}
            Spacer(modifier = Modifier.height(16.dp))
            StarRatingBar(text = "healthIssues", rating = data.healthIssues.toFloat()) {}
            Spacer(modifier = Modifier.height(16.dp))
        }

        val context = LocalContext.current

        Button(
            onClick =
            {
                context.startActivity(Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(data.wikipediaUrl ?: "")
                ))
            }
        ) {
            Text(text = "Wiki")
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
