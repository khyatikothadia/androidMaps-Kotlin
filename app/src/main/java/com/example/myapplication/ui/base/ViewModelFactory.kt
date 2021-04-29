package com.example.myapplication.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.repository.AccountRepository
import com.example.myapplication.retrofit.ApiService
import com.example.myapplication.viewmodel.AccountViewModel

class ViewModelFactory(private val apiService: ApiService) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AccountViewModel::class.java)) {
            return AccountViewModel(AccountRepository(apiService)) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}