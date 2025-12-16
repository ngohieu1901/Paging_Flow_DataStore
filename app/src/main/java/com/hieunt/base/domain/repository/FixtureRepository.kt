package com.hieunt.base.domain.repository

import androidx.paging.PagingData
import com.hieunt.base.domain.model.FixtureDomain
import kotlinx.coroutines.flow.Flow

interface FixtureRepository {
    fun getAllFixture(): Flow<PagingData<FixtureDomain>>
}