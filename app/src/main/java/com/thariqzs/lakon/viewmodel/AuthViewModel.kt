package com.thariqzs.lakon.viewmodel

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thariqzs.lakon.api.ApiConfig
import com.thariqzs.lakon.api.AuthResponse
import com.thariqzs.lakon.data.User
import com.thariqzs.lakon.helper.Event
import com.thariqzs.lakon.preference.UserPreferences
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthViewModel(application: Application, private val pref: UserPreferences) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _userDetail = MutableLiveData<User>()
    val userDetail: LiveData<User> = _userDetail

    private val _errorMsg = MutableLiveData<Event<String>>()
    val errorMsg: LiveData<Event<String>> = _errorMsg

    fun loginUser(email: String, password: String) {
        if (email.isNullOrEmpty() || password.isNullOrEmpty()) return

        _isLoading.value = true
        val client = ApiConfig.getApiService().loginUser(email!!, password!!)
        client.enqueue(object : Callback<AuthResponse> {
            override fun onResponse(
                call: Call<AuthResponse>,
                response: Response<AuthResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        if (responseBody.error == true) {
                            _errorMsg.value =
                                Event("Failed to register, ${responseBody.message!!}")
                        } else {
                            val userData = responseBody.loginResult
                            _userDetail.value = userData!!
                            setCurrentUserPreferences(
                                userData.userId!!,
                                userData.name!!,
                                userData.token!!
                            )
                            Log.d(TAG, "onResponse: tes ${userData.name}")
                        }
                    }
                } else {
                    _errorMsg.value = Event("Server Error, ${response.message()}")
                    Log.d(TAG, "onResponseFail: ${response.message()} ")
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMsg.value = Event("Error, cek koneksi anda!")
                Log.d(TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun registerUser(name: String, email: String, password: String) {
        if (name.isNullOrEmpty() || email.isNullOrEmpty() || password.isNullOrEmpty()) return

        _isLoading.value = true
        val client = ApiConfig.getApiService().registerUser(name!!, email!!, password!!)
        client.enqueue(object : Callback<AuthResponse> {
            override fun onResponse(
                call: Call<AuthResponse>,
                response: Response<AuthResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        if (responseBody.error == true) {
                            _errorMsg.value =
                                Event("Failed to register, ${responseBody.message!!}")
                        } else {
                            _errorMsg.value =
                                Event("User registered")
                        }
                    }
                } else {
                    _errorMsg.value = Event("Server Error, ${response.message()}")
                    Log.d(TAG, "onResponseFail: ${response.message()} ")
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMsg.value = Event("Error, cek koneksi anda!")
                Log.d(TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun setCurrentUserPreferences(userId: String, name: String, token: String) {
        viewModelScope.launch {
            pref.updateUserPreferences(userId, name, token)
        }
    }

    companion object {
        val TAG = "avmthoriq"
    }
}