package com.example.myapplication.retrofit

import com.example.myapplication.model.request.LoginRequest
import com.example.myapplication.model.request.SignUpRequest
import com.example.myapplication.model.response.User
import com.example.myapplication.model.response.Vehicle
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST


interface ApiService {

    @POST("api/v2/people/authenticate")
    suspend fun userLogin(@Body loginRequest: LoginRequest): Response<User>

    @POST("api/v2/people/create")
    suspend fun userSignUp(@Body signUpRequest: SignUpRequest): Response<User>

    @GET("api/v2/vehicles")
    suspend fun getVehicles(@Header("Authorization") authToken: String): Response<ArrayList<Vehicle.VehicleItem>>
}
