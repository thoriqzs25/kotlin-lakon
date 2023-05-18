package com.thariqzs.lakon.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.thariqzs.lakon.api.ApiService
import com.thariqzs.lakon.database.MainDatabase
import com.thariqzs.lakon.database.StoryItem

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val database: MainDatabase,
    private val apiService: ApiService
) : RemoteMediator<Int, StoryItem>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoryItem>
    ): MediatorResult {
        val page = INITIAL_PAGE_INDEX

        try {
            val responseData = apiService.getAllStories(page, state.config.pageSize)

            val endOfPaginationReached = responseData.isEmpty()

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.storyDao().deleteAll()
                }
                database.storyDao().insertStory(responseData)
            }
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}