package com.example.catapult.cats.gallery

interface ICatGalleryContract {
    data class CatGalleryState(
        val isLoading: Boolean = false,
        val photos: List<String> = emptyList(),
        val error: DetailsError? = null,
        val catId: String
    ) {
        sealed class DetailsError {
            data class DataUpdateFailed(val cause: Throwable? = null): DetailsError()
        }
    }
}