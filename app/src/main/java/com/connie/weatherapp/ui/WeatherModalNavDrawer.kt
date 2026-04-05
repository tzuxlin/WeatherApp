package com.connie.weatherapp.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.connie.city.ui.SearchNavKey
import com.connie.ui.R
import com.connie.weather.ui.saved.SavedLocationsWeatherSection
import com.connie.weather.ui.weather.WeatherNavKey
import kotlinx.coroutines.launch

sealed interface DrawerUiEvent {
    data class Navigate(val key: NavKey): DrawerUiEvent
}

@Composable
fun WeatherModalNavDrawer(
    drawerState: DrawerState,
    onEvent: (DrawerUiEvent) -> Unit,
    content: @Composable () -> Unit,
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { WeatherDrawerContent(drawerState, onEvent) }
    ) {
        content()
    }
}

@Composable
private fun WeatherDrawerContent(
    drawerState: DrawerState,
    onEvent: (DrawerUiEvent) -> Unit,
) {
    val scope = rememberCoroutineScope()
    ModalDrawerSheet {
        DrawerSearchEntry(
            onClick = {
                onEvent(DrawerUiEvent.Navigate(SearchNavKey))
                scope.launch { drawerState.close() }
            }
        )

        DrawerSectionHeader(
            iconRes = R.drawable.ic_favorite_filled,
            stringRes = com.connie.weatherapp.R.string.drawer__saved,
        )
        SavedLocationsWeatherSection(onClickCity = {
            onEvent(DrawerUiEvent.Navigate(WeatherNavKey(it)))
            scope.launch { drawerState.close() }
        })

    }
}

@Composable
private fun DrawerSearchEntry(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(28.dp),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(com.connie.weatherapp.R.string.drawer__search_city),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DrawerSectionHeader(@DrawableRes iconRes: Int, @StringRes stringRes: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 8.dp, start = 8.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(16.dp),
            painter = painterResource(iconRes),
            contentDescription = null,
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = stringResource(stringRes),
            style = MaterialTheme.typography.titleMedium,
        )
    }
}