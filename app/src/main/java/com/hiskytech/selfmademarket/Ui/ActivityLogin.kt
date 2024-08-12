package com.hiskytech.selfmademarket.Ui

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hiskytech.selfmademarket.ApiInterface.UserLoginRequest
import com.hiskytech.selfmademarket.ApiInterface.logininterface
import com.hiskytech.selfmademarket.Model.modeluserlogin
import com.hiskytech.selfmademarket.Model.loginBuilder
import com.hiskytech.selfmademarket.R
import com.hiskytech.selfmademarket.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivityLogin : AppCompatActivity() {
    private lateinit var apiInterFace: logininterface
    private lateinit var binding: ActivityLoginBinding
    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize API interface
        apiInterFace = loginBuilder.apiInterface

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

        val userLoginRequest = UserLoginRequest(email, password)
        apiInterFace.loginUser(userLoginRequest).enqueue(object : Callback<modeluserlogin> {
            override fun onResponse(call: Call<modeluserlogin>, response: Response<modeluserlogin>) {
                closeAnimation()
                val userResponse = response.body()
                if (response.isSuccessful && userResponse != null) {
                    Toast.makeText(this@ActivityLogin, "Response: ${userResponse.message}", Toast.LENGTH_SHORT).show()
                    if (userResponse.message == "Login successful") {
                        Toast.makeText(this@ActivityLogin, "Login Successful!!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@ActivityLogin, MainActivity::class.java))
                        finishAffinity()
                    } else {
                        Toast.makeText(this@ActivityLogin, "Login Failed: ${userResponse.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@ActivityLogin, "Login Failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }


            override fun onFailure(call: Call<modeluserlogin>, t: Throwable) {
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
