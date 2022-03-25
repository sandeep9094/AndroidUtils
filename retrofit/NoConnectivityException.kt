package com.example.retrofitbestpractice.retrofit

import java.io.IOException

class NoConnectivityException : IOException() {

    override val message: String
        get() = "No Internet Connection"

}