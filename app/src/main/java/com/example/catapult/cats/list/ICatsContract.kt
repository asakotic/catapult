package com.example.catapult.cats.list

import com.example.catapult.cats.domen.CatInfo

interface ICatsContract {
    data class CatsListState(
        val isLoading: Boolean = false,
        val cats: List<CatInfo> = emptyList(),
        val catsFiltered: List<CatInfo> = emptyList(),
        val isSearching: Boolean = false,
        val searchText: String = "",
        val error: DetailsError? = null
    ) {
        sealed class DetailsError {
            data class DataUpdateFailed(val cause: Throwable? = null): DetailsError()
        }
    }

    sealed class CatsListUIEvent {
        data class SearchQueryChanged(val query: String) : CatsListUIEvent()
    }
}