package com.example.catapult

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.catapult.analytics.AppAnalytics
import com.example.catapult.core.theme.CatapultTheme
import com.example.catapult.navigation.AppNavigation
import com.example.catapult.users.User
import com.example.catapult.users.UsersDataStore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val analytics = AppAnalytics()
    @Inject
    lateinit var usersDataStore: UsersDataStore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        var user = User.EMPTY
        if (usersDataStore.data.value.pick > -1)
            user = usersDataStore.data.value.users[usersDataStore.data.value.pick]
        setContent {
            CatapultTheme(darkTheme = user.darkTheme) {
                AppNavigation()
            }
        }
    }
}

val LocalAnalytics = compositionLocalOf<AppAnalytics> {
    error("Analytics not provided")
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CatapultTheme {
        Greeting("Android")
    }
}