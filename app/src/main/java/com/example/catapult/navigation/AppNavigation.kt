package com.example.catapult.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.catapult.cats.details.catDetailsScreen
import com.example.catapult.cats.gallery.catGalleryScreen
import com.example.catapult.cats.gallery.photo.catPhotoScreen
import com.example.catapult.cats.list.catsListScreen
import com.example.catapult.cats.quiz.chooseQuizScreen
import com.example.catapult.cats.quiz.guess_cat.guessCatScreen
import com.example.catapult.cats.quiz.guess_fact.guessFactScreen
import com.example.catapult.cats.quiz.left_right_cat.quizLeftRightCat
import java.net.URLEncoder

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "cats"
    ) {

        catsListScreen(
            route = "cats",
            navController = navController,
            goToQuiz = {
                navController.navigate("quiz")
            }
        )

        catDetailsScreen(
            route = "cats/{id}",
            navController = navController,
            arguments = listOf(navArgument("id") {
                type = NavType.StringType
            })
        )

        catGalleryScreen(
            route = "images/{id}",
            navController = navController,
            arguments = listOf(navArgument("id") {
                type = NavType.StringType
            }),
            onPhotoClicked = {id,photoIndex->
                navController.navigate(route = "photo/${id}/${photoIndex}")
            }
        )
        catPhotoScreen(
            route = "photo/{id}/{photoIndex}",
            navController = navController,
            arguments = listOf(navArgument("id") {
                type = NavType.StringType
            }, navArgument("photoIndex") {
                type = NavType.IntType
            }),
        )
        chooseQuizScreen(
            route = "quiz",
            navController = navController
        )
        guessFactScreen(
            route = "quiz/guessFact",
        )
        quizLeftRightCat(
            route = "quiz/left-right-cat",
            navController = navController
        )
    }

}

inline val SavedStateHandle.catId: String
    get() = checkNotNull(get("id")) {"catId is mandatory"}

inline val SavedStateHandle.photoIndex: Int
    get() = checkNotNull(get("photoIndex")) {"photoIndex is mandatory"}

