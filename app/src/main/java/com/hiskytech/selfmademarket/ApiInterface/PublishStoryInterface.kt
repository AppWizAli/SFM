package com.hiskytech.selfmademarket.ApiInterface

import com.hiskytech.selfmademarket.Model.ModelStoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.Part
import retrofit2.http.POST

interface PublishStoryInterface {
    @Multipart
    @POST("/planemanger/api_comment.php")
    fun publishStory(
        @Part("user_name") user_name: RequestBody,
        @Part("description") description: RequestBody,
    ): Call<ModelStoryResponse>
}
