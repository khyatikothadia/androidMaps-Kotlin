package com.example.myapplication.repository

import com.example.myapplication.model.request.LoginRequest
import com.example.myapplication.model.request.SignUpRequest
import com.example.myapplication.model.response.User
import com.example.myapplication.retrofit.ApiService
import retrofit2.Response

class AccountRepository(private val apiService: ApiService) {

    /**
     * Method to call login api
     *
     * @param loginRequest loginRequest
     */
    suspend fun userLogin(loginRequest: LoginRequest): Response<User> {
        return apiService.userLogin(loginRequest)
    }

    /**
     * Method to call sign up api
     *
     * @param signUpRequest signUpRequest
     */
    suspend fun userSignUp(signUpRequest: SignUpRequest): Response<User> {
        return apiService.userSignUp(signUpRequest)
    }
}