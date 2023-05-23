package com.thariqzs.lakon.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.thariqzs.lakon.data.db.dao.RemoteKeysDao
import com.thariqzs.lakon.data.db.dao.StoryDao
import com.thariqzs.lakon.data.model.RemoteKeys
import com.thariqzs.lakon.data.model.Story

@Database(entities = [Story::class, RemoteKeys::class], version = 2)
abstract class MainDatabase : RoomDatabase() {
    abstract fun storyDao(): StoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        @Volatile
        private  var INSTANCE: MainDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): MainDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    MainDatabase::class.java, "quote_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}