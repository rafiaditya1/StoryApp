package com.bangkit.storyapp.ui.upload

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.bangkit.storyapp.data.model.UserLogin
import com.bangkit.storyapp.data.preference.SettingPreference
import kotlinx.coroutines.launch

class UploadViewModel(private val preference: SettingPreference) : ViewModel() {

    fun getUser(): LiveData<UserLogin> {
        return preference.getUserToken().asLiveData()
    }

    fun saveUser(user: UserLogin) {
        viewModelScope.launch {
            preference.saveUserLogin(user)
        }
    }
}