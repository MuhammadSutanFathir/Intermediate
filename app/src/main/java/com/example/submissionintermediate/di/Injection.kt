package com.example.submissionintermediate.di

import android.content.Context
import com.example.submissionintermediate.data.UserRepository
import com.example.submissionintermediate.data.pref.UserPreference
import com.example.submissionintermediate.data.pref.dataStore
import com.example.submissionintermediate.data.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getUser().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return UserRepository.getInstance(pref, apiService)
    }
}