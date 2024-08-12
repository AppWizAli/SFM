package com.hiskytech.selfmademarket.Ui

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hiskytech.selfmademarket.ApiInterface.UserLoginRequest
import com.hiskytech.selfmademarket.ApiInterface.logininterface
import com.hiskytech.selfmademarket.Model.ModeluserResponse
import com.hiskytech.selfmademarket.Model.RetrofitBuilder
import com.hiskytech.selfmademarket.Model.loginBuilder
import com.hiskytech.selfmademarket.R
import com.hiskytech.selfmademarket.databinding.ActivityLoginBinding
import com.qamar.curvedbottomnaviagtion.log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivityLogin : AppCompatActivity() {
    private lateinit var apiInterFace:logininterface
private lateinit var binding: ActivityLoginBinding
    private lateinit var dialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {

        apiInterFace = loginBuilder.apiInterface
        super.onCreate(savedInstanceState)
       binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.login.setOnClickListener {


            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this@ActivityLogin, "Please enter both email and password", Toast.LENGTH_SHORT).show()
            }

    }}
        private fun loginUser(email: String, password: String) {
            showAnimation()
            val userLoginRequest = UserLoginRequest(email, password)

            apiInterFace.loginUser(userLoginRequest).enqueue(object : Callback<ModeluserResponse> {
                override fun onResponse(call: Call<ModeluserResponse>, response: Response<ModeluserResponse>) {
                  closeAnimation()
                    if (response.isSuccessful) {
                        val userResponse = response.body()
                        if (userResponse != null && userResponse.message == "Login successful") {
                            val userData = userResponse.data
                            if (userData.is_verified <= 1) {
                                Toast.makeText(this@ActivityLogin, "Login Successful!!", Toast.LENGTH_SHORT).show()

                                startActivity(Intent(this@ActivityLogin, MainActivity::class.java))
                                finishAffinity()
                            } else {
                                Toast.makeText(this@ActivityLogin, "Already Logged In on another device", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this@ActivityLogin, "Login Failed: ${userResponse?.message}", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@ActivityLogin, "Login Failed", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ModeluserResponse>, t: Throwable) {
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