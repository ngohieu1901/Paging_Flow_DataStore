package com.hieunt.base.data.repositories

import com.hieunt.base.BuildConfig
import com.hieunt.base.data.dto.toFixtureDomain
import com.hieunt.base.data.local.LocalDataSource
import com.hieunt.base.data.remote.FixtureRemoteDataSource
import com.hieunt.base.di.IoDispatcher
import com.hieunt.base.domain.model.FixtureDomain
import com.hieunt.base.domain.repository.FixtureRepository
import com.hieunt.base.widget.runSuspendCatching
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FixtureRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val leagueAndFixtureRemoteDataSource: FixtureRemoteDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): FixtureRepository {
    override suspend fun getAllFixtureByDate(
        date: String,
        timezone: String,
        include: String,
        perPage: Int,
        page: Int
    ): Result<List<FixtureDomain>> = runSuspendCatching(ioDispatcher) {
        leagueAndFixtureRemoteDataSource.getFixturesByDate(
            date = date,
            apiToken = BuildConfig.API_KEY,
            timezone = timezone,
            include = include,
            perPage = perPage,
            page = page
        ).data.map {
            it.toFixtureDomain()
        }
    }
}