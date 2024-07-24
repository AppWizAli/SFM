package com.hiskytech.selfmademarket.Model

import com.hiskytech.selfmademarket.ApiInterface.ApiInterFace
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://deezerdevs-deezer.p.rapidapi.com/"
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val apiInterface: ApiInterFace = retrofit.create(ApiInterFace::class.java)
}
