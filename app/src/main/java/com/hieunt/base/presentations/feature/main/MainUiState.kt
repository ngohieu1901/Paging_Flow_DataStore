package com.hieunt.base.presentations.feature.main

sealed interface MainUiState {
    data object Idle : MainUiState
    data object Loading : MainUiState
    data class Success(val isEmpty: Boolean) : MainUiState
    data class Error(val message: String?) : MainUiState
}