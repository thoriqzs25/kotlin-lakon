package com.thariqzs.lakon.ui.post

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.thariqzs.lakon.api.ApiConfig
import com.thariqzs.lakon.api.FileUploadResponse
import com.thariqzs.lakon.data.model.UserResponse
import com.thariqzs.lakon.helper.Event
import com.thariqzs.lakon.preference.UserPreferences
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostViewModel(private val mApplication: Application, private val pref: UserPreferences) :
    ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _userResponseDetail = MutableLiveData<UserResponse>()
    val userResponseDetail: LiveData<UserResponse> = _userResponseDetail

    private val _errorMsg = MutableLiveData<Event<String>>()
    val errorMsg: LiveData<Event<String>> = _errorMsg

    init {
        getUserPreferencesData()
    }

    private fun getUserPreferencesData() {
        _isLoading.value = true
        pref.userPreferencesFlow()
            .asLiveData()
            .observeForever { user ->
                _userResponseDetail.value = user
            }
        _isLoading.value = false
    }

    fun postStory(token: String, desc: RequestBody, imageMultipart: MultipartBody.Part) {
        _isLoading.value = true
        val apiService = ApiConfig.getApiService()
        val uploadImageRequest = apiService.uploadImage("Bearer $token",imageMultipart, desc)
        uploadImageRequest.enqueue(object : Callback<FileUploadResponse> {
            override fun onResponse(
                call: Call<FileUploadResponse>,
                response: Response<FileUploadResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        _errorMsg.value = Event("Upload Success, ${response.message()}")
                    }
                } else {
                    _errorMsg.value = Event("Server Error, ${response.message()}")
                }
            }

            override fun onFailure(call: Call<FileUploadResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMsg.value = Event("Server Error, ${t.message}")
            }
        })
    }
}