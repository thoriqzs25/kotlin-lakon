package com.thariqzs.lakon.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.thariqzs.lakon.data.model.Story
import com.thariqzs.lakon.data.model.UserResponse
import com.thariqzs.lakon.data.repository.StoryRepository
import com.thariqzs.lakon.helper.Event
import com.thariqzs.lakon.preference.UserPreferences
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class MainViewModel(private var pref: UserPreferences, private val storyRepository: StoryRepository): ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _userResponseDetail = MutableLiveData<UserResponse>()
    val userResponseDetail: LiveData<UserResponse> = _userResponseDetail

    private val _errorMsg = MutableLiveData<Event<String>>()
    val errorMsg: LiveData<Event<String>> = _errorMsg

//    private val _stories = MutableLiveData<List<Story>>()
//    val stories: LiveData<List<Story>> = _stories

    val story: LiveData<PagingData<Story>> =
        storyRepository.getStory().cachedIn(viewModelScope)

    init {
        getUserPreferencesData()
    }

    fun getUserPreferencesData() {
        pref.userPreferencesFlow()
            .asLiveData()
            .observeForever { user ->
                _userResponseDetail.value = user
            }
    }

    suspend fun removeStoryList() {
        storyRepository.deleteList()
    }

//    private fun sortByDate(list : List<Story>?): List<Story>? {
//        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
//
//        val sortedList = list?.sortedByDescending {
//            dateFormat.parse(it.createdAt)?.time
//        }
//
//        return sortedList
//    }

    fun logoutUser() {
        viewModelScope.launch {
            pref.clearUserPreferences()
        }
    }

//    fun getStories(token: String) {
//        _isLoading.value = true
//        val client = ApiConfig.getApiService().getAllStories("Bearer $token")
//        client.enqueue(object : Callback<StoriesResponse> {
//            override fun onResponse(
//                call: Call<StoriesResponse>,
//                response: Response<StoriesResponse>
//            ) {
//                _isLoading.value = false
//                if (response.isSuccessful) {
//                    _stories.value = sortByDate(response.body()?.listStory)
//                } else {
//                    Log.d(TAG, "onResponseFail: ${response.message()} ")
//                    _errorMsg.value = Event("Server Error, ${response.message()}")
//                }
//            }
//
//            override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
//                _isLoading.value = false
//                _errorMsg.value = Event("Error, cek koneksi anda!")
//                Log.d(TAG, "onFailure: ${t.message}")
//            }
//        })
//    }

    companion object {
        val TAG = "mvmthoriq"
    }
}