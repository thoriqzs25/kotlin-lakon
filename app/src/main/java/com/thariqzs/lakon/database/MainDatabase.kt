package com.thariqzs.lakon.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.thariqzs.lakon.database.StoryItem

@Database(entities = [StoryItem::class], version = 1)
abstract class MainDatabase : RoomDatabase() {
    abstract fun storyDao(): StoryDao

    companion object {
        @Volatile
        private  var INSTANCE: MainDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): MainDatabase {
            if (INSTANCE == null) {
                synchronized(MainDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext, MainDatabase::class.java, "main_database")
                        .build()
                }
            }

            return INSTANCE as MainDatabase
        }
    }
}