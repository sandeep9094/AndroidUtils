package com.kochartech.myapplication

sealed class LoadingState {

    object Loading : LoadingState()
    object Success : LoadingState()
    data class Error(val error: String) : LoadingState()

}