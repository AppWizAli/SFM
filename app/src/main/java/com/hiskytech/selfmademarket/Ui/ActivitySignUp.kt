package com.hiskytech.selfmademarket.Ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.hiskytech.selfmademarket.Model.Data
import com.hiskytech.selfmademarket.Model.ModelSubscription
import com.hiskytech.selfmademarket.R
import com.hiskytech.selfmademarket.api.RetrofitClient
import com.hiskytech.selfmademarket.databinding.ActivitySignUpBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivitySignUp : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    private lateinit var accountTitle : String
    private lateinit var accountNumber : String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Adding Payment Method

        binding.btnPayment.setOnClickListener(){

            showPaymentDialog()

        }



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


    private fun showPaymentDialog() {

        val dialog = Dialog(this@ActivitySignUp)

        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.setContentView(R.layout.paymentmethod_dialog)

        val btnBinance = dialog.findViewById<CardView>(R.id.binance)
        val btnWise = dialog.findViewById<CardView>(R.id.wise)
        val btnPayonner = dialog.findViewById<CardView>(R.id.payoneer)
        val btnEasyPaisa = dialog.findViewById<CardView>(R.id.easypaisa)
        val btnJazzCash = dialog.findViewById<CardView>(R.id.jazzcash)
        val btnBank = dialog.findViewById<CardView>(R.id.banktransfer)

        btnBinance?.setOnClickListener {
            accountTitle = "Binance"
            accountNumber = "1111111"
            val drawableId = R.drawable.binance
            showPaymentDetailsDialog(drawableId)
        }
        btnWise?.setOnClickListener {
            accountTitle = "Wise"
            accountNumber = "22222"
            val drawableId = R.drawable.wise
            showPaymentDetailsDialog(drawableId)
        }
        btnPayonner?.setOnClickListener {
            accountTitle = "Payonner"
            accountNumber = "3333333"
            val drawableId = R.drawable.payoneer
            showPaymentDetailsDialog(drawableId)
        }
        btnEasyPaisa?.setOnClickListener {
            accountTitle = "Easy Paisa"
            accountNumber = "4444444"
            val drawableId = R.drawable.img_2
            showPaymentDetailsDialog(drawableId)
        }
        btnJazzCash?.setOnClickListener {
            accountTitle = "Jazz Cash"
            accountNumber = "5555555"
            val drawableId = R.drawable.jazzcash
            showPaymentDetailsDialog(drawableId)
        }
        btnBank?.setOnClickListener {
            accountTitle = "Bank Transfer"
            accountNumber = "666666"
            val drawableId = R.drawable.banktransfer
            showPaymentDetailsDialog(drawableId)
        }
        dialog.create()
        dialog.show()
    }

    private fun showPaymentDetailsDialog(drawableId: Int) {

        val dialog = Dialog(this@ActivitySignUp)

        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)

        dialog.setContentView(R.layout.account_details_dialog)

        val detailTittle = dialog.findViewById<TextView>(R.id.tvTittle)
        val detailNumber = dialog.findViewById<TextView>(R.id.tvAccountNumber)
        val imgCopy = dialog.findViewById<ImageView>(R.id.imgCopy)
        val imgBack = dialog.findViewById<ImageView>(R.id.imgBack)
        val imgPaymentMethod = dialog.findViewById<ImageView>(R.id.imgPaymentMethod)

        detailTittle.text = accountTitle
        detailNumber.text = accountNumber
        imgPaymentMethod.setImageResource(drawableId)

        imgCopy.setOnClickListener {
            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("Account Number", detailNumber.text.toString())
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show()
        }

        imgBack.setOnClickListener {
            dialog.dismiss()
        }

        dialog.create()
        dialog.show()
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
