package com.hieunt.base.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hieunt.base.data.database.dao.AppDao
import com.hieunt.base.data.database.entities.AppModel

@Database(entities = [AppModel::class],version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun filesDao(): AppDao
}