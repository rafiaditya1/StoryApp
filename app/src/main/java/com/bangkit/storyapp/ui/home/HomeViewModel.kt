package com.bangkit.storyapp.ui.home

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bangkit.storyapp.data.database.StoryRepository
import com.bangkit.storyapp.data.model.ListStoryItem
import com.bangkit.storyapp.data.model.StoryResponse
import com.bangkit.storyapp.data.model.UserLogin
import com.bangkit.storyapp.data.networking.ApiConfig
import com.bangkit.storyapp.data.preference.SettingPreference
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel(private val preference: SettingPreference, private val storyRepository: StoryRepository) : ViewModel() {
    private val _allStories = MutableLiveData<List<ListStoryItem>>()
    val getAllStories: LiveData<List<ListStoryItem>> = _allStories

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private var _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

//    fun getStories(token: String) {
//        _isLoading.value = true
//        val client = ApiConfig.getApiService().getAllStories("Bearer $token")
//        client.enqueue(object : Callback<StoryResponse> {
//            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
//                _isLoading.value = false
//                if (response.isSuccessful && response.body()?.message == "Stories fetched successfully") {
//                    _allStories.value = response.body()?.listStory
//                    Log.e(ContentValues.TAG, "onResponse: ${response.message()}")
//                } else {
//                    Log.e(ContentValues.TAG, "onFailure: ${response.message()}")
//                }
//            }
//
//            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
//                _isLoading.value = false
//                Log.e(ContentValues.TAG, "onFailure: ${t.message.toString()}")
//            }
//
//        })
//    }

    fun getStories(token: String) : LiveData<PagingData<ListStoryItem>> = storyRepository.getStory(token).cachedIn(viewModelScope)


    fun getUser(): LiveData<UserLogin> {
        return preference.getUserToken().asLiveData()
    }

    fun saveUser(user: UserLogin) {
        viewModelScope.launch {
            preference.saveUserLogin(user)
        }
    }
}