package com.bangkit.storyapp.ui.home


import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bangkit.storyapp.data.database.StoryRepository
import com.bangkit.storyapp.data.model.ListStoryItem
import com.bangkit.storyapp.data.model.UserLogin
import com.bangkit.storyapp.data.preference.SettingPreference
import kotlinx.coroutines.launch


class HomeViewModel(private val preference: SettingPreference, private val storyRepository: StoryRepository) : ViewModel() {
    private val _allStories = MutableLiveData<List<ListStoryItem>>()
    val getAllStories: LiveData<List<ListStoryItem>> = _allStories

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private var _message = MutableLiveData<String>()
    val message: LiveData<String> = _message


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