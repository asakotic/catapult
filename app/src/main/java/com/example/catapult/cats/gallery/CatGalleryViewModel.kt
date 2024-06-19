package com.example.catapult.cats.gallery

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catapult.cats.gallery.ICatGalleryContract.CatGalleryState
import com.example.catapult.cats.db.CatsService
import com.example.catapult.di.DispatcherProvider
import com.example.catapult.navigation.catId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject


@HiltViewModel
class CatGalleryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dispatcherProvider: DispatcherProvider,
    private val catsService: CatsService
) : ViewModel() {

    private val catId: String = savedStateHandle.catId
    private val _catGalleryState = MutableStateFlow(CatGalleryState(catId = catId))
    val catGalleryState = _catGalleryState.asStateFlow()

    private fun setCatGalleryState (update: CatGalleryState.() -> CatGalleryState) =
        _catGalleryState.getAndUpdate(update)

    init {
        observeCatGallery()
    }

    private fun observeCatGallery() {

        viewModelScope.launch {
            setCatGalleryState { copy(isLoading = true) }
            try {
                withContext(dispatcherProvider.io()) {
                    catsService.getAllCatsPhotosApi(id = catId)
                }
                catsService.getAllCatImagesByIdFlow(id = catId).collect { photos ->
                    setCatGalleryState { copy(photos = photos, isLoading = false) }
                }
            }catch (error: IOException){
                setCatGalleryState { copy(error = CatGalleryState.DetailsError.DataUpdateFailed(cause = error)) }
            }finally {
                setCatGalleryState { copy(photos = photos, isLoading = false) }
            }

        }
    }

}
