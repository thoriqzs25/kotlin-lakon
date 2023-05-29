package com.thariqzs.lakon.ui.map

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.thariqzs.lakon.api.ApiConfig
import com.thariqzs.lakon.data.model.StoriesResponse
import com.thariqzs.lakon.data.model.Story
import com.thariqzs.lakon.data.repository.StoryRepository
import com.thariqzs.lakon.helper.Event
import com.thariqzs.lakon.preference.UserPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel (private val mApplication: Application, private val pref: UserPreferences): ViewModel(){
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _stories = MutableLiveData<List<Story>>()
    val stories: LiveData<List<Story>> = _stories

    private val _errorMsg = MutableLiveData<Event<String>>()
    val errorMsg: LiveData<Event<String>> = _errorMsg

    init {
        Log.d(TAG, "line 29 fetch data: ")
        getStories()
    }

    private fun getStories() {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getStoryLocation(1)
        client.enqueue(object : Callback<StoriesResponse> {
            override fun onResponse(
                call: Call<StoriesResponse>,
                response: Response<StoriesResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    Log.d(TAG, "onResponse: berhasil ${response.body()?.listStory}")
                    _stories.value = response.body()?.listStory
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