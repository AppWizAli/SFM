package com.hiskytech.selfmademarket.api

import com.hiskytech.selfmademarket.Model.Data
import com.hiskytech.selfmademarket.Model.ModelSubscription
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("/planemanger/subscribeapi.php") // replace with your actual endpoint
    fun signUp(@Body data: Data): Call<ModelSubscription>
}
