package com.connie.weather.ui.weather.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.connie.domain.model.ViewState
import com.connie.ui.composable.HorizontalFadingEdges
import com.connie.ui.composable.rememberFadeVisibility
import com.connie.ui.composable.shimmerSkeleton
import com.connie.weather.ui.weather.CurrentWeatherState
import com.connie.weather.ui.weather.ForecastState
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

@Composable
fun HourlyForecast(viewState: ViewState<PersistentList<ForecastState>>) {
    if (viewState is ViewState.Error) return

    val lazyListState = rememberLazyListState()

    HorizontalFadingEdges(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(color = MaterialTheme.colorScheme.surfaceContainer),
        fadeVisibility = rememberFadeVisibility(lazyListState),
        fadeColor = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        LazyRow(
            modifier = Modifier.matchParentSize(),
            state = lazyListState,
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            contentPadding = PaddingValues(horizontal = 24.dp),
        ) {
            when (viewState) {
                ViewState.Loading -> {
                    repeat(6) {
                        item {
                            LoadingHourlyForecastItem()
                        }
                    }
                }

                is ViewState.Success<*> -> {
                    viewState.get().forEachIndexed { index, state ->
                        item(index) {
                            HourlyForecastItem(state)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingHourlyForecastItem() {
    Column(
        modifier = Modifier
            .sizeIn(minWidth = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(height = 16.dp, width = 40.dp)
                .shimmerSkeleton(true)
        )
        Box(
            modifier = Modifier
                .padding(2.dp)
                .size(36.dp)
                .clip(CircleShape)
                .shimmerSkeleton(true)
                .padding(4.dp),
        )
        Box(
            modifier = Modifier
                .size(height = 16.dp, width = 24.dp)
                .shimmerSkeleton(true)
        )
    }
}

@Composable
private fun HourlyForecastItem(item: ForecastState) {
    Column(
        modifier = Modifier
            .sizeIn(minWidth = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = item.displayTime,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
        )
        AsyncImage(
            modifier = Modifier
                .padding(2.dp)
                .size(36.dp)
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
            text = item.weather.temperature,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewHourlyForecast() {
    val forecast = ForecastState(
        displayTime = "24:00",
        weather = CurrentWeatherState(
            cityName = "Taipei",
            temperature = "30\u00B0",
        )
    )
    val forecasts = buildList { repeat(10) { add(forecast) } }.toPersistentList()
    HourlyForecast(
        ViewState.Success(forecasts)
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewLoadingHourlyForecast() {
    HourlyForecast(ViewState.Loading)
}