package com.connie.weather.ui.weather.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.connie.domain.model.ViewState
import com.connie.ui.composable.shimmerSkeleton
import com.connie.weather.R
import com.connie.weather.ui.weather.CurrentWeatherState
import com.connie.weather.ui.weather.ForecastState
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

private val loadingDailyForecastWidths = listOf(
    96.dp,
    132.dp,
    88.dp,
    156.dp,
    104.dp,
)

fun LazyListScope.dailyForecastItems(viewState: ViewState<PersistentList<ForecastState>>) {
    when (viewState) {
        is ViewState.Loading -> {
            loadingDailyForecastWidths.forEach {
                item {
                    LoadingDailyForecastItem(it)
                }
            }
        }

        is ViewState.Success<*> -> {
            viewState.get().forEach {
                item {
                    DailyForecastItem(it)
                }
            }

        }

        else -> Unit
    }
}

@Composable
private fun DailyForecastItem(item: ForecastState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                modifier = Modifier.widthIn(min = 48.dp),
                text = item.displayTime,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            )
            AsyncImage(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                    .padding(4.dp),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.weather.iconUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = item.weather.main,
            )
            Text(
                text = item.weather.main,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(modifier = Modifier.size(24.dp))
        Text(
            text = stringResource(
                R.string.weather__max_and_min_temperature,
                item.weather.maxTemperature,
                item.weather.minTemperature,
            ),
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Composable
private fun LoadingDailyForecastItem(loadingWidth: Dp) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .height(36.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            modifier = Modifier
                .padding(end = 12.dp)
                .size(height = 16.dp, width = 36.dp)
                .shimmerSkeleton()
        )
        Box(
            modifier = Modifier
                .size(32.dp)
                .shimmerSkeleton(shape = CircleShape)
                .padding(4.dp)
        )
        Box(
            modifier = Modifier
                .size(height = 16.dp, width = loadingWidth)
                .shimmerSkeleton()
                .padding(end = 12.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewDailyForecast() {
    val forecasts = buildList {
        val weather = CurrentWeatherState(
            cityName = "Taipei",
            temperature = "30",
            feelsLike = "25",
            minTemperature = "21",
            maxTemperature = "32",
            humidity = "89",
            windSpeed = "10",
            main = "CloudsCloudsCloudsCloudsClouds",
            description = "overcast clouds",
            iconUrl = "",
        )
        repeat(5) {
            add(
                ForecastState(
                    displayTime = "Mon",
                    weather = weather,
                )
            )
            add(
                ForecastState(
                    displayTime = "Thur",
                    weather = weather,
                )
            )
        }
    }.toPersistentList()
    LazyColumn {
        dailyForecastItems(
            ViewState.Success(forecasts)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewLoadingDailyForecast() {
    LazyColumn {
        dailyForecastItems(
            ViewState.Loading
        )
    }
}