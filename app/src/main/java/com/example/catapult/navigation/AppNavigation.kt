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
import com.example.catapult.cats.quiz.leaderboard.leaderboardScreen
import com.example.catapult.cats.quiz.left_right_cat.quizLeftRightCat
import com.example.catapult.cats.quiz.result.resultScreen
import com.example.catapult.cats.user.loginScreen
import java.net.URLEncoder

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        catsListScreen(
            route = "cats",
            navController = navController,
            goToQuiz = {
                navController.navigate("quiz")
            }
        )

        loginScreen( //add-new-user
            route = "login?add-new-user={addNewUser}",
//            route = "login/{addNewUser}",
            arguments = listOf(navArgument("addNewUser"){
               defaultValue = false
               type = NavType.BoolType
            }),
            navController = navController,
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
            route = "quiz/guess-fact",
            navController = navController
        )
        guessCatScreen(
            route = "quiz/guess-cat",
            navController = navController
        )
        quizLeftRightCat(
            route = "quiz/left-right-cat",
            navController = navController
        )
        resultScreen(
            route = "quiz/result/{category}/{nickname}/{result}",
            navController = navController,
            arguments = listOf(
                navArgument("category") {
                type = NavType.IntType
            }, navArgument("nickname") {
                type = NavType.StringType
            }, navArgument("result") {
            type = NavType.FloatType
            }  )
        )
        leaderboardScreen(
            route = "quiz/leaderboard/{category}/{nickname}",
            navController = navController,
            arguments = listOf(
                navArgument("category") {
                    type = NavType.IntType
                }, navArgument("nickname") {
                    type = NavType.StringType
                }
            )
        )
    }

}

inline val SavedStateHandle.catId: String
    get() = checkNotNull(get("id")) {"catId is mandatory"}

inline val SavedStateHandle.photoIndex: Int
    get() = checkNotNull(get("photoIndex")) {"photoIndex is mandatory"}
inline val SavedStateHandle.category: Int
    get() = checkNotNull(get("category")) {"category is mandatory"}
inline val SavedStateHandle.nickname: String
    get() = checkNotNull(get("nickname")) {"nickname is mandatory"}
inline val SavedStateHandle.result: Float
    get() = checkNotNull(get("result")) {"result is mandatory"}

inline val SavedStateHandle.addNewUser: Boolean
    get() = get("addNewUser") ?: false

