package com.thariqzs.lakon.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
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
import kotlinx.coroutines.flow.transform
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

    private val _story = MutableLiveData<PagingData<Story>>()
    val story: LiveData<PagingData<Story>> = _story

    // Rest of the code...

    init {
        // Fetch story data and assign it to _story
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val data: LiveData<PagingData<Story>> = storyRepository.getStory().cachedIn(viewModelScope)
                data.observeForever { pagingData -> _story.value = pagingData }
            } catch (e: Exception) {
                _errorMsg.value = Event("Error fetching story: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
        getUserPreferencesData()
    }

    fun getUserPreferencesData() {
        pref.userPreferencesFlow()
            .transform { value ->
                // Map the nullable value to a non-null value or a default value if needed
                emit(value ?: UserResponse(value.userId, value.name, value.token))
            }
            .asLiveData()
            .observeForever { user ->
                _userResponseDetail.value = user
            }
    }

    suspend fun removeStoryList() {
        storyRepository.deleteList()
    }

    fun logoutUser() {
        viewModelScope.launch {
            pref.clearUserPreferences()
        }
    }


    companion object {
        val TAG = "mvmthoriq"
    }
}