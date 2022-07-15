package com.dicoding.storyapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.storyapp.data.model.DataListStory
import com.dicoding.storyapp.data.model.UserData
import com.dicoding.storyapp.data.model.UserPreference
import com.dicoding.storyapp.data.network.api.ApiConfig
import com.dicoding.storyapp.data.network.response.UserStoryResponse
import com.dicoding.storyapp.data.repository.StoryRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryViewModel(private val storyRepository: StoryRepository, private val pref: UserPreference) : ViewModel() {

    fun getAllStories(token: String) = storyRepository.getAllStories("Bearer $token").asLiveData()

    private val _itemStory = MutableLiveData<List<DataListStory>>()
    val itemStory: LiveData<List<DataListStory>> = _itemStory

    private val _isLoading = MutableLiveData<Boolean>()

    private val _isGetData = MutableLiveData<Boolean>()

    fun getUser(): LiveData<UserData> {
        return pref.getUserLogin().asLiveData()
    }

    fun showLocationStory(token: String) {
        _isLoading.value = true
        _isGetData.value = true
        val client = ApiConfig
            .getApiService()
            .getLocationStories("Bearer $token")
        client.enqueue(object : Callback<UserStoryResponse> {
            override fun onResponse(
                call: Call<UserStoryResponse>,
                response: Response<UserStoryResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        if (!responseBody.error) {
                            _itemStory.value = response.body()?.dataListStories
                            _isGetData.value = responseBody.message == "Story successfully fetched"
                        }
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<UserStoryResponse>, t: Throwable) {
                _isLoading.value = false
            }
        })
    }

    companion object {
        private const val TAG = "StoryViewModel"
    }
}