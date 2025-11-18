package com.hieunt.base.data.local

import com.hieunt.base.data.database.dao.AppDao
import dagger.Lazy
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataSource @Inject constructor(private val appDao: Lazy<AppDao>) {

}