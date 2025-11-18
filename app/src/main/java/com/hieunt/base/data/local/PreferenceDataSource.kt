package com.hieunt.base.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import jakarta.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

}