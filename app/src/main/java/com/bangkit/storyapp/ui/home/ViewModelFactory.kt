package com.bangkit.storyapp.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bangkit.storyapp.data.di.Injection
import com.bangkit.storyapp.data.preference.SettingPreference
import com.bangkit.storyapp.ui.login.LoginViewModel
import com.bangkit.storyapp.ui.map.MapViewModel
import com.bangkit.storyapp.ui.setting.SettingViewModel
import com.bangkit.storyapp.ui.upload.UploadViewModel

class ViewModelFactory(private val preference: SettingPreference, private val context: Context) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T: ViewModel> create(modelClass: Class<T>) : T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(preference, Injection.provideRepository(context)) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(preference) as T
            }
            modelClass.isAssignableFrom(SettingViewModel::class.java) -> {
                SettingViewModel(preference) as T
            }
            modelClass.isAssignableFrom(MapViewModel::class.java) -> {
                MapViewModel(preference, Injection.provideRepository(context)) as T
            }
//            modelClass.isAssignableFrom(UploadViewModel::class.java) -> {
//                UploadViewModel(preference) as T
//            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}