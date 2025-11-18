package com.hieunt.base.data.result

sealed interface DataResult<out T: Any> {
    data class Success<out T : Any>(val data: T) : DataResult<T>
    data class Error(val exception: Exception) : DataResult<Nothing>
}
