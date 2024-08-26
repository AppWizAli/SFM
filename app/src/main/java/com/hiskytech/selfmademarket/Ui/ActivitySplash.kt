package com.hiskytech.selfmademarket.Ui

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.gson.Gson
import com.hiskytech.selfmademarket.ApiInterface.BitCoinInterface
import com.hiskytech.selfmademarket.ApiInterface.CommentsInterface
import com.hiskytech.selfmademarket.ApiInterface.CourseInterface
import com.hiskytech.selfmademarket.ApiInterface.ForexInterface
import com.hiskytech.selfmademarket.ApiInterface.UserLoginRequest
import com.hiskytech.selfmademarket.ApiInterface.logininterface
import com.hiskytech.selfmademarket.Model.DataX
import com.hiskytech.selfmademarket.Model.ModelBitCoin
import com.hiskytech.selfmademarket.Model.ModelCommint
import com.hiskytech.selfmademarket.Model.ModelCourses
import com.hiskytech.selfmademarket.Model.ModelForex
import com.hiskytech.selfmademarket.Model.ModelLoginResponse2
import com.hiskytech.selfmademarket.Model.ModelNotification
import com.hiskytech.selfmademarket.Model.ModelStatusCheck
import com.hiskytech.selfmademarket.Model.NotificationBuilder
import com.hiskytech.selfmademarket.Model.RetrofitBuilder
import com.hiskytech.selfmademarket.Model.loginBuilder
import com.hiskytech.selfmademarket.R
import com.hiskytech.selfmademarket.Repo.MySharedPref
import com.hiskytech.selfmademarket.databinding.ActivitySplashBinding
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.CountDownLatch

class ActivitySplash : AppCompatActivity() {
    private lateinit var apiInterFace: logininterface
    private lateinit var binding: ActivitySplashBinding
    private lateinit var mySharedPref: MySharedPref
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = true
        window.statusBarColor = ContextCompat.getColor(this, R.color.splash)

        val layoutParams = binding.main.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.setMargins(-2, -2, -2, -2)
        binding.main.layoutParams = layoutParams

        mySharedPref = MySharedPref(this@ActivitySplash)


        fetchComments()









        apiInterFace = loginBuilder.apiInterface

        val animator =
            ObjectAnimator.ofFloat(findViewById<ImageView>(R.id.img), "translationX", -30f, 30f)
                .apply {
                    duration = 1500 // 1 second for each back-and-forth
                    repeatCount = ObjectAnimator.INFINITE // Loop the animation indefinitely
                    repeatMode = ObjectAnimator.REVERSE // Reverse animation on repeat
                    start()
                }

