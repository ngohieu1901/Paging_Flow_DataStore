package com.hieunt.base.state

import android.util.Log
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update

class UiStateStore<T : Any>(
    initialState: T
) {
    private val _uiStateFlow = MutableStateFlow(initialState)
    val uiStateFlow get() = _uiStateFlow.asStateFlow()

    val currentUiState: T get() = _uiStateFlow.value

    suspend fun collect(collector: FlowCollector<T>) {
        Log.e("UiStateStore", "collectData: ${_uiStateFlow.value} ", )
        _uiStateFlow.collect(collector)
    }

    suspend fun collectLatest(action: suspend (uiState: T) -> Unit) {
        Log.e("UiStateStore", "collectData: ${{ _uiStateFlow.value }} ")
        _uiStateFlow.collectLatest(action)
    }

    fun updateStateUi(uiState: T) {
        Log.e("UiStateStore", "updateStateUi: $uiState", )
        _uiStateFlow.update { uiState }
    }
}