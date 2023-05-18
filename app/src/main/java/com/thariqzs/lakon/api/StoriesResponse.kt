package com.thariqzs.lakon.api

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.thariqzs.lakon.database.StoryItem
import kotlinx.parcelize.Parcelize

data class StoriesResponse(

	@field:SerializedName("listStory")
	val listStory: List<StoryItem>,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)