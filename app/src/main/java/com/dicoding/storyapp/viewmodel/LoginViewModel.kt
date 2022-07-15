package com.dicoding.storyapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.model.UserPreference
import com.dicoding.storyapp.data.network.api.ApiConfig
import com.dicoding.storyapp.data.network.response.UserResponse
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val preference: UserPreference) : ViewModel() {
    private val _userResponse = MutableLiveData<UserResponse?>()
    val userResponse: LiveData<UserResponse?> = _userResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun login(email: String, password: String) {
        _isLoading.value = true
        _userResponse.value = null
        val service = ApiConfig.getApiService().login(email, password)
        service.enqueue(object : Callback<UserResponse> {
            override fun onResponse(
                call: Call<UserResponse>,
                response: Response<UserResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        _userResponse.value = response.body()
                        viewModelScope.launch {
                            response.body()?.loginResult?.isLogin = true
                            response.body()?.loginResult?.let { preference.setUserLogin(it) }
                        }
                    }
                } else {
                    val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                    _userResponse.value = UserResponse(
                        jsonObj.getBoolean("error"),
                        jsonObj.getString("message"),
                        null
                    )
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    companion object {
        private const val TAG = "LoginViewModel"
    }
}