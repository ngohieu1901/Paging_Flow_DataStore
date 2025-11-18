package com.hieunt.base.data.database.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class AppModel(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var name: String = "",
) : Parcelable