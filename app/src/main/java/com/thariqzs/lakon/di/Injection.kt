package com.thariqzs.lakon.di

import android.content.Context
import android.util.Log
import com.thariqzs.lakon.api.ApiConfig
import com.thariqzs.lakon.data.db.MainDatabase
import com.thariqzs.lakon.data.repository.StoryRepository

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val database = MainDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return StoryRepository(database, apiService, context)
    }
}