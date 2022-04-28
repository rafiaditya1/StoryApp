package com.bangkit.storyapp.ui.map

import androidx.lifecycle.*
import com.bangkit.storyapp.data.database.StoryRepository
import com.bangkit.storyapp.data.model.ListStoryItem
import com.bangkit.storyapp.data.model.UserLogin
import com.bangkit.storyapp.data.preference.SettingPreference
import kotlinx.coroutines.launch

class MapViewModel(private val preference: SettingPreference, private val storyRepository: StoryRepository) : ViewModel() {
    private val _listStory = MutableLiveData<List<ListStoryItem>>()
    var listStory: LiveData<List<ListStoryItem>> = _listStory

    fun getUser(): LiveData<UserLogin> {
        return preference.getUserToken().asLiveData()
    }

    fun getLocation(token: String) {
        viewModelScope.launch {
            _listStory.postValue(storyRepository.getLocation(token))
        }
    }
}