package com.hieunt.base.presentations.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.hieunt.base.domain.repository.FixtureRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val fixtureRepository: FixtureRepository
): ViewModel() {
    val fixtureStream = fixtureRepository.getAllFixture().cachedIn(viewModelScope)
}