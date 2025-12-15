package com.hieunt.base.presentations.feature.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hieunt.base.domain.repository.FixtureRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val fixtureRepository: FixtureRepository
): ViewModel() {
    fun getAllFixtureByDate() {
        viewModelScope.launch {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val date = sdf.format(Calendar.getInstance().time)

            fixtureRepository.getAllFixtureByDate(
                date = date,
                timezone = "Asia/Ho_Chi_Minh",
                include = "league;participants;scores;periods;state",
                perPage = 20,
                page = 1
            ).fold(
                onSuccess = {
                    it.forEach { fixture ->
                        Log.d("getAllFixtureByDate", "fixture: $fixture")
                    }
                },
                onFailure = {
                    Log.d("getAllFixtureByDate", "error: $it")
                }
            )
        }
    }
}