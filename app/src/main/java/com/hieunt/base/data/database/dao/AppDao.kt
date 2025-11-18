package com.hieunt.base.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.hieunt.base.data.database.entities.AppModel
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Insert
    suspend fun insert(files: List<AppModel>)

    @Update
    suspend fun update(file: AppModel)

    @Delete
    suspend fun delete(files: List<AppModel>)

    @Query("SELECT * FROM AppModel")
    suspend fun getAll(): List<AppModel>

    @Query("SELECT * FROM AppModel")
    fun getAllFlow(): Flow<List<AppModel>>

}