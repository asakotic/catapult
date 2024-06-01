package com.example.catapult.cats.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.catapult.cats.details.ICatDetailsContract.CatDetailsState
import com.example.catapult.cats.repository.CatsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class CatDetailsViewModel(
    private val catId: String,
    private val catsRepository: CatsRepository = CatsRepository
) : ViewModel() {

    private val _catDetailsState = MutableStateFlow(CatDetailsState(catId = catId))
    val catDetailsState = _catDetailsState.asStateFlow()

    private fun setCatDetailsState (update: CatDetailsState.() -> CatDetailsState) =
        _catDetailsState.getAndUpdate(update)

    init {
        observeCatDetails()
        fetchCatDetailsRepo()
    }

    private fun observeCatDetails() {
        viewModelScope.launch {
            catsRepository.observeCat(id = catId).collect { catInfoDetail ->
                setCatDetailsState { copy(data = catInfoDetail) }
            }
        }
    }

    private fun fetchCatDetailsRepo() {
        viewModelScope.launch {
            setCatDetailsState { copy(isLoading = true) }
            try {
                //delay(1.seconds)
                withContext(Dispatchers.IO) {
                    catsRepository.getCatById(_catDetailsState.value.catId)
                }
            }
            catch (error: IOException) {
                setCatDetailsState { copy(error = CatDetailsState.DetailsError.DataUpdateFailed(cause = error)) }
            }
            finally {
                setCatDetailsState { copy(isLoading = false) }
            }
        }
    }

}

class CatDetailsViewModelFactory(
    private val catId: String
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return CatDetailsViewModel(catId = catId) as T
    }
}
