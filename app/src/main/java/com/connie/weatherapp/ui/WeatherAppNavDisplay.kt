package com.connie.weatherapp.ui

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.connie.city.ui.SearchCityScreen
import com.connie.city.ui.SearchNavKey
import com.connie.weather.ui.weather.WeatherNavKey
import com.connie.weather.ui.weather.WeatherScreen
import com.connie.weather.ui.weather.WeatherViewModel
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import kotlinx.coroutines.launch

@Composable
fun WeatherAppNavDisplay() {
    val backStack = remember { mutableStateListOf<NavKey>(WeatherNavKey()) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    WeatherModalNavDrawer(
        drawerState = drawerState,
        onEvent = {
            when (it) {
                is DrawerUiEvent.Navigate -> {
                    backStack.navigateToSingleTask(it.key)
                }
            }
        }
    ) {
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = { key ->
                when (key) {
                    is WeatherNavKey -> NavEntry(key) {
                        WeatherScreen(
                            weatherViewModel = hiltViewModel<WeatherViewModel, WeatherViewModel.Factory>(
                                creationCallback = { it.create(key) },
                            ),
                            onOpenDrawer = {
                                scope.launch { drawerState.open() }
                            }
                        )
                    }

                    is SearchNavKey -> NavEntry(key) {
                        SearchCityScreen(
                            onBackClick = {
                                backStack.removeLastOrNull()
                            },
                            openWeatherDetail = {
                                backStack.navigateToSingleTask(WeatherNavKey(it))
                            },
                        )
                    }

                    else -> error("Unknown nav key: $key")
                }
            }
        )
    }
}

fun SnapshotStateList<NavKey>.navigateToSingleTask(key: NavKey) {
    val targetIndex = indexOfLast { it::class == key::class }

    when (targetIndex) {
        -1 -> {
            add(key)
        }

        lastIndex -> {
            removeAt(lastIndex)
            add(key)
        }

        else -> {
            while (lastIndex > targetIndex) {
                removeAt(lastIndex)
            }
            removeAt(lastIndex)
            add(key)
        }
    }
}