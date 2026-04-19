package com.connie.city.ui

import com.connie.domain.model.City
import com.connie.domain.model.ViewState
import kotlinx.collections.immutable.PersistentList

data class SearchCityUiState(
    val query: String = "",
    val searchResults: ViewState<PersistentList<CityUiState>> = ViewState.Loading,
)

data class CityUiState(
    val key: String,
    val city: City,
)

sealed interface SearchCityEvent {
    data class OnQueryChange(val query: String): SearchCityEvent
}