package com.hiskytech.selfmademarket.Model

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CommintsBuilder {

    val apiUrl = "https://hiskytechs.com/"

    fun getInstance(): Retrofit{
        return Retrofit.Builder().baseUrl(apiUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}