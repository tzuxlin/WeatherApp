package com.connie.city.ui

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.connie.domain.model.City
import com.connie.domain.model.ViewState
import com.connie.ui.R
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

@Serializable
data object SearchNavKey : NavKey

@Composable
fun SearchCityScreen(
    onBackClick: () -> Unit,
    openWeatherDetail: (City) -> Unit,
    viewModel: SearchCityViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SearchCityScreenContent(
        query = uiState.query,
        searchResult = uiState.searchResults,
        onBackClick = onBackClick,
        onQueryChange = { viewModel.onEvent(SearchCityEvent.OnQueryChange(it)) },
        onSearch = {},
        onCityClick = {
            openWeatherDetail(it)
        },
        modifier = Modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchCityScreenContent(
    query: String,
    searchResult: ViewState<PersistentList<City>>,
    onBackClick: () -> Unit,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onCityClick: (City) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    CitySearchBar(
                        query = query,
                        onQueryChange = onQueryChange,
                        onSearch = onSearch
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        }
    ) { innerPadding ->
        SearchResultContent(
            searchResults = searchResult,
            onCityClick = onCityClick,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CitySearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    SearchBarDefaults.InputField(
        query = query,
        onQueryChange = {
            onQueryChange(it)
        },
        onSearch = {
            onSearch(it)
        },
        expanded = false,
        onExpandedChange = {},
        placeholder = {
            Text(stringResource(com.connie.city.R.string.city__search_for_a_city))
        },
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = null
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
    )
}

@Composable
private fun SearchResultContent(
    searchResults: ViewState<PersistentList<City>>,
    onCityClick: (City) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        when {
            searchResults is ViewState.Success && searchResults.data.isNotEmpty() -> {
                CityList(
                    cities = searchResults.data,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_location),
                            contentDescription = null
                        )
                    },
                    onCityClick = onCityClick
                )
            }

            else -> {
                EmptySearchState()
            }
        }
    }

}

@Composable
private fun CityList(
    cities: PersistentList<City>,
    leadingIcon: @Composable () -> Unit,
    onCityClick: (City) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        cities.forEach { city ->
            CityListItem(
                city = city,
                leadingIcon = leadingIcon,
                onClick = { onCityClick(city) }
            )
        }
    }
}

@Composable
private fun CityListItem(
    city: City,
    leadingIcon: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        modifier = modifier.clickable(onClick = onClick),
        headlineContent = {
            Text(text = city.name)
        },
        supportingContent = {
            val subtitleText = buildString {
                append(city.country)
            }
            Text(text = subtitleText)
        },
        leadingContent = leadingIcon,
    )
}

@Composable
private fun EmptySearchState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = null,
            )
            Text(
                text = stringResource(com.connie.city.R.string.city__search_for_a_city),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(com.connie.city.R.string.city__find_the_weather_for),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchCityScreenContentResultPreview() {
    SearchCityScreenContent(
        query = "to",
        searchResult = ViewState.Success(
            persistentListOf(
                City("Taipei", "TW", 0.0, 0.0),
            )
        ),
        onBackClick = {},
        onQueryChange = {},
        onSearch = {},
        onCityClick = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun SearchCityScreenContentEmptyPreview() {
    SearchCityScreenContent(
        query = "zzzz",
        searchResult = ViewState.Success(persistentListOf()),
        onBackClick = {},
        onQueryChange = {},
        onSearch = {},
        onCityClick = {}
    )
}