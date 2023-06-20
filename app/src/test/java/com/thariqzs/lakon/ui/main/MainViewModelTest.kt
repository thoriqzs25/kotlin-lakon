package com.thariqzs.lakon.ui.main

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.thariqzs.lakon.data.model.Story
import com.thariqzs.lakon.data.repository.StoryRepository
import com.thariqzs.lakon.preference.UserPreferences
import com.thariqzs.lakon.ui.adapter.StoryRvAdapter
import com.thariqzs.lakon.utils.MainDispatcherRule
import com.thariqzs.lakon.utils.StoryDataDummy
import com.thariqzs.lakon.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Mock
    private lateinit var application: Application

    @Mock
    private lateinit var pref: UserPreferences

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when Get Story List Should Not Null and Return Data`() = runTest {
        val dummyStory = StoryDataDummy.generateDummyNewsEntity()
        val data: PagingData<Story> = StoryPagingSource.snapshot(dummyStory)
        val expectedStories = MutableLiveData<PagingData<Story>>()
        expectedStories.value = data
        Mockito.`when`(storyRepository.getStory()).thenReturn(expectedStories)

        val mainViewModel = MainViewModel(pref, storyRepository)
        val actualStories: PagingData<Story> = mainViewModel.story.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryRvAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStories)

        assertNotNull(differ.snapshot())
        assertEquals(dummyStory.size, differ.snapshot().size)
        assertEquals(dummyStory[0], differ.snapshot()[0])
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when Get Stories Empty Should Return No Data`() = runTest {
        val data: PagingData<Story> = PagingData.from(emptyList())
        val expectedStories = MutableLiveData<PagingData<Story>>()
        expectedStories.value = data

        Mockito.`when`(storyRepository.getStory()).thenReturn(expectedStories)

        val mainViewModel = MainViewModel(pref, storyRepository)
        val actualStories: PagingData<Story> = mainViewModel.story.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryRvAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStories)

        Assert.assertEquals(0, differ.snapshot().size)
    }
}

class StoryPagingSource : PagingSource<Int, LiveData<List<Story>>>() {

    companion object {
        fun snapshot(items: List<Story>): PagingData<Story> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<Story>>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<Story>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}