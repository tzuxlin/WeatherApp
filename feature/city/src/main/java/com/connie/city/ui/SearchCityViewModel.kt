package com.connie.city.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.connie.domain.model.City
import com.connie.domain.model.ViewState
import com.connie.domain.repository.GeoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchCityViewModel @Inject constructor(
    private val geoRepository: GeoRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchCityUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeQueryChanges()
    }

    fun onEvent(event: SearchCityEvent) {
        when (event) {
            is SearchCityEvent.OnQueryChange -> {
                _uiState.update {
                    it.copy(query = event.query)
                }
            }
        }
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun observeQueryChanges() {
        viewModelScope.launch {
            uiState
                .map { it.query.trim() }
                .debounce(800)
                .filter { it.isNotEmpty() }
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    geoRepository.getDirectGeocoding(query)
                }
                .map { cities ->
                    cities.map(::buildCityUiState).toPersistentList()
                }
                .collectLatest { result ->
                    _uiState.update {
                        it.copy(searchResults = ViewState.Success(result))
                    }
                }
        }
    }

    private fun buildCityUiState(city: City): CityUiState =
        CityUiState(
            key = city.name + city.lat.toString() + city.lon.toString(),
            city = city,
        )

}