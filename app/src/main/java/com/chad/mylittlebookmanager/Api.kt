package com.chad.mylittlebookmanager

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {

    @GET("character")
    fun getItems(@Query("page") currentPage: Int): Call<ApiResponse>

}