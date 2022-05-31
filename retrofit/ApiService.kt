package com.example.retrofitbestpractice.retrofit

import retrofit2.Call
import retrofit2.http.GET

interface ApiService {

    @GET("/api?len=20&count=10")
    fun getRandomActivity(): Call<RandomString>


}