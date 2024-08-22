package com.hiskytech.selfmademarket.Ui

import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.hiskytech.selfmademarket.ApiInterface.UserLoginRequest
import com.hiskytech.selfmademarket.ApiInterface.logininterface
import com.hiskytech.selfmademarket.Model.ModelLoginResponse2
import com.hiskytech.selfmademarket.Model.loginBuilder
import com.hiskytech.selfmademarket.R
import com.hiskytech.selfmademarket.Repo.MySharedPref
import com.hiskytech.selfmademarket.databinding.ActivityLoginBinding
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.internal.http.hasBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivityLogin : AppCompatActivity() {
    private lateinit var apiInterFace: logininterface
    private lateinit var binding: ActivityLoginBinding
    private lateinit var mySharedPref: MySharedPref
    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
mySharedPref= MySharedPref(this@ActivityLogin)
        // Initialize API interface
        apiInterFace = loginBuilder.apiInterface

binding.btnSignUp.setOnClickListener()
{
    startActivity(Intent(this@ActivityLogin,ActivitySignUp::class.java))
}


        binding.login.setOnClickListener {


            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this@ActivityLogin, "Please enter both email and password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        showAnimation()
        val emailPart = RequestBody.create("text/plain".toMediaType(), email)
        val passwordPart = RequestBody.create("text/plain".toMediaType(), password)

        val userLoginRequest = UserLoginRequest(email, password)
        apiInterFace.logininterface(emailPart,passwordPart).enqueue(object : Callback<ModelLoginResponse2> {
            override fun onResponse(call: Call<ModelLoginResponse2>, response: Response<ModelLoginResponse2>) {
                closeAnimation()
                val userResponse = response.body()
                if (response.isSuccessful && userResponse != null) {
                    Toast.makeText(this@ActivityLogin, "Response: ${userResponse.message}", Toast.LENGTH_SHORT).show()
mySharedPref.putUserLoggedIn()
                    if (userResponse.message.toString().equals("Login successful") ){
                        Toast.makeText(this@ActivityLogin, "Login Successful!!", Toast.LENGTH_SHORT).show()

                        mySharedPref.saveUserModel(response.body()?.data!!)


                        startActivity(Intent(this@ActivityLogin, MainActivity::class.java))
                        finishAffinity()
                    } else {
                        Toast.makeText(this@ActivityLogin, "Login Failed: ${userResponse.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@ActivityLogin, "Login Failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ModelLoginResponse2>, t: Throwable) {
                closeAnimation()
                Toast.makeText(this@ActivityLogin, "Something went wrong!!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showAnimation() {
        dialog = Dialog(this)
        dialog.setContentView(R.layout.loadingdialog)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    private fun closeAnimation() {
        dialog.dismiss()
    }
}
