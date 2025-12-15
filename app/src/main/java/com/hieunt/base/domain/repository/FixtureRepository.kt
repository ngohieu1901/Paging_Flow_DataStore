package com.hieunt.base.domain.repository

import com.hieunt.base.domain.model.FixtureDomain

interface FixtureRepository {
    suspend fun getAllFixtureByDate(
        date: String,
        timezone: String,
        include: String,
        perPage: Int,
        page: Int
    ): Result<List<FixtureDomain>>
}