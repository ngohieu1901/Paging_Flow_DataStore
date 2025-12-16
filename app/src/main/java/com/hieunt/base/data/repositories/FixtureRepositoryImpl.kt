package com.hieunt.base.data.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.hieunt.base.data.apis.AppApi
import com.hieunt.base.data.remote.FixturePagingSource
import com.hieunt.base.domain.model.FixtureDomain
import com.hieunt.base.domain.repository.FixtureRepository
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FixtureRepositoryImpl @Inject constructor(
    private val appApi: AppApi,
): FixtureRepository {
    override fun getAllFixture(): Flow<PagingData<FixtureDomain>> {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = sdf.format(Calendar.getInstance().time)

        return Pager(
            config = PagingConfig(
                pageSize = 30,
                initialLoadSize = 30,
                prefetchDistance = 1,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                FixturePagingSource(appApi = appApi, date = date)
            }
        ).flow
    }
}