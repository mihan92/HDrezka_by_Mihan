package com.mihan.movie.library.common

sealed interface ApiResponse<out T> {
    data object Loading: ApiResponse<Nothing>
    data class Success<T>(val data: T): ApiResponse<T>
    data class Error(val errorMessage: String): ApiResponse<Nothing>
}
