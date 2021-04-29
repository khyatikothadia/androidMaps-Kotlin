package com.example.myapplication.retrofit

import com.example.myapplication.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "https://blooming-stream-45371.herokuapp.com/"

    private fun getRetrofit(): Retrofit {

        val levelType: HttpLoggingInterceptor.Level =
            if (BuildConfig.BUILD_TYPE.contentEquals("debug"))
                HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE

        val logging = HttpLoggingInterceptor()
        logging.level = levelType

        val okHttpClient = OkHttpClient.Builder()
        okHttpClient.connectTimeout(60, TimeUnit.SECONDS)
        okHttpClient.readTimeout(60, TimeUnit.SECONDS)
        okHttpClient.writeTimeout(60, TimeUnit.SECONDS)
        okHttpClient.addInterceptor(logging)

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient.build())
            .build()
    }

    val apiService: ApiService = getRetrofit().create(ApiService::class.java)

}