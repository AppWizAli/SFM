package com.hiskytech.selfmademarket.Model

import com.hiskytech.selfmademarket.ApiInterface.PublishStoryInterface
import com.hiskytech.selfmademarket.Model.RetrofitBuilder.api
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object StoryBuilder {

    private const val BASE_URL = "https://hiskytechs.com/video_adminpenal/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiInterface: PublishStoryInterface = retrofit.create(PublishStoryInterface::class.java)

}