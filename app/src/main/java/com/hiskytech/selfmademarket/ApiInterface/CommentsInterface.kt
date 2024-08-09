package com.hiskytech.selfmademarket.ApiInterface

import com.hiskytech.selfmademarket.Model.ModelCommint
import retrofit2.Call
import retrofit2.http.GET

interface CommentsInterface {
    @GET("/planemanger/fetch_approved_comments.php")
    fun getCommints(): Call<ModelCommint>
}
