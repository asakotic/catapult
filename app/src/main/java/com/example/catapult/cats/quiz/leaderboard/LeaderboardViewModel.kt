package com.example.catapult.cats.quiz.leaderboard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catapult.cats.db.CatsService
import com.example.catapult.navigation.category
import com.example.catapult.users.UsersData
import com.example.catapult.users.UsersDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val catsService: CatsService,
    private val usersDataStore: UsersDataStore
) : ViewModel() {

    private val categ: Int = savedStateHandle.category
    private val _leaderboardState = MutableStateFlow(ILeaderboardContract.LeaderboardState())
    val leaderboardState = _leaderboardState.asStateFlow()

    private fun setLeaderboardState (update: ILeaderboardContract.LeaderboardState.() -> ILeaderboardContract.LeaderboardState) =
        _leaderboardState.getAndUpdate(update)

    init {
        observeCatPhoto()
    }

    private fun observeCatPhoto() {

        viewModelScope.launch {
            setLeaderboardState { copy(isLoading = true) }
            try {
                val list = catsService.fetchAllResultsForCategory(category = categ)
                setLeaderboardState { copy(results = list, nick = usersDataStore.data.value.users[usersDataStore.data.value.pick].nickname) }
            }catch (error: IOException){
                setLeaderboardState { copy(error = ILeaderboardContract.LeaderboardState.DetailsError.DataUpdateFailed(cause = error)) }
            }finally {
                setLeaderboardState { copy(isLoading = false) }
            }

        }
    }

}