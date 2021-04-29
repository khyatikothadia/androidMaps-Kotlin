package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.myapplication.model.request.LoginRequest
import com.example.myapplication.model.request.SignUpRequest
import com.example.myapplication.repository.AccountRepository
import com.example.myapplication.util.Resource
import kotlinx.coroutines.Dispatchers

class AccountViewModel(private val accountRepository: AccountRepository) : ViewModel() {

    /**
     * Method to get login user info hitting login api
     */
    fun getUserInfo(loginRequest: LoginRequest) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = accountRepository.userLogin(loginRequest)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    /**
     * Method to get new user info by hitting sign up api
     */
    fun getNewUserInfo(signUpRequest: SignUpRequest) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = accountRepository.userSignUp(signUpRequest)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
}