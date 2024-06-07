package com.example.catapult.cats.gallery.photo

interface ICatPhotoContract {
    data class CatPhotoState(
        val isLoading: Boolean = false,
        val photos: List<String> = emptyList(),
        val photoIndex: Int = 0,
        val error: DetailsError? = null
    ) {
        sealed class DetailsError {
            data class DataUpdateFailed(val cause: Throwable? = null): DetailsError()
        }
    }
}