       // checkStatus()
    }


    private fun checkStatus() {

        var id= RequestBody.create("text/plain".toMediaType(), mySharedPref.getUserModel()?.id.toString())
        if(mySharedPref.isUserLoggedIn())
        {
            id = RequestBody.create("text/plain".toMediaType(), mySharedPref.getUserModel()?.id.toString())

        }
        else if(mySharedPref.isUserSignUp())
        {
            id = RequestBody.create("text/plain".toMediaType(), mySharedPref.getUserId())

        }
        else if(mySharedPref.getUserId()==mySharedPref.getUserModel()?.id.toString())
        { id = RequestBody.create("text/plain".toMediaType(), mySharedPref.getUserModel()?.id.toString())

        }
        else
        {
            id = RequestBody.create("text/plain".toMediaType(), mySharedPref.getUserModel()?.id.toString())

        }




        apiInterFace.checkStatus(id).enqueue(object :
            Callback<ModelStatusCheck> {
            override fun onResponse(
                call: Call<ModelStatusCheck>,
                response: Response<ModelStatusCheck>
            ) {


                val userResponse = response.body()

                if (response.isSuccessful && userResponse != null) {

                    if (userResponse.is_verified == 1 && mySharedPref.isUserLoggedIn() && userResponse.is_subscription_valid) {
                        //   Toast.makeText(this@ActivitySplash, "1", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@ActivitySplash, MainActivity::class.java))
                        finish()
                    } else if (userResponse.is_verified == 1 && userResponse.is_subscription_valid) {
                        // Toast.makeText(this@ActivitySplash, "1", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@ActivitySplash, ActivityLogin::class.java))
                        finish()
                    } else if (userResponse.is_verified == 0 && mySharedPref.isUserSignUp()) {
                        // Toast.makeText(this@ActivitySplash, "2", Toast.LENGTH_SHORT).show()

                        startActivity(Intent(this@ActivitySplash, ActivityVerification::class.java))
                        finish()
                    } else if(!mySharedPref.isUserSignUp() && !mySharedPref.isUserLoggedIn()){
                        // Toast.makeText(this@ActivitySplash, "4", Toast.LENGTH_SHORT).show()

                        startActivity(Intent(this@ActivitySplash, ActivitySignUp::class.java))
                        finish()
                    }else if (!userResponse.is_subscription_valid) {
                        // Toast.makeText(this@ActivitySplash, "3", Toast.LENGTH_SHORT).show()

                        startActivity(Intent(this@ActivitySplash, ActivityRenewSub::class.java))
                        finish()
                    }


                }
            }

            override fun onFailure(call: Call<ModelStatusCheck>, t: Throwable) {

                Toast.makeText(this@ActivitySplash, "Something went wrong!!", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }


    private fun fetchForexData() {
        val forexInterface = RetrofitBuilder.getInstance().create(ForexInterface::class.java)
        val call = forexInterface.getForex()

        call.enqueue(object : Callback<ModelForex> {
            override fun onResponse(call: Call<ModelForex>, response: Response<ModelForex>) {
                if (response.isSuccessful) {
                    val forexData = response.body()?.data ?: emptyList()
                    mySharedPref.storeForexInPref(forexData)
                    checkStatus()
                }
            }

            override fun onFailure(call: Call<ModelForex>, t: Throwable) {
                // Handle failure
            }
        })
    }


    private fun fetchComments() {
        val apiInterface = RetrofitBuilder.getInstance().create(CommentsInterface::class.java)
        val call = apiInterface.getCommints()

        call.enqueue(object : Callback<ModelCommint> {
            override fun onResponse(call: Call<ModelCommint>, response: Response<ModelCommint>) {
                if (response.isSuccessful) {
                    val comments = response.body()?.comments ?: emptyList()
                    mySharedPref.storeCommentsInPref(comments)
                    fetchCourses()

                }
            }

            override fun onFailure(call: Call<ModelCommint>, t: Throwable) {
                // Handle failure
            }
        })
    }

    private fun fetchCourses() {
        val apiInterface = RetrofitBuilder.getInstance().create(CourseInterface::class.java)
        val call = apiInterface.getCourses()

        call.enqueue(object : Callback<ModelCourses> {
            override fun onResponse(call: Call<ModelCourses>, response: Response<ModelCourses>) {
                if (response.isSuccessful) {
                    val courses = response.body() ?: emptyList()
                    mySharedPref.storeCoursesInPref(courses)
                    fetchBitCoins()

                }
            }

            override fun onFailure(call: Call<ModelCourses>, t: Throwable) {
                // Handle failure
            }
        })
    }

    private fun fetchBitCoins() {
        val apiInterface = RetrofitBuilder.getInstance().create(BitCoinInterface::class.java)
        val call = apiInterface.getBitCoin()

        call.enqueue(object : Callback<ModelBitCoin> {
            override fun onResponse(call: Call<ModelBitCoin>, response: Response<ModelBitCoin>) {
                if (response.isSuccessful) {
                    val bitCoins = response.body()?.data ?: emptyList()
                    mySharedPref.storeBitCoinsInPref(bitCoins)
                    fetchForexData()
                }
            }

            override fun onFailure(call: Call<ModelBitCoin>, t: Throwable) {
                // Handle failure
            }
        })
    }



}