package com.hiskytech.selfmademarket.ApiInterface

import com.hiskytech.selfmademarket.Model.ModelComments
import retrofit2.Call
import retrofit2.http.GET

interface CommentsInterface {
    @GET("planemanger/view_community.php")
    fun getCommints(): Call<ModelComments>
}
