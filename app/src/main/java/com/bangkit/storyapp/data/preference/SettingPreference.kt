package com.bangkit.storyapp.data.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.bangkit.storyapp.data.model.UserLogin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingPreference private constructor(private val dataStore: DataStore<Preferences>){


    fun getUserToken(): Flow<UserLogin> {
        return dataStore.data.map {
            UserLogin(
                it[NAME_KEY] ?: "",
                it[USERID_KEY] ?: "",
                it[EMAIL_KEY] ?: "",
                it[PASSWORD_KEY] ?: "",
                it[TOKEN] ?: "",
                it[STATE] ?: false
            )
        }
    }

    suspend fun saveUserLogin(user: UserLogin) {
        dataStore.edit {
            it[NAME_KEY] = user.name
            it[USERID_KEY] = user.userId
            it[EMAIL_KEY] = user.email
            it[PASSWORD_KEY] = user.password
            it[TOKEN] = user.token
            it[STATE] = user.isLogin
        }
    }

    suspend fun logout() {
        dataStore.edit {preferences ->
            preferences[STATE] = false
        }
    }



    companion object {
        @Volatile
        private var INSTANCE: SettingPreference? = null

        private val TOKEN = stringPreferencesKey("token")
        private val STATE = booleanPreferencesKey("state")
        private val NAME_KEY = stringPreferencesKey("name")
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val PASSWORD_KEY = stringPreferencesKey("password")
        private val USERID_KEY = stringPreferencesKey("userId")

        fun getInstance(dataStore: DataStore<Preferences>) : SettingPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = SettingPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}