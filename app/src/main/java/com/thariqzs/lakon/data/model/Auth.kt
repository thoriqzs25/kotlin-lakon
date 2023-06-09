package com.thariqzs.lakon.api

import com.google.gson.annotations.SerializedName
import com.thariqzs.lakon.data.model.UserResponse

data class AuthResponse(
    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("loginResult")
    val loginResult: UserResponse? = null
)