package com.hiskytech.selfmademarket.ApiInterface

import com.hiskytech.selfmademarket.Model.ModelBitCoin
import com.hiskytech.selfmademarket.Model.ModelForex
import retrofit2.Call
import retrofit2.http.GET

interface ForexInterface {

    @GET("/planemanger/get_forex.php")
    fun getForex() : Call<ModelForex>
}