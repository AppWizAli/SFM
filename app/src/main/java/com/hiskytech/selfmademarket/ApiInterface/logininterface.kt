package com.hiskytech.selfmademarket.ApiInterface

import com.hiskytech.selfmademarket.Model.ModelLoginResponse2
import com.hiskytech.selfmademarket.Model.ModelResponsePassword
import com.hiskytech.selfmademarket.Model.ModelStatusCheck
import com.hiskytech.selfmademarket.Model.ModelStoryResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

data class UserLoginRequest(val email: String, val password: String)

interface logininterface {

    @Multipart
    @POST("/planemanger/api_login.php")
    fun logininterface(
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
    ): Call<ModelLoginResponse2>

    @Multipart
    @POST("/planemanger/update_password_api.php")
    fun updatePassowrd(
        @Part("id") id: RequestBody,
        @Part("new_password") new_password: RequestBody,
    ): Call<ModelResponsePassword>


    @Multipart
    @POST("/planemanger/Is_verified.php")
    fun checkStatus(
        @Part("id") id: RequestBody,
    ): Call<ModelStatusCheck>





}