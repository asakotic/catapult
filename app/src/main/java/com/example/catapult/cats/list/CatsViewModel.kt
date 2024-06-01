package com.example.catapult.cats.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catapult.cats.list.ICatsContract.CatsListState
import com.example.catapult.cats.list.ICatsContract.CatsListUIEvent
import com.example.catapult.cats.repository.CatsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class CatsViewModel(
    private val catsRepository: CatsRepository = CatsRepository
) : ViewModel() {

    private val _catsState = MutableStateFlow(CatsListState())
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
        fetchCats()
        observeEvents()
    }

    private fun observeEvents() {
        viewModelScope.launch {
            _catsEvents.collect { catsListUIEvent ->
                when (catsListUIEvent) {
                    is CatsListUIEvent.SearchQueryChanged -> searchQueryFilter(catsListUIEvent.query)
                }
            }
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
    private fun fetchCats() {
        viewModelScope.launch {

            setCatsState { copy(isLoading = true) }
            try {
                withContext(Dispatchers.IO) {
                    //delay(1.seconds)
                    catsRepository.fetchAllCats()
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
            catsRepository.observeCats().collect { newCatsList ->
                setCatsState { copy(cats = newCatsList) }
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