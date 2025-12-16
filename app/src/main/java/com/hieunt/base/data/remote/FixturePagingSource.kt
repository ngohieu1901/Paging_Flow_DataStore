package com.hieunt.base.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.hieunt.base.BuildConfig
import com.hieunt.base.data.apis.AppApi
import com.hieunt.base.data.dto.FixtureResponse
import com.hieunt.base.data.dto.toFixtureDomain
import com.hieunt.base.domain.model.FixtureDomain

class FixturePagingSource(
    private val appApi: AppApi,
    private val date: String
): PagingSource<Int, FixtureDomain>() {
    override fun getRefreshKey(state: PagingState<Int, FixtureDomain>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FixtureDomain> {
        return try {
            val position = params.key ?: 1
            val response: List<FixtureResponse> = appApi.getFixturesByDate(
                date = date,
                timezone = "Asia/Ho_Chi_Minh",
                include = "league;participants;scores;periods;state",
                perPage = params.loadSize,
                page = position,
                apiToken = BuildConfig.API_KEY
            ).data ?: emptyList()

            val domainData = response.map { networkModel -> networkModel.toFixtureDomain() }

            LoadResult.Page(
                data = domainData,
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (domainData.isEmpty() || domainData.size < params.loadSize) null else position + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}