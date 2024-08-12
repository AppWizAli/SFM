package com.hiskytech.selfmademarket.ApiInterface

import com.hiskytech.selfmademarket.Model.ModelLoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
data class UserLoginRequest(val email: String, val password: String)

interface logininterface {
    @POST("/planemanger/api_login.php")
    fun loginUser(@Body userLoginRequest: UserLoginRequest): Call<ModelLoginResponse>

}