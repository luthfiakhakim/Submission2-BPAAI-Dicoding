package com.dicoding.storyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.model.UserData
import com.dicoding.storyapp.data.model.UserPreference
import kotlinx.coroutines.launch

class MainViewModel(private val preference: UserPreference) : ViewModel()  {

    fun getUser(): LiveData<UserData> {
        return preference.getUserLogin().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            preference.logout()
        }
    }
}