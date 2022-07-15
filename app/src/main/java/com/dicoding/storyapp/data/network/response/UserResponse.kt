package com.dicoding.storyapp.data.network.response

import com.dicoding.storyapp.data.model.UserData
import com.google.gson.annotations.SerializedName

data class UserResponse(

    @field:SerializedName("error")
    var error: Boolean,

    @field:SerializedName("message")
    var message: String,

    @field:SerializedName("loginResult")
    var loginResult: UserData?
)