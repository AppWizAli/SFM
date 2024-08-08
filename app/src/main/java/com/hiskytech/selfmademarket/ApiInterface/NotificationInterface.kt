package com.hiskytech.selfmademarket.ApiInterface

import com.hiskytech.selfmademarket.Model.ModelNotification
import retrofit2.Call
import retrofit2.http.GET

interface NotificationInterface {

    @GET("/planemanger/get_notifications.php")
    fun getNotification() : Call<ModelNotification>
}