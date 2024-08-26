package com.hiskytech.selfmademarket.Repo

import android.content.Context
import com.google.gson.Gson
import com.hiskytech.selfmademarket.Model.CommentX
import com.hiskytech.selfmademarket.Model.DataX
import com.hiskytech.selfmademarket.Model.DataXX
import com.hiskytech.selfmademarket.Model.DataXXXX
import com.hiskytech.selfmademarket.Model.ModelCoursesItem

class MySharedPref(context:Context) {

    var shared=context.getSharedPreferences("MySharedPref",Context.MODE_PRIVATE)
    var editor=shared.edit()




    fun putUserLoggedIn()
    {
        editor.putBoolean("IsUserLoggedIn",true)
        editor.apply()
    }
    fun putUserLoggedOut()
    {
        editor.putBoolean("IsUserLoggedIn",false)
        editor.apply()
    }

    fun isUserLoggedIn():Boolean
    {
        return  shared.getBoolean("IsUserLoggedIn",false)
    }



    fun putSignUpUser()
    {
        editor.putBoolean("IsUserSingUp",true)
        editor.apply()
    }

    fun isUserSignUp():Boolean
    {
        return  shared.getBoolean("IsUserSingUp",false)
    }

    fun putRenewPlan()
    {
        editor.putBoolean("IsRenew",true)
        editor.apply()
    }

    fun isRenewPlan():Boolean
    {
        return  shared.getBoolean("IsRenew",false)
    }


    fun saveUserId(id:String)
    {
        editor.putString("UserId",id)
        editor.apply()
    }
    fun getUserId():String
    {
        return   shared.getString("UserId","")!!

    }



    fun saveUserModel(modelUser:DataXXXX)
    {
        val gson = Gson()
        val jsonString = gson.toJson(modelUser)
        editor.putString("user_model", jsonString)
        editor.apply()
    }

    fun getUserModel(): DataXXXX? {

        val jsonString = shared.getString("user_model", null)

        return if (jsonString != null) {

            val gson = Gson()
            gson.fromJson(jsonString, DataXXXX::class.java)
        } else {
            null
        }
    }
  fun storeCommentsInPref(comments: List<CommentX>) {

        // Convert list to JSON and store in SharedPreferences
        editor.putString("comments", Gson().toJson(comments))
        editor.apply()
    }

     fun storeCoursesInPref(courses: List<ModelCoursesItem>) {

        // Convert list to JSON and store in SharedPreferences
        editor.putString("courses", Gson().toJson(courses))
        editor.apply()
    }

   fun storeBitCoinsInPref(bitCoins: List<DataX>) {

        // Convert list to JSON and store in SharedPreferences
        editor.putString("bitCoins", Gson().toJson(bitCoins))
        editor.apply()
    }

   fun storeForexInPref(forexData: List<DataXX>) {

        editor.putString("forexData", Gson().toJson(forexData))
        editor.apply()
    }




     fun retrieveStoredComments(): List<CommentX> {
        val commentsJson = shared.getString("comments", null)
        return if (commentsJson != null) {
            Gson().fromJson(commentsJson, Array<CommentX>::class.java).toList()
        } else {
            emptyList()
        }
    }

     fun retrieveStoredCourses(): List<ModelCoursesItem> {
        val coursesJson = shared.getString("courses", null)
        return if (coursesJson != null) {
            Gson().fromJson(coursesJson, Array<ModelCoursesItem>::class.java).toList()
        } else {
            emptyList()
        }
    }

fun retrieveStoredBitCoins(): List<DataX> {
        val bitCoinsJson = shared.getString("bitCoins", null)
        return if (bitCoinsJson != null) {
            Gson().fromJson(bitCoinsJson, Array<DataX>::class.java).toList()
        } else {
            emptyList()
        }
    }


    fun retrieveStoredForexData(): List<DataXX> {
        val forexJson = shared.getString("forexData", null)
        return if (forexJson != null) {
            Gson().fromJson(forexJson, Array<DataXX>::class.java).toList()
        } else {
            emptyList()
        }
    }


}