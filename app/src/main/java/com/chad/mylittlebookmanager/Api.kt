package com.chad.mylittlebookmanager

import retrofit2.Call
import retrofit2.http.GET

interface Api {

    @GET("character")
    fun getItems(): Call<ApiResponse>

}