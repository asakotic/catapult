package com.example.catalist.cats.details

import com.example.catalist.cats.domen.CatInfo

interface ICatDetailsContract {

    data class CatDetailsState(
        val catId: String,
        val isLoading: Boolean = false,
        val data: CatInfo? = null,
        val error: DetailsError? = null
    ) {
        sealed class DetailsError {
            data class DataUpdateFailed(val cause: Throwable? = null): DetailsError()
        }
    }
}