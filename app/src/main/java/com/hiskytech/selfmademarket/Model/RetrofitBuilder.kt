package com.hiskytech.selfmademarket.Model

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitBuilder {
    val api = "https://en.selfmademarket.net/"
    fun getInstance() : Retrofit {
        return Retrofit.Builder().baseUrl(api)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}