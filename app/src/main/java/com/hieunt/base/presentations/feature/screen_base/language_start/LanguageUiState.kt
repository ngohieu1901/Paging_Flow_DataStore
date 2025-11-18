package com.hieunt.base.presentations.feature.screen_base.language_start

import com.hieunt.base.domain.model.LanguageModelNew

sealed interface LanguageUiState {
    data object Idle : LanguageUiState
    data object Loading : LanguageUiState
    data class Error(val e: Throwable): LanguageUiState
    data class Language(
        val listLanguage: List<LanguageModelNew> = emptyList(),
    ) : LanguageUiState
}