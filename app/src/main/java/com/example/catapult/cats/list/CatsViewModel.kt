package com.example.catapult.cats.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catapult.cats.db.CatsService
import com.example.catapult.cats.list.ICatsContract.CatsListState
import com.example.catapult.cats.list.ICatsContract.CatsListUIEvent
import com.example.catapult.di.DispatcherProvider
import com.example.catapult.users.User
import com.example.catapult.users.UsersDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.internal.toImmutableList
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class CatsViewModel @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val catsService: CatsService,
    private val usersDataStore: UsersDataStore
) : ViewModel() {

    private val _catsState = MutableStateFlow(CatsListState(
        usersData =  usersDataStore.data.value,
        darkTheme = usersDataStore.data.value.users[usersDataStore.data.value.pick].darkTheme
    ))
    val catsState = _catsState.asStateFlow()

    private val _catsEvents = MutableSharedFlow<CatsListUIEvent>()
    fun setCatsEvent(event: CatsListUIEvent) = viewModelScope.launch { _catsEvents.emit(event) }

    //every time _catsState changes catsState automatically changes
    //catsState.asStateFlow().collect() - is to react to does automatically changes to catState, if you want do to something extra when that happens
    //like when repository cats.asStateFlow() changes i want to _catsState to change so i set collect to repository cats.asStateFlow().collect

    //when "emit" is called then "events.collect" is called(technically its subscribed to emit) and check what "event" in "emit.(event)" it its
    //so "event" becomes "it" in "events.collect". CHECK RMA6 PROJECT FOR MORE DETAILS

    init {
        observeRepoCats()
        fetchAllCats()
        observeEvents()
    }

    fun changeMainUser(pick: Int) {
        CoroutineScope(dispatcherProvider.io()).launch {
            val usersData = usersDataStore.changeMainUser(newPick = pick)
            setCatsState { copy(usersData = usersData) }
        }
    }

    private fun observeEvents() {
        viewModelScope.launch {
            _catsEvents.collect { catsListUIEvent ->
                when (catsListUIEvent) {
                    is CatsListUIEvent.SearchQueryChanged -> searchQueryFilter(catsListUIEvent.query)
                    is CatsListUIEvent.ChangeTheme -> changeTheme(catsListUIEvent.bool)
                    is CatsListUIEvent.Logout -> logout(catsListUIEvent.user)
                }
            }
        }
    }

    private fun logout(user: User) {
        viewModelScope.launch {
            usersDataStore.removeUser(user)
            setCatsState { copy(usersData = usersDataStore.data.value) }
        }
    }

    private fun changeTheme(bool: Boolean) {
        val users = usersDataStore.data.value.users.toMutableList()
        val pick = usersDataStore.data.value.pick

        users[pick] = users[pick].copy(
            darkTheme = bool
        )

        viewModelScope.launch {
            usersDataStore.updateUser(users.toImmutableList())
            setCatsState { copy(darkTheme = bool) }
        }
    }

    /**
     * Updates list of cats
     */
    private fun setCatsState(updateWith: CatsListState.() -> CatsListState) =
        _catsState.getAndUpdate(updateWith)

    /**
     * Gets new list of cats from repository, triggers recomposition
     */
    private fun fetchAllCats() {
        viewModelScope.launch {

            setCatsState { copy(isLoading = true) }
            try {
                withContext(dispatcherProvider.io()) {
                    catsService.fetchAllCatsFromApi()
                }
            } catch (error: IOException) {
                setCatsState { copy(error = CatsListState.DetailsError.DataUpdateFailed(cause = error)) }
            } finally {
                setCatsState { copy(isLoading = false) }
            }
        }
    }

    /**
     * Listens for any changes to list of cats in repository with collect
     * and calls setState if any changes are made
     */
    private fun observeRepoCats() {
        viewModelScope.launch {
            setCatsState { copy(isLoading = true) }
            catsService.getAllCatsFlow().collect { newCatsList ->
                setCatsState { copy(cats = newCatsList, isLoading = false) }
                searchQueryFilter(_catsState.value.searchText)
            }
        }
    }

    private fun searchQueryFilter(query: String) {
        viewModelScope.launch {
            setCatsState {
                copy(
                    catsFiltered =
                    if (query.isBlank())
                        cats
                    else
                        cats.filter { catInfoDetails -> catInfoDetails.doesMatchSearchQuery(query) },
                    searchText = query
                )
            }
        }
    }
}