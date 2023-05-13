package com.thariqzs.lakon.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.thariqzs.lakon.api.ApiConfig
import com.thariqzs.lakon.api.ListStoryItem
import com.thariqzs.lakon.api.StoriesResponse
import com.thariqzs.lakon.data.User
import com.thariqzs.lakon.helper.Event
import com.thariqzs.lakon.preference.UserPreferences
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class MainViewModel(application: Application, private var pref: UserPreferences): ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _userDetail = MutableLiveData<User>()
    val userDetail: LiveData<User> = _userDetail

    private val _errorMsg = MutableLiveData<Event<String>>()
    val errorMsg: LiveData<Event<String>> = _errorMsg

    private val _stories = MutableLiveData<List<ListStoryItem>>()
    val stories: LiveData<List<ListStoryItem>> = _stories

    init {
        getUserPreferencesData()
    }

    fun getUserPreferencesData() {
        pref.userPreferencesFlow()
            .asLiveData()
            .observeForever { user ->
                _userDetail.value = user
            }
    }

    private fun sortByDate(list : List<ListStoryItem>?): List<ListStoryItem>? {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())

        val sortedList = list?.sortedByDescending {
            dateFormat.parse(it.createdAt)?.time
        }

        return sortedList
    }

    fun logoutUser() {
        viewModelScope.launch {
            pref.clearUserPreferences()
        }
    }

    fun getStories(token: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getAllStories("Bearer $token")
        client.enqueue(object : Callback<StoriesResponse> {
            override fun onResponse(
                call: Call<StoriesResponse>,
                response: Response<StoriesResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _stories.value = sortByDate(response.body()?.listStory)
                } else {
                    Log.d(TAG, "onResponseFail: ${response.message()} ")
                    _errorMsg.value = Event("Server Error, ${response.message()}")
                }
            }

            override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMsg.value = Event("Error, cek koneksi anda!")
                Log.d(TAG, "onFailure: ${t.message}")
            }
        })
    }

    companion object {
        val TAG = "mvmthoriq"
    }
}