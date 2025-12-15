package com.hieunt.base.data.remote

import com.hieunt.base.data.apis.AppApi
import com.hieunt.base.data.dto.DataFixtures
import dagger.Lazy
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FixtureRemoteDataSource @Inject constructor(private val appApi: Lazy<AppApi>) {
    suspend fun getFixturesByDate(
        date: String,
        apiToken: String,
        timezone: String,
        include: String,
        perPage: Int,
        page: Int
    ): DataFixtures = appApi.get().getFixturesByDate(
        date = date,
        apiToken = apiToken,
        timezone = timezone,
        include = include,
        perPage = perPage,
        page = page
    )
}