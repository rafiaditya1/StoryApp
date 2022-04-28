package com.bangkit.storyapp.ui.upload

import android.util.Log
import androidx.lifecycle.*
import com.bangkit.storyapp.data.model.*
import com.bangkit.storyapp.data.networking.ApiConfig
import com.bangkit.storyapp.data.preference.SettingPreference
import com.bangkit.storyapp.utils.ApiCallbackString
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//
class UploadViewModel(private val preference: SettingPreference) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getUser(): LiveData<UserLogin> {
        return preference.getUserToken().asLiveData()
    }


    companion object {
        private const val TAG = "addStoryViewmodel"
        private const val SUCCESS = "success"
    }
}