package com.example.myapplication.repository

import com.example.myapplication.model.response.Vehicle
import com.example.myapplication.retrofit.ApiService
import retrofit2.Response

class VehicleRepository(private val apiService: ApiService) {

    /**
     * Method to call v2 vehicles api
     *
     * @param authToken authToken
     */
    suspend fun getVehicles(authToken: String): Response<ArrayList<Vehicle.VehicleItem>> {
        return apiService.getVehicles(authToken)
    }
}