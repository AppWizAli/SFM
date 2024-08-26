package com.hiskytech.selfmademarket.Ui

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
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.hiskytech.selfmademarket.Model.ModelResponsePassword
import com.hiskytech.selfmademarket.Model.ModelSignupResponse
import com.hiskytech.selfmademarket.Model.SignupBuilder.apiInterface
import com.hiskytech.selfmademarket.R
import com.hiskytech.selfmademarket.Repo.MySharedPref
import com.hiskytech.selfmademarket.databinding.ActivityRenewSubBinding
import com.hiskytech.selfmademarket.databinding.ActivitySignUpBinding
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class ActivityRenewSub : AppCompatActivity() {


    private lateinit var mySharedPref: MySharedPref
    private lateinit var binding: ActivityRenewSubBinding
    private var subscriptionPlan: String? = null
    private var isPlanSelected = false
    private val IMAGE_PICK_CODE = 1000
    private var clickedTextViewId: Int? = null
    private lateinit var dialog: Dialog
    private var screenshotUri: Uri? = null
    private var idCardFrontUri: Uri? = null
    private var idCardBackUri: Uri? = null
    private var profileImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRenewSubBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mySharedPref= MySharedPref(this@ActivityRenewSub)


        val layoutParams = binding.main.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.setMargins(-2, -2, -2, -2)
        binding.main.layoutParams = layoutParams

        binding.js.setOnClickListener {
            clickedTextViewId = it.id
            pickImageFromGallery()
        }
        binding.cv.setOnClickListener {

            subscriptionPlan = "one_month"
            binding.plan2.visibility = View.GONE
            binding.startupPlanSaving.visibility = View.VISIBLE
            isPlanSelected = true


            binding.cv.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white))
            binding.cv1.setCardBackgroundColor(ContextCompat.getColor(this, R.color.hint_color2))

            // Toast.makeText(this@ActivitySignUp, "Selected Plan: $subscriptionPlan", Toast.LENGTH_SHORT).show()

        }

        binding.cv1.setOnClickListener {
            binding.cv1.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white))
            binding.cv.setCardBackgroundColor(ContextCompat.getColor(this, R.color.hint_color))
            subscriptionPlan = "three_month"
            binding.plan2.visibility = View.VISIBLE
            binding.startupPlanSaving.visibility = View.GONE
            isPlanSelected = true


            binding.cv.setCardBackgroundColor(ContextCompat.getColor(this, R.color.hint_color))


        }
        binding.btnPayment.setOnClickListener {
            showPaymentDialog()
        }






        binding.btn.setOnClickListener {

            val transactionId = binding.tid.text.toString().trim()
var id2=mySharedPref.getUserModel()?.id

            if (transactionId.isEmpty()) {
                Toast.makeText(this@ActivityRenewSub, "Transaction ID is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (subscriptionPlan == null) {
                Toast.makeText(this@ActivityRenewSub, "Please select a subscription plan", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (screenshotUri == null) {
                Toast.makeText(this@ActivityRenewSub, "Please select a screenshot", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            showAnimation()
            val screenShot = uriToFile(screenshotUri!!)
            val requestFile = RequestBody.create("image/jpeg".toMediaType(), screenShot)
            val screenShotImage = MultipartBody.Part.createFormData("transcript_screenshot", screenShot.name, requestFile)









            val requestBodyMap = mapOf(

                "transaction_id" to RequestBody.create("text/plain".toMediaTypeOrNull(), transactionId),
                "plan_select" to RequestBody.create("text/plain".toMediaTypeOrNull(), subscriptionPlan!!),
                "id" to RequestBody.create("text/plain".toMediaTypeOrNull(), id2.toString()),

                )

            // Make the API call
            val call = apiInterface.RenewPlanUser(
                requestBodyMap["transaction_id"]!!,
                requestBodyMap["plan_select"]!!,
                        screenShotImage!!,
                requestBodyMap["id"]!!,


            )

            // Handle the API response
            call.enqueue(object : Callback<ModelResponsePassword> {
                override fun onResponse(call: Call<ModelResponsePassword>, response: Response<ModelResponsePassword>) {
                    closeAnimation()
                    if (response.isSuccessful) {
                        response.body()?.let {

mySharedPref.putRenewPlan()
                            mySharedPref.putSignUpUser()
                            startActivity(Intent(this@ActivityRenewSub, ActivityVerification::class.java))
                            finish()
                        } ?: run {
                            Log.e("API Error", "Response body is null")
                            Toast.makeText(this@ActivityRenewSub, "Response body is null", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Log.e("API Error", "Response code: ${response.code()}, message: ${response.message()}")
                        Toast.makeText(this@ActivityRenewSub, "Failed to fetch data: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ModelResponsePassword>, t: Throwable) {
                    closeAnimation()
                    Log.e("API Failure", "API call failed: ${t.message}", t)
                    Toast.makeText(this@ActivityRenewSub, "Failure: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })

        }}











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





    private fun showPaymentDialog() {
        val dialog = Dialog(this@ActivityRenewSub)
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
        val dialog = Dialog(this@ActivityRenewSub)
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
    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            val imageUri = data?.data
            if (imageUri != null) {

                screenshotUri = imageUri
                binding.js.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.baseline_image_24,
                    0,
                    0,
                    0
                )

            } else {
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this, "Image selection canceled", Toast.LENGTH_SHORT).show()
        }
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


private fun uriToFile(uri: Uri): File {
    val inputStream: InputStream? = contentResolver.openInputStream(uri)
    val file = File(cacheDir, getFileName(uri))
    val outputStream = FileOutputStream(file)
    inputStream?.use { input ->
        outputStream.use { output ->
            val buffer = ByteArray(4 * 1024)
            var read: Int
            while (input.read(buffer).also { read = it } != -1) {
                output.write(buffer, 0, read)
            }
            output.flush()
        }
    }
    return file
}

private fun getFileName(uri: Uri): String {
    var name = ""
    val returnCursor = contentResolver.query(uri, null, null, null, null)
    if (returnCursor != null) {
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        name = returnCursor.getString(nameIndex)
        returnCursor.close()
    }
    return name
}

}