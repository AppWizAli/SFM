package com.hiskytech.selfmademarket.Model

import com.hiskytech.selfmademarket.ApiInterface.NotificationInterface
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NotificationBuilder {

    val apiUrl = "https://hiskytechs.com/"

    fun getInstance() : Retrofit {
        return Retrofit.Builder()
            .baseUrl(apiUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    fun getNotificationInterface(): NotificationInterface {
        return getInstance().create(NotificationInterface::class.java)
    }
}