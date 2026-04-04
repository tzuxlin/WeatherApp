package com.connie.domain.model

sealed interface ViewState<out T> {
    data object Loading : ViewState<Nothing>
    data class Success<T>(val data: T) : ViewState<T>
    data class Error(val message: String = "") : ViewState<Nothing>

    fun get(): T = (this as Success).data
    fun getOrNull(): T? = (this as? Success)?.data
}