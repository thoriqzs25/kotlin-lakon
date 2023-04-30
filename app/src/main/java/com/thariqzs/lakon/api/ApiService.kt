package com.thariqzs.lakon.api

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("login")
    fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String,
    ): Call<AuthResponse>

    @FormUrlEncoded
    @POST("register")
    fun registerUser(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
    ): Call<AuthResponse>
//
//    @GET("users/{username}")
//    fun getUserDetail(@Path("username") username: String): Call<UserDetailResponse>
//
//    @GET("users/{username}/followers")
//    fun getFollowers(@Path("username") username: String): Call<List<FollowResponseItem>>
//
//    @GET("users/{username}/following")
//    fun getFollowing(@Path("username") username: String): Call<List<FollowResponseItem>>
}