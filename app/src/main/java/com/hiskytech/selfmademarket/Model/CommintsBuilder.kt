package com.hiskytech.selfmademarket.Model

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CommintsBuilder {
    private const val BASE_URL = "https://hiskytechs.com/"

    fun getInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
