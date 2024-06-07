package com.example.catapult.cats.gallery

import com.example.catapult.cats.db.CatGallery

interface ICatGalleryContract {
    data class CatGalleryState(
        val isLoading: Boolean = false,
        val photos: List<String> = emptyList(),
        val error: ICatGalleryContract.CatGalleryState.DetailsError? = null
    ) {
        sealed class DetailsError {
            data class DataUpdateFailed(val cause: Throwable? = null): DetailsError()
        }
    }

    sealed class CatListUIEvent {
        data class SearchQueryChanged(val query: String) : CatListUIEvent()
    }

}