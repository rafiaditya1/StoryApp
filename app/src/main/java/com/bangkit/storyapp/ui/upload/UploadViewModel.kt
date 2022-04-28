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

//    fun uploadImage(
//        user: UserLogin,
//        description: String,
//        imageMultipart: MultipartBody.Part,
//        lat: Float,
//        lon: Float,
//        callback: ApiCallbackString
//    ) {
//        _isLoading.value = true
//        val client = ApiConfig.getApiService().storyUpload("Bearer ${user.token}", imageMultipart, description, lat, lon)
//        client.enqueue(object : Callback<UploadResponse> {
//            override fun onResponse(
//                call: Call<UploadResponse>,
//                response: Response<UploadResponse>
//            ) {
//                if (response.isSuccessful) {
//                    val responseBody = response.body()
//                    if (responseBody != null && !responseBody.error) {
//                        callback.onResponse(response.body() != null, SUCCESS)
//                    }
//                } else {
//                    Log.e(TAG, "onFailure: ${response.message()}")
//
//                    // get message error
//                    val jsonObject =
//                        JSONTokener(response.errorBody()!!.string()).nextValue() as JSONObject
//                    val message = jsonObject.getString("message")
//                    callback.onResponse(false, message)
//                }
//            }
//
//            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
//                _isLoading.value = false
//                Log.e(TAG, "onFailure: ${t.message}")
//                callback.onResponse(false, t.message.toString())
//            }
//        })
//
//    }

    fun getUser(): LiveData<UserLogin> {
        return preference.getUserToken().asLiveData()
    }

    var getUserToken : LiveData<String> = preference.getToken().asLiveData()
//
//    fun saveUser(user: UserLogin) {
//        viewModelScope.launch {
//            preference.saveUserLogin(user)
//        }
//    }

    companion object {
        private const val TAG = "addStoryViewmodel"
        private const val SUCCESS = "success"
    }
}