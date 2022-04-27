package com.bangkit.storyapp.ui.login

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.*
import com.bangkit.storyapp.data.model.LoginResult
import com.bangkit.storyapp.data.model.UserLogin
import com.bangkit.storyapp.data.model.UserResponse
import com.bangkit.storyapp.data.networking.ApiConfig
import com.bangkit.storyapp.data.preference.SettingPreference
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val preference: SettingPreference) : ViewModel() {
    private var _user = MutableLiveData<LoginResult>()
    val user: LiveData<LoginResult> = _user

    private var _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private var _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private var _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun login(email: String, password: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().userLogin(email, password)
        client.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                _isLoading.value = false
                val responseBody = response.body()
                val responseBodyLogin = responseBody?.loginResult
                if (response.isSuccessful) {
//                    _user.value = response.body()?.loginResult
                    if (responseBody != null) {
                        if (responseBodyLogin != null) {
                            saveUser(UserLogin(responseBodyLogin.name, responseBodyLogin.userId, email, password, responseBodyLogin.token,true))
                        }
                    }
                } else {
                    Log.e(ContentValues.TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(ContentValues.TAG, "onFailure: ${t.message.toString()}")
            }

        })
    }

    @JvmName("getUser1")
    fun getUser(): LiveData<UserLogin> {
        return preference.getUserToken().asLiveData()
    }

    fun saveUser(user: UserLogin) {
        viewModelScope.launch {
            preference.saveUserLogin(user)
        }
    }
}