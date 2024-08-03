package com.hiskytech.selfmademarket.ApiInterface

import com.hiskytech.selfmademarket.Model.ModelBitCoin
import com.hiskytech.selfmademarket.Model.ModelCourses
import retrofit2.Call
import retrofit2.http.GET

interface BitCoinInterface {

    @GET("/planemanger/get_bitcons.php")
    fun getBitCoin() : Call<ModelBitCoin>

}