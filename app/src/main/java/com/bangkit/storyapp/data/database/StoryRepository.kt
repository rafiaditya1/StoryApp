package com.bangkit.storyapp.data.database

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.bangkit.storyapp.data.model.ListStoryItem
import com.bangkit.storyapp.data.networking.ApiService

class StoryRepository(private val storyDatabase: StoryDatabase, private val apiService: ApiService) {
    fun getStory(token: String): LiveData<PagingData<ListStoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, "Bearer $token"),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    @Suppress("UNCHEKED_CAST")
    suspend fun getLocation(token: String): List<ListStoryItem> {
        return apiService.getStoryLocation("bearer $token").listStory as List<ListStoryItem>
    }
}