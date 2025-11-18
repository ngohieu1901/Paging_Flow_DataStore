package com.hieunt.base.presentations.feature.screen_base.uninstall

import com.hieunt.base.domain.model.AnswerModel

data class UninstallUiState (
    var listAnswer: List<AnswerModel> = emptyList(),
)