package com.example.catapult.cats.gallery.photo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catapult.cats.gallery.photo.ICatPhotoContract.CatPhotoState
import com.example.catapult.cats.db.CatsService
import com.example.catapult.di.DispatcherProvider
import com.example.catapult.navigation.catId
import com.example.catapult.navigation.photoIndex
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject


@HiltViewModel
class CatPhotoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dispatcherProvider: DispatcherProvider,
    private val catsService: CatsService
) : ViewModel() {

    private val catId: String = savedStateHandle.catId
    private val photoIndex: Int = savedStateHandle.photoIndex
    private val _catPhotoState = MutableStateFlow(CatPhotoState(photoIndex = photoIndex))
    val catPhotoState = _catPhotoState.asStateFlow()

    private fun setCatPhotoState (update: CatPhotoState.() -> CatPhotoState) =
        _catPhotoState.getAndUpdate(update)

    init {
        observeCatPhoto()
    }

    private fun observeCatPhoto() {

        viewModelScope.launch {
            setCatPhotoState { copy(isLoading = true) }
            try {
                catsService.getAllCatImagesByIdFlow(id = catId).collect { photos ->
                    setCatPhotoState { copy(photos = photos, isLoading = false) }
                }
            }catch (error: IOException){
                setCatPhotoState { copy(error = CatPhotoState.DetailsError.DataUpdateFailed(cause = error)) }
            }finally {
                setCatPhotoState { copy(photos = photos, isLoading = false) }
            }

        }
    }

}
