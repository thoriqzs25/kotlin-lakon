package com.thariqzs.lakon.di

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.thariqzs.lakon.api.ApiConfig
import com.thariqzs.lakon.data.db.MainDatabase
import com.thariqzs.lakon.data.repository.StoryRepository
import com.thariqzs.lakon.preference.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "userPreference")

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val TAG = "prthoriq"
        val token = runBlocking {
            UserPreferences.getInstance(context.dataStore).userPreferencesFlow().first().token
        }
        val database = MainDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService(token)
        Log.d(TAG, "token: $token")
        return StoryRepository(database, apiService, context)
    }
}