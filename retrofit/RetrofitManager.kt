package com.example.retrofitbestpractice.retrofit

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import okhttp3.logging.HttpLoggingInterceptor

object RetrofitManager {

    const val BASE_URL = "https://ciprand.p3p.repl.co/"

    fun getApiService(context: Context): ApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getOkHttpClient(context))
            .build()
        return retrofit.create(ApiService::class.java)
    }

    private fun getOkHttpClient(context: Context): OkHttpClient {
        val logging = HttpLoggingInterceptor() // set your desired log level
        if (BuildConfig.DEBUG) {
            logging.level = HttpLoggingInterceptor.Level.BASIC
        } else {
            logging.level = HttpLoggingInterceptor.Level.NONE
        }
        val okHttpClientBuilder = OkHttpClient.Builder()
            .addInterceptor(NetworkConnectionInterceptor(context))
            .addInterceptor(logging)
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
        return okHttpClientBuilder.build()
    }

}