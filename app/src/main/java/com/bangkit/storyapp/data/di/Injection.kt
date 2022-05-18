package com.bangkit.storyapp.data.di

import android.content.Context
import com.bangkit.storyapp.data.database.StoryDatabase
import com.bangkit.storyapp.data.database.StoryRepository
import com.bangkit.storyapp.data.networking.ApiConfig

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return StoryRepository(database, apiService)
    }
}