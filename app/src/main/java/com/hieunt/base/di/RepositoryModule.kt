package com.hieunt.base.di

import com.hieunt.base.domain.repository.FixtureRepository
import com.hieunt.base.data.repositories.FixtureRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Singleton
    @Binds
    abstract fun bindTaskRepository(repository: FixtureRepositoryImpl): FixtureRepository
}