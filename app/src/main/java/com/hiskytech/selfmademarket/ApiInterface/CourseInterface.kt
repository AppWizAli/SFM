package com.hiskytech.selfmademarket.ApiInterface

import com.hiskytech.selfmademarket.Model.ModelCourses
import retrofit2.Call
import retrofit2.http.GET

interface CourseInterface {

    @GET("/planemanger/get-course.php")

    fun getCourses() : Call<ModelCourses>

}