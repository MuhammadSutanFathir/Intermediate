package com.example.submissionintermediate.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.submissionintermediate.data.UserRepository
import com.example.submissionintermediate.data.pref.UserModel
import com.example.submissionintermediate.data.response.DetailStoryResponse
import com.example.submissionintermediate.data.response.ListStoryItem
import com.example.submissionintermediate.data.response.ListStoryResponse
import com.example.submissionintermediate.data.response.LoginResponse
import com.example.submissionintermediate.data.response.LoginResult
import com.example.submissionintermediate.data.response.RegisterResponse
import com.example.submissionintermediate.data.response.Story
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException


class MainViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _detailstories = MutableStateFlow<Result<Story>?>(null)
    val detailstories: StateFlow<Result<Story>?> get() = _detailstories

    private val _currentImageUri = MutableLiveData<Uri?>()
    val currentImageUri: LiveData<Uri?> = _currentImageUri

    private val _isLoginSuccessful = MutableLiveData<Boolean>()
    val isLoginSuccessful: LiveData<Boolean> get() = _isLoginSuccessful

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isLoggedOut = MutableLiveData<Boolean>()
    val isLoggedOut: LiveData<Boolean> = _isLoggedOut

    private val _stories = MutableStateFlow<Result<ListStoryResponse>?>(null)
    val stories: StateFlow<Result<ListStoryResponse>?> get() = _stories

    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession().asLiveData()
    }

    private val _isRegisterSuccessful = MutableLiveData<Boolean>()
    val isRegisterSuccessful: LiveData<Boolean> get() = _isRegisterSuccessful

    private val _upload = MutableLiveData<Boolean>()
    val upload: LiveData<Boolean> get() = _upload



    private fun saveSession(user: UserModel) {
        viewModelScope.launch {
            Log.d("UserSession", "Saving session: $user")
            userRepository.saveSession(user)
        }
    }

    fun register(name: String, email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = userRepository.register(name, email, password)

            result.onSuccess {
                _isRegisterSuccessful.value = true
                _isLoading.value = false
                clearErrorMessage()
            }.onFailure {
                _isRegisterSuccessful.value = false
                _errorMessage.value = it.message
            }
        }
    }

    fun login(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = userRepository.login(email, password)

            result.onSuccess { loginResult ->
                saveSession(UserModel(email, loginResult.token,true))
                _isLoginSuccessful.value = true
                _isLoading.value = false
                clearErrorMessage()
            }.onFailure {
                _isLoginSuccessful.value = false
                _errorMessage.value = it.message
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun setCurrentImageUri(uri: Uri?) {
        _currentImageUri.value = uri
    }
    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
            _isLoggedOut.value = true
        }
    }

    fun fetchStories(token: String) {
        viewModelScope.launch {
            _stories.value = userRepository.getStories(token)
        }
    }

    fun getDetailStories(token: String, id: String) {
        viewModelScope.launch {
            _detailstories.value =userRepository.getDetailStories(token,id)
        }
    }

    fun uploadStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody
    ) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = userRepository.uploadImage(token,file, description)
            result.onSuccess {
                _upload.value = true
                _isLoading.value = false
                clearErrorMessage()
            }.onFailure {
                _errorMessage.value = it.message
            }
        }
    }

}