package com.connie.weather.mapper

import kotlin.math.roundToInt

fun Double.toDegree(): String {
    return "${this.roundToInt()}\u00B0"
}