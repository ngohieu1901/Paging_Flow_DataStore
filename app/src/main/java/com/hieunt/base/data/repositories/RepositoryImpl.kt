package com.hieunt.base.data.repositories

import com.hieunt.base.data.local.LocalDataSource
import com.hieunt.base.data.remote.RemoteDataSource
import com.hieunt.base.di.IoDispatcher
import com.hieunt.base.domain.repository.Repository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): Repository {

}