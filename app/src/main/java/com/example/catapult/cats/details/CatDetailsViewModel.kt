package com.example.catapult.cats.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catapult.cats.db.CatsService
import com.example.catapult.cats.details.ICatDetailsContract.CatDetailsState
import com.example.catapult.di.DispatcherProvider
import com.example.catapult.navigation.catId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CatDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dispatcherProvider: DispatcherProvider,
    private val catsService: CatsService
) : ViewModel() {

    private val catId: String = savedStateHandle.catId
    private val _catDetailsState = MutableStateFlow(CatDetailsState(catId = catId))
    val catDetailsState = _catDetailsState.asStateFlow()

    private fun setCatDetailsState (update: CatDetailsState.() -> CatDetailsState) =
        _catDetailsState.getAndUpdate(update)

    init {
        observeCatDetails()
    }

    private fun observeCatDetails() {
        viewModelScope.launch {
            setCatDetailsState { copy(isLoading = true) }
            catsService.getCatByIdFlow(id = catId).collect { catInfoDetail ->
                setCatDetailsState { copy(data = catInfoDetail, isLoading = false) }
            }
        }
    }

}
