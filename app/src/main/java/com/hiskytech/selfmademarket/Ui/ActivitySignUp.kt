package com.hiskytech.selfmademarket.Ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.hiskytech.selfmademarket.Model.ModelSignupResponse
import com.hiskytech.selfmademarket.Model.SignupBuilder.apiInterface
import com.hiskytech.selfmademarket.Model.userModel
import com.hiskytech.selfmademarket.R
import com.hiskytech.selfmademarket.api.ApiService
import com.hiskytech.selfmademarket.api.RetrofitClient
import com.hiskytech.selfmademarket.databinding.ActivitySignUpBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File

class ActivitySignUp : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private var subscriptionPlan: String? = null
    private var isPlanSelected = false
    private val IMAGE_PICK_CODE = 1000
    private var clickedTextViewId: Int? = null

    private var screenshotUri: Uri? = null
    private var idCardFrontUri: Uri? = null
    private var idCardBackUri: Uri? = null

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Image selection listeners
        binding.js.setOnClickListener {
            clickedTextViewId = it.id
            pickImageFromGallery()
        }
        binding.d.setOnClickListener {
            clickedTextViewId = it.id
            pickImageFromGallery()
        }
        binding.c.setOnClickListener {
            clickedTextViewId = it.id
            pickImageFromGallery()
        }

        binding.cv.setOnClickListener {
            if (!isPlanSelected) {
                subscriptionPlan = "1"
                binding.startupPlanSaving.visibility = View.VISIBLE
                binding.cv1.isEnabled = false
                isPlanSelected = true


                binding.cv1.setCardBackgroundColor(ContextCompat.getColor(this, R.color.hint_color))

                Toast.makeText(this@ActivitySignUp, "Selected Plan: $subscriptionPlan", Toast.LENGTH_SHORT).show()
            }
        }

        binding.cv1.setOnClickListener {
            if (!isPlanSelected) {
                subscriptionPlan = "3"
                binding.plan2.visibility = View.VISIBLE
                binding.cv.isEnabled = false
                isPlanSelected = true


                binding.cv.setCardBackgroundColor(ContextCompat.getColor(this, R.color.hint_color))

                Toast.makeText(this@ActivitySignUp, "Selected Plan: $subscriptionPlan", Toast.LENGTH_SHORT).show()
            }
        }


        // Payment method button
        binding.btnPayment.setOnClickListener {
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

            // Check if all fields are filled
       /*     if (email.isEmpty() || phone.isEmpty() || password.isEmpty() || name.isEmpty() || country.isEmpty() || district.isEmpty() || postalCode.isEmpty() || subscriptionPlan == null) {
                Toast.makeText(this@ActivitySignUp, "Please fill all fields and select a plan", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
*/
            // Create request body parts
            val requestBodyMap = mapOf(
                "email" to RequestBody.create("text/plain".toMediaTypeOrNull(), email),
                "phone" to RequestBody.create("text/plain".toMediaTypeOrNull(), phone),
                "password" to RequestBody.create("text/plain".toMediaTypeOrNull(), password),
                "name" to RequestBody.create("text/plain".toMediaTypeOrNull(), name),
                "country" to RequestBody.create("text/plain".toMediaTypeOrNull(), country),
                "city" to RequestBody.create("text/plain".toMediaTypeOrNull(), city),
                "district" to RequestBody.create("text/plain".toMediaTypeOrNull(), district),
                "postal_code" to RequestBody.create("text/plain".toMediaTypeOrNull(), postalCode),
                "transaction_id" to RequestBody.create("text/plain".toMediaTypeOrNull(), transactionId),
                "plan_select" to RequestBody.create("text/plain".toMediaTypeOrNull(), subscriptionPlan!!),
                "payment_method" to RequestBody.create("text/plain".toMediaTypeOrNull(), "YourPaymentMethodHere")
            )

            // Create MultipartBody.Part for the screenshot
            val transcriptScreenshotPart = createPartFromUri(this,screenshotUri, "transcript_screenshot")
   val front_pic = createPartFromUri(this,idCardFrontUri, "id_card_front_pic")
   val back_pic = createPartFromUri(this,idCardBackUri, "id_card_back_pic")

            // Make the API call
            val call = apiInterface.signUpUser(
                requestBodyMap["email"]!!,
                requestBodyMap["phone"]!!,
                requestBodyMap["password"]!!,
                requestBodyMap["name"]!!,
                requestBodyMap["country"]!!,
                requestBodyMap["city"]!!,
                requestBodyMap["district"]!!,
                requestBodyMap["postal_code"]!!,
                requestBodyMap["transaction_id"]!!,
                requestBodyMap["plan_select"]!!,
                requestBodyMap["payment_method"]!!,
                transcriptScreenshotPart,
                front_pic,
                back_pic
            )

            // Handle the API response
            call.enqueue(object : Callback<ModelSignupResponse> {
                override fun onResponse(call: Call<ModelSignupResponse>, response: Response<ModelSignupResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            Log.d("API Success", "Message: ${it.message}, Status: ${it.status}")
                            Toast.makeText(this@ActivitySignUp, "Account Created Successfully", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@ActivitySignUp, ActivityLogin::class.java))
                            finish()
                        } ?: run {
                            Log.e("API Error", "Response body is null")
                            Toast.makeText(this@ActivitySignUp, "Response body is null", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Log.e("API Error", "Response code: ${response.code()}, message: ${response.message()}")
                        Toast.makeText(this@ActivitySignUp, "Failed to fetch data: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ModelSignupResponse>, t: Throwable) {
                    Log.e("API Failure", "API call failed: ${t.message}", t)
                    Toast.makeText(this@ActivitySignUp, "Failure: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })

}}

        private fun showPaymentDialog() {
        val dialog = Dialog(this@ActivitySignUp)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.paymentmethod_dialog)

        val btnBinance = dialog.findViewById<CardView>(R.id.binance)
        val btnWise = dialog.findViewById<CardView>(R.id.wise)
        val btnPayonner = dialog.findViewById<CardView>(R.id.payoneer)
        val btnEasyPaisa = dialog.findViewById<CardView>(R.id.easypaisa)
        val btnJazzCash = dialog.findViewById<CardView>(R.id.jazzcash)
        val btnBank = dialog.findViewById<CardView>(R.id.banktransfer)

        btnBinance.setOnClickListener {
            showPaymentDetailsDialog("Binance", "1111111", R.drawable.binance)
        }
        btnWise.setOnClickListener {
            showPaymentDetailsDialog("Wise", "22222", R.drawable.wise)
        }
        btnPayonner.setOnClickListener {
            showPaymentDetailsDialog("Payonner", "3333333", R.drawable.payoneer)
        }
        btnEasyPaisa.setOnClickListener {
            showPaymentDetailsDialog("Easy Paisa", "4444444", R.drawable.img_2)
        }
        btnJazzCash.setOnClickListener {
            showPaymentDetailsDialog("Jazz Cash", "5555555", R.drawable.jazzcash)
        }
        btnBank.setOnClickListener {
            showPaymentDetailsDialog("Bank Transfer", "666666", R.drawable.banktransfer)
        }

        dialog.show()
    }

    private fun showPaymentDetailsDialog(accountTitle: String, accountNumber: String, drawableId: Int) {
        val dialog = Dialog(this@ActivitySignUp)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.setContentView(R.layout.account_details_dialog)

        val detailTitle = dialog.findViewById<TextView>(R.id.tvTittle)
        val detailNumber = dialog.findViewById<TextView>(R.id.tvAccountNumber)
        val imgCopy = dialog.findViewById<ImageView>(R.id.imgCopy)
        val imgBack = dialog.findViewById<ImageView>(R.id.imgBack)
        val imgPaymentMethod = dialog.findViewById<ImageView>(R.id.imgPaymentMethod)

        detailTitle.text = accountTitle
        detailNumber.text = accountNumber
        imgPaymentMethod.setImageResource(drawableId)

        imgCopy.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Account Number", accountNumber)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show()
        }
        imgBack.setOnClickListener {
            dialog.dismiss()
        }

        val window = dialog.window
        if (window != null) {
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(window.attributes)
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
            window.attributes = layoutParams
        }

        dialog.show()
    }
    private fun compressImage(uri: Uri): ByteArray {
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
        val outputStream = ByteArrayOutputStream()
        // Compress bitmap to JPEG format with 75% quality
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream)
        return outputStream.toByteArray()
    }
    private fun createPartFromUri(context: Context, uri: Uri?, partName: String): MultipartBody.Part? {
        return uri?.let {
            try {
                // Use the compressImage function
                val byteArray = compressImage(uri)
                val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), byteArray)
                MultipartBody.Part.createFormData(partName, "image.jpg", requestFile)
            } catch (e: Exception) {
                Log.e("Image Error", "Error creating MultipartBody.Part from Uri", e)
                null
            }
        }
    }




    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            val imageUri = data?.data
            if (imageUri != null) {
                when (clickedTextViewId) {
                    R.id.d -> {
                        idCardFrontUri = imageUri


                        binding.d.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_image_24, 0, 0, 0) // Change the icon
                    }
                    R.id.js -> {
                        screenshotUri = imageUri

                        binding.js.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_image_24, 0, 0, 0) // Change the icon
                    }
                    R.id.c -> {
                        idCardBackUri = imageUri


                        binding.c.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_image_24, 0, 0, 0) // Change the icon
                    }
                }
            } else {
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this, "Image selection canceled", Toast.LENGTH_SHORT).show()
        }
    }


}
