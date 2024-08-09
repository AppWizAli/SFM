package com.hiskytech.selfmademarket.Ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
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
import java.io.File

class ActivitySignUp : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var apiInterFace: ApiService
    private var subscriptionPlan: String? = null
    private var isPlanSelected = false
    private val IMAGE_PICK_CODE = 1000
    private var clickedTextViewId: Int? = null

    // Variables to hold image URIs
    private var screenshotUri: Uri? = null
    private var idCardFrontUri: Uri? = null
    private var idCardBackUri: Uri? = null

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiInterFace = RetrofitClient.apiInterface

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

        // Plan selection listeners
        binding.cv.setOnClickListener {

            if (!isPlanSelected) {
                subscriptionPlan = "1"
                binding.startupPlanSaving.visibility = View.VISIBLE
                binding.cv1.isEnabled = false
                isPlanSelected = true
              
                Toast.makeText(this@ActivitySignUp, "Selected Plan: $subscriptionPlan", Toast.LENGTH_SHORT).show()
            }
        }

        binding.cv1.setOnClickListener {

            if (!isPlanSelected) {
                subscriptionPlan = "3"
                binding.plan2.visibility = View.VISIBLE
                binding.cv.isEnabled = false
                isPlanSelected = true

                binding.cv1.setBackgroundColor(R.color.white)
                Toast.makeText(this@ActivitySignUp, "Selected Plan: $subscriptionPlan", Toast.LENGTH_SHORT).show()
            }
        }

        // Payment method button
        binding.btnPayment.setOnClickListener {
            showPaymentDialog()
        }

        // Sign-up button listener
        binding.btn.setOnClickListener {
            val email = binding.email.text.toString().trim()
            val phone = binding.phone.text.toString().trim()
            val password = binding.pswrd.text.toString().trim()
            val name = binding.bn.text.toString().trim()
            val country = binding.bcountry.text.toString().trim()
            val district = binding.district.text.toString().trim()
            val city = binding.city.text.toString().trim()
            val postalCode = binding.postal.text.toString().trim()
            val transectionid = binding.tid.text.toString().trim()

            if (email.isEmpty() || phone.isEmpty() || password.isEmpty() || name.isEmpty() || country.isEmpty() || district.isEmpty() || postalCode.isEmpty() || subscriptionPlan == null) {
                Toast.makeText(this@ActivitySignUp, "Please fill all fields and select a plan", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val username = RequestBody.create("text/plain".toMediaTypeOrNull(), name)
            val uemail = RequestBody.create("text/plain".toMediaTypeOrNull(), email)
            val upassword = RequestBody.create("text/plain".toMediaTypeOrNull(), password)
            val uphone = RequestBody.create("text/plain".toMediaTypeOrNull(), phone)
            val ucountry = RequestBody.create("text/plain".toMediaTypeOrNull(), country)
            val udistrict = RequestBody.create("text/plain".toMediaTypeOrNull(), district)
            val ucity = RequestBody.create("text/plain".toMediaTypeOrNull(), city)
            val upostalcode = RequestBody.create("text/plain".toMediaTypeOrNull(), postalCode)
            val transid = RequestBody.create("text/plain".toMediaTypeOrNull(), transectionid)
            val uplanselect = RequestBody.create("text/plain".toMediaTypeOrNull(), subscriptionPlan!!)
            val screenshot = createPartFromUri(screenshotUri)
            val idCardFrontPic = createPartFromUri(idCardFrontUri)
            val idCardBackPic = createPartFromUri(idCardBackUri)

            // Log the request data
            Log.d("API Request", "Phone: $phone, Email: $email, Password: $password, Name: $name, Country: $country, District: $district, City: $city, Postal Code: $postalCode, Transaction ID: $transectionid, Plan: $subscriptionPlan")

            val call = apiInterFace.signUpUser(
                uphone,
                uemail,
                upassword,
                username,
                ucountry,
                ucity,
                udistrict,
                upostalcode,
                transid,
                uplanselect,
                screenshot,
                idCardBackPic,
                idCardFrontPic
            )

            call.enqueue(object : Callback<userModel> {
                override fun onResponse(call: Call<userModel>, response: Response<userModel>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            Log.d("API Success", "User data: ${it.users}")
                            Toast.makeText(
                                this@ActivitySignUp,
                                "Account Created Successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(Intent(this@ActivitySignUp, ActivityLogin::class.java))
                            finish()
                        } ?: run {
                            Log.e("API Error", "Response body is null")
                            Toast.makeText(
                                this@ActivitySignUp,
                                "Response body is null",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Log.e("API Error", "Response code: ${response.code()}, message: ${response.message()}")
                        response.errorBody()?.let { errorBody ->
                            Log.e("API Error", "Error body: ${errorBody.string()}")
                        }
                        Toast.makeText(
                            this@ActivitySignUp,
                            "Failed to fetch data: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<userModel>, t: Throwable) {
                    Log.e("API Failure", "API call failed: ${t.message}", t)
                    Toast.makeText(this@ActivitySignUp, "Failure: ${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        }
    }

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

    private fun createPartFromUri(uri: Uri?): MultipartBody.Part? {
        return uri?.let {
            val file = File(getRealPathFromURI(uri))
            val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            MultipartBody.Part.createFormData("file", file.name, requestFile)
        }
    }

    private fun getRealPathFromURI(uri: Uri): String {
        var filePath = ""
        val cursor = contentResolver.query(uri, null, null, null, null)
        if (cursor != null) {
            cursor.moveToFirst()
            val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            filePath = cursor.getString(index)
            cursor.close()
        }
        return filePath
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
                        binding.d.visibility = View.GONE
                        binding.dimg.visibility = View.VISIBLE
                        binding.dimg.setImageURI(imageUri)
                    }
                    R.id.js -> {
                        screenshotUri = imageUri
                        binding.js.visibility = View.GONE
                        binding.jsimg.visibility = View.VISIBLE
                        binding.jsimg.setImageURI(imageUri)
                    }
                    R.id.c -> {
                        idCardBackUri = imageUri
                        binding.c.visibility = View.GONE
                        binding.cimg.visibility = View.VISIBLE
                        binding.cimg.setImageURI(imageUri)
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
