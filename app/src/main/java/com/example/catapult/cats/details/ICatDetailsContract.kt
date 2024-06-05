package com.example.catapult.cats.details

import com.example.catapult.cats.db.Cat

interface ICatDetailsContract {

    data class CatDetailsState(
        val catId: String,
        val isLoading: Boolean = false,
        val data: Cat? = null,
        val error: DetailsError? = null
    ) {
        sealed class DetailsError {
            data class DataUpdateFailed(val cause: Throwable? = null): DetailsError()
        }
    }
}