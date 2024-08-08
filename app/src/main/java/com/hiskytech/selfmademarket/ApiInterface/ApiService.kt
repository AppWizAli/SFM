package com.hiskytech.selfmademarket.api

import com.hiskytech.selfmademarket.Model.userModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @Multipart
    @POST("/planemanger/subscribeapi.php")
    fun signUpUser(
        @Part("phone") phoneno: RequestBody,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part("name") username: RequestBody,
        @Part("country") countryname: RequestBody,
        @Part("city") city: RequestBody,
        @Part("district") district: RequestBody,
        @Part("postal_code") postalcode: RequestBody,
        @Part("transaction_id") transectionid: RequestBody,
        @Part transcriptScreenshot: MultipartBody.Part,
        @Part idCardBackPic: MultipartBody.Part,
        @Part idCardFrontPic: MultipartBody.Part
    ): Call<userModel>
}
