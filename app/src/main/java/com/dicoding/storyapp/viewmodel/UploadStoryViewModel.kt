package com.dicoding.storyapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.storyapp.data.model.UserData
import com.dicoding.storyapp.data.model.UserPreference
import com.dicoding.storyapp.data.network.api.ApiConfig
import com.dicoding.storyapp.data.network.response.UploadStoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UploadStoryViewModel(private val preference: UserPreference) : ViewModel() {

    private val _fileUploadResponse = MutableLiveData<UploadStoryResponse>()
    val fileUploadResponse: LiveData<UploadStoryResponse> = _fileUploadResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getUser(): LiveData<UserData> {
        return preference.getUserLogin().asLiveData()
    }

    fun uploadImage(token: String, description: RequestBody, imageMultipart: MultipartBody.Part) {

        _isLoading.value = true
        val service = ApiConfig.getApiService().uploadStory("Bearer $token", description, imageMultipart)
        service.enqueue(object : Callback<UploadStoryResponse> {
            override fun onResponse(
                call: Call<UploadStoryResponse>,
                response: Response<UploadStoryResponse>
            ) {
                if (response.isSuccessful) {
                    _fileUploadResponse.value = response.body()
                } else {
                    val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                    _fileUploadResponse.value =
                        UploadStoryResponse(jsonObj.getBoolean("error"), jsonObj.getString("message"))
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<UploadStoryResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    companion object {
        private const val TAG = "UploadStoryViewModel"
    }
}