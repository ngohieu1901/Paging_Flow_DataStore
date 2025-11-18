package com.hieunt.base.presentations.feature.screen_base.language_start_new

import com.hieunt.base.domain.model.LanguageParentModel

data class LanguageStartNewUiState(
    val listLanguage: List<LanguageParentModel> = emptyList()
)