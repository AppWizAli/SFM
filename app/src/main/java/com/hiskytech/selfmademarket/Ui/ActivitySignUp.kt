package com.hiskytech.selfmademarket.Ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hiskytech.selfmademarket.Model.Data
import com.hiskytech.selfmademarket.Model.ModelSubscription
import com.hiskytech.selfmademarket.api.RetrofitClient
import com.hiskytech.selfmademarket.databinding.ActivitySignUpBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivitySignUp : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btn.setOnClickListener {
            val email = binding.email.text.toString().trim()
            val phone = binding.phone.text.toString().trim()
            val password = binding.pswrd.text.toString().trim()
            val name = binding.bn.text.toString().trim()
            val country = binding.bcountry.text.toString().trim()
            val district = binding.district.text.toString().trim()
            val city = binding.city.text.toString().trim()
            val postalCode = binding.postal.text.toString().trim()
            val transactionId = binding.tid.text.toString().trim()
            val transcriptScreenshot = binding.js.text.toString().trim()
            val idCardFrontPic = binding.d.text.toString().trim()
            val idCardBackPic = binding.c.text.toString().trim()
            val paymentMethod = "your_payment_method" // adjust accordingly
            val planSelect = "your_plan" // adjust accordingly

            if (email.isNotEmpty() && phone.isNotEmpty() && password.isNotEmpty()) {
                val data = Data(
                    city = city,
                    country = country,
                    created_at = "", // Set appropriately
                    district = district,
                    email = email,
                    id = 0, // Server-generated
                    id_card_back_pic = idCardBackPic,
                    id_card_front_pic = idCardFrontPic,
                    is_verified = 0, // Server-generated
                    name = name,
                    payment_method = paymentMethod,
                    phone = phone,
                    plan_select = planSelect,
                    postal_code = postalCode,
                    subscription_end_date = "", // Server-generated
                    subscription_start_date = "", // Server-generated
                    transaction_id = transactionId,
                    transcript_screenshot = transcriptScreenshot,
                    user_image = "" // Add if required
                )
                signUp(data)
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signUp(data: Data) {
        RetrofitClient.instance.signUp(data).enqueue(object : Callback<ModelSubscription> {
            override fun onResponse(call: Call<ModelSubscription>, response: Response<ModelSubscription>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    Toast.makeText(this@ActivitySignUp, result?.message ?: "Success", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("SignUpError", "Response code: ${response.code()}")
                    Log.e("SignUpError", "Response message: ${response.message()}")
                    response.errorBody()?.let { errorBody ->
                        Log.e("SignUpError", "Error body: ${errorBody.string()}")
                    }
                    Toast.makeText(this@ActivitySignUp, "Sign Up Failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ModelSubscription>, t: Throwable) {
                Log.e("SignUpError", "Failure message: ${t.message}", t)
                Toast.makeText(this@ActivitySignUp, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}
