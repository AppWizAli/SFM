package com.hiskytech.selfmademarket.Model

import com.hiskytech.selfmademarket.ApiInterface.logininterface
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object loginBuilder {
    private const val BASE_URL = "https://hiskytechs.com/video_adminpenal/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiInterface: logininterface = retrofit.create(logininterface::class.java)
}



