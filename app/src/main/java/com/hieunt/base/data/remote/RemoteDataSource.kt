package com.hieunt.base.data.remote

import com.hieunt.base.base.network.BaseRemoteService
import com.hieunt.base.base.network.NetworkResult
import com.hieunt.base.data.apis.AppApi
import com.hieunt.base.data.database.entities.AppModel
import dagger.Lazy
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteDataSource @Inject constructor(private val appApi: Lazy<AppApi>): BaseRemoteService() {
    suspend fun getAllData(): NetworkResult<List<AppModel>> {
        return callApi { appApi.get().getAllData() }
    }
}