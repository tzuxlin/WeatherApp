package com.connie.weather.ui.saved

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.connie.domain.model.City
import com.connie.domain.model.ViewState
import com.connie.ui.composable.shimmerSkeleton
import com.connie.weather.R

@Composable
fun SavedLocationsWeatherSection(
    onClickCity: (City) -> Unit,
    viewModel: SavedLocationsWeatherViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SavedLocationsWeatherSectionContent(uiState, onClickCity)
}

@Composable
private fun SavedLocationsWeatherSectionContent(
    uiState: SavedLocationsWeatherUiState,
    onClickCity: (City) -> Unit,
) {
    Column(
        modifier = Modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        when (val favorites = uiState.weathers) {
            is ViewState.Loading -> {
                repeat(1) {
                    WeatherPreviewLoadingItem()
                }
            }

            is ViewState.Success -> {
                favorites.data.forEach {
                    WeatherPreviewItem(it, onClickCity)
                }
            }

            is ViewState.Error -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        modifier = Modifier.size(16.dp),
                        painter = painterResource(com.connie.ui.R.drawable.ic_warning),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        text = favorites.message,
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.secondary)
                    )
                }
            }
        }
    }
}

@Composable
private fun WeatherPreviewItem(
    weather: CityWeatherState,
    onClickCity: (City) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(16.dp)
            .clickable { onClickCity(weather.city) }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier.weight(1f),
                text = weather.city.name,
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = weather.temperature,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            )
        }
        Row {
            Text(
                modifier = Modifier.weight(1f),
                text = weather.main,
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = stringResource(
                    R.string.weather__max_and_min_temperature,
                    weather.maxTemperature,
                    weather.minTemperature
                ),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun WeatherPreviewLoadingItem() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(84.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .shimmerSkeleton(enabled = true)
    )
}

@Preview(widthDp = 300, showBackground = true)
@Composable
private fun PreviewWeatherPreviewSection(
    @PreviewParameter(SavedLocationsWeatherPreviewParameterProvider::class)
    uiState: SavedLocationsWeatherUiState,
) {
    MaterialTheme {
        SavedLocationsWeatherSectionContent(uiState) {}
    }
}