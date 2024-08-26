package com.hiskytech.selfmademarket

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.hiskytech.selfmademarket.ApiInterface.logininterface
import com.hiskytech.selfmademarket.Model.ModelLoginResponse2
import com.hiskytech.selfmademarket.Model.ModelResponsePassword
import com.hiskytech.selfmademarket.Model.loginBuilder
import com.hiskytech.selfmademarket.Repo.MySharedPref
import com.hiskytech.selfmademarket.Ui.ActivityLogin
import com.hiskytech.selfmademarket.Ui.MainActivity
import com.hiskytech.selfmademarket.databinding.ActivityInvoicesBinding
import com.hiskytech.selfmademarket.databinding.ActivitySplashBinding
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivityInvoices : AppCompatActivity() {
    private lateinit var apiInterFace: logininterface
    private lateinit var binding: ActivityInvoicesBinding
    private lateinit var mySharedPref: MySharedPref
    private lateinit var dialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityInvoicesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        apiInterFace = loginBuilder.apiInterface
        mySharedPref=MySharedPref(this@ActivityInvoices)
        binding.startDate.text=mySharedPref.getUserModel()?.subscription_start_date
        binding.endDate.text=mySharedPref.getUserModel()?.subscription_end_date

binding.btnLogout.setOnClickListener()
{
    mySharedPref.putUserLoggedOut()
    Toast.makeText(this@ActivityInvoices, "Logout Successsfully", Toast.LENGTH_SHORT).show()
    startActivity(Intent(this@ActivityInvoices,ActivityLogin::class.java))
    finishAffinity()
}

        binding.btnUpdatePassword.setOnClickListener {
            // Create the Dialog instance
            val dialogView = Dialog(this@ActivityInvoices)

            // Set the custom layout for the dialog
            dialogView.setContentView(R.layout.dialog_update_password)

            // Make the dialog full-screen
            dialogView.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            // Find the EditText and Button in the dialog
            val etPassword = dialogView.findViewById<EditText>(R.id.pswrd)
            val btnSubmit = dialogView.findViewById<MaterialButton>(R.id.btn)

            // Set click listener for the submit button
            btnSubmit.setOnClickListener {

                showAnimation()
                val password = etPassword.text.toString().trim()

                if (password.isNotEmpty()) {



                    val pass = RequestBody.create("text/plain".toMediaType(), password)
                    val id = RequestBody.create("text/plain".toMediaType(), mySharedPref.getUserModel()?.id.toString())

                    apiInterFace.updatePassowrd(id,pass).enqueue(object :
                        Callback<ModelResponsePassword> {
                        override fun onResponse(call: Call<ModelResponsePassword>, response: Response<ModelResponsePassword>) {
                            closeAnimation()
                            val userResponse = response.body()
                            if (response.isSuccessful && userResponse != null) {
                                 mySharedPref.putUserLoggedIn()
                                if (userResponse.status.toBoolean()) {
                                    Toast.makeText(this@ActivityInvoices, "Password Update Successful!!", Toast.LENGTH_SHORT).show()

                                } else {
                                    Toast.makeText(this@ActivityInvoices, "Updation Failed: ${userResponse.message}", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(this@ActivityInvoices, "Updation Failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<ModelResponsePassword>, t: Throwable) {
                            closeAnimation()
                            Toast.makeText(this@ActivityInvoices, "Something went wrong!!", Toast.LENGTH_SHORT).show()
                        }
                    })

                    dialogView.dismiss()
                } else {
                    etPassword.error = "Password cannot be empty"
                }
            }

            // Show the dialog
            dialogView.show()

            // Optional: Customize dialog appearance, e.g., make the background transparent
            dialogView.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
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
}