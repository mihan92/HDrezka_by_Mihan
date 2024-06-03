package com.mihan.movie.library.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mihan.movie.library.data.models.VideoHistoryDbModel

@Database(entities = [VideoHistoryDbModel::class], version = 1, exportSchema = false)
abstract class VideoHistoryDataBase: RoomDatabase() {

    abstract fun videoHistoryDao(): VideoHistoryDao
}