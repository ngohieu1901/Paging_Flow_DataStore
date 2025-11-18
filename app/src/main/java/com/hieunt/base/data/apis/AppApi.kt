package com.hieunt.base.data.apis

import com.hieunt.base.data.database.entities.AppModel
import retrofit2.Response
import retrofit2.http.GET

interface AppApi {
    @GET("all_data")
    suspend fun getAllData(): Response<List<AppModel>>
}