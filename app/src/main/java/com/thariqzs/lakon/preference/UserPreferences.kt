package com.thariqzs.lakon.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import com.thariqzs.lakon.data.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    fun userPreferencesFlow(): Flow<User> {
        return dataStore.data.map { preferences ->
            val userId = preferences[USER_ID]  ?: "Not Found"
            val name = preferences[USER_NAME] ?: "Not Found"
            val token = preferences[USER_TOKEN] ?: "Not Found"

            User(userId, name, token)
        }
    }

    suspend fun updateUserPreferences(userId: String, name: String, token: String) {
        dataStore.edit { preferences ->
            preferences[USER_ID] = userId
            preferences[USER_NAME] = name
            preferences[USER_TOKEN] = token
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }

        private val USER_ID = stringPreferencesKey("user_id")
        private val USER_NAME = stringPreferencesKey("user_name")
        private val USER_TOKEN = stringPreferencesKey("user_token")
    }
}
