package com.hiskytech.selfmademarket.api

import com.hiskytech.selfmademarket.Model.ModelResponsePassword
import com.hiskytech.selfmademarket.Model.ModelSignupResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface SignupInterface {

    @Multipart
    @POST("/planemanger/subscribeapi.php")
    fun signUpUser(
        @Part("email") email: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("password") password: RequestBody,
        @Part("name") name: RequestBody,
        @Part("country") country: RequestBody,
        @Part("city") city: RequestBody,
        @Part("district") district: RequestBody,
        @Part("postal_code") postal_code: RequestBody,
        @Part("transaction_id") transaction_id: RequestBody,
        @Part("plan_select") plan_select: RequestBody,
        @Part("payment_method") payment_method: RequestBody,
       @Part transcript_screenshot: MultipartBody.Part,
       @Part id_card_front_pic: MultipartBody.Part,
       @Part id_card_back_pic: MultipartBody.Part,
       @Part user_image: MultipartBody.Part,
    //  @Part id_card_front_pic: MultipartBody.Part?,
  //    @Part("user_image") user_image: MultipartBody.Part?
      //@Part id_card_back_pic: MultipartBody.Part?
    ): Call<ModelSignupResponse>

    @Multipart
    @POST("/planemanger/upgrade_plan.php")
    fun RenewPlanUser(
        @Part("transaction_id") transaction_id: RequestBody,

        @Part("plan_select") plan_select: RequestBody,
       @Part transcript_screenshot: MultipartBody.Part,
        @Part("id") id: RequestBody,
    ): Call<ModelResponsePassword>

}