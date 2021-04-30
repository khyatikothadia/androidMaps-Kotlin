package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.myapplication.repository.VehicleRepository
import com.example.myapplication.util.Resource
import kotlinx.coroutines.Dispatchers

class VehicleViewModel(private val vehicleRepository: VehicleRepository) : ViewModel() {

    /**
     * Method to get vehicles info by hitting vehicles api
     */
    fun getVehiclesDetails(authToken: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = vehicleRepository.getVehicles(authToken)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
}