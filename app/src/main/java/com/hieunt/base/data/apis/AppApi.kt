package com.hieunt.base.data.apis

import com.hieunt.base.data.database.entities.AppModel
import com.hieunt.base.data.dto.DataFixtures
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AppApi {
    @GET("football/fixtures/date/{date}")
    fun getFixturesByDate(
        @Path("date") date: String,
        @Query("api_token") apiToken: String,
        @Query("timezone") timezone: String,
        @Query("per_page") perPage: Int,
        @Query("include") include: String,
        @Query("page") page: Int
    ): DataFixtures
}