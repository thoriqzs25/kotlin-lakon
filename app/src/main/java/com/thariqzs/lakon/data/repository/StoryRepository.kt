package com.thariqzs.lakon.data.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.thariqzs.lakon.api.ApiService
import com.thariqzs.lakon.data.db.MainDatabase
import com.thariqzs.lakon.data.model.Story
import com.thariqzs.lakon.data.paging.remotemediator.StoryRemoteMediator

class StoryRepository(private val mainDatabase: MainDatabase, private val apiService: ApiService, private val context: Context) {
    fun getStory(): LiveData<PagingData<Story>> {
        Log.d("srthoriq", "getStory: line 17")
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 10
            ),
            remoteMediator = StoryRemoteMediator(mainDatabase, apiService, context),
            pagingSourceFactory = {
                mainDatabase.storyDao().getStory()
            }
        ).liveData
    }

    suspend fun deleteList() {
        mainDatabase.storyDao().deleteAll()
        mainDatabase.remoteKeysDao().deleteRemoteKeys()
    }
}