package com.stylify.rbx.clothes.skincreator.widget

import kotlinx.coroutines.flow.SharingStarted

private const val STOP_TIMEOUT_MILLIS: Long = 5000
val WHILE_UI_SUBSCRIBED: SharingStarted = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS)