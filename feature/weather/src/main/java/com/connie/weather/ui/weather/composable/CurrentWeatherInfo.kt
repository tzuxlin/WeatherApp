package com.connie.weather.ui.weather.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.connie.domain.model.ViewState
import com.connie.weather.R
import com.connie.weather.ui.weather.CurrentWeatherState

@Composable
fun CurrentWeatherInfo(
    weatherViewState: ViewState<CurrentWeatherState>,
    modifier: Modifier = Modifier
) {
    val weather = weatherViewState.getOrNull()

    Column(
        modifier = modifier.height(400.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        if (weather == null) {
            EmptyCurrentWeatherInfo()
        } else {
            AsyncImage(
                modifier = Modifier
                    .size(108.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                    .padding(4.dp),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(weather.iconUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = weather.main,
            )
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                text = weather.cityName,
                style = MaterialTheme.typography.displaySmall,
            )
            Text(
                text = weather.temperature,
                style = MaterialTheme.typography.displayLarge,
            )
            Text(
                text = weather.description,
                style = MaterialTheme.typography.headlineSmall,
            )
            Spacer(modifier = Modifier.size(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = stringResource(
                        R.string.weather__max_and_min_temperature,
                        weather.maxTemperature,
                        weather.minTemperature,
                    ),
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.secondary),
                )
                Box(
                    modifier = Modifier
                        .size(3.dp)
                        .clip(CircleShape)
                        .background(color = MaterialTheme.colorScheme.secondary)
                )
                Text(
                    text = stringResource(R.string.weather__feel_like, weather.feelsLike),
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.secondary),
                )
            }
        }
    }
}

@Composable
private fun EmptyCurrentWeatherInfo() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "--",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.displaySmall.copy(color = MaterialTheme.colorScheme.secondary),
        )
    }
}

@Preview(showBackground = true, widthDp = 300)
@Composable
private fun PreviewCurrentWeatherInfo() {
    val currentWeatherState = CurrentWeatherState(
        cityName = "Taipei",
        main = "Sunny",
        description = "overcast clouds",
        temperature = "30\u00B0",
        maxTemperature = "32\u00B0",
        minTemperature = "25\u00B0",
        feelsLike = "28\u00B0",
    )

    MaterialTheme {
        CurrentWeatherInfo(ViewState.Success(currentWeatherState))
    }
}

@Preview(showBackground = true, widthDp = 300)
@Composable
private fun PreviewEmptyCurrentWeatherInfo() {
    MaterialTheme {
        CurrentWeatherInfo(ViewState.Loading)
    }
}