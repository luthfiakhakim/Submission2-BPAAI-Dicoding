package com.dicoding.storyapp.data.network.response

import com.dicoding.storyapp.data.model.DataListStory
import com.google.gson.annotations.SerializedName

data class UserStoryResponse(
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("listStory")
    val dataListStories: List<DataListStory>
)