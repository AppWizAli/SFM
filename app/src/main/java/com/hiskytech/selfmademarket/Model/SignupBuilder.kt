package com.hiskytech.selfmademarket.Model

import com.hiskytech.selfmademarket.api.SignupInterface
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SignupBuilder {


    private const val BASE_URL = "https://hiskytechs.com/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiInterface: SignupInterface = retrofit.create(SignupInterface::class.java)

}