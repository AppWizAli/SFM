package com.hiskytech.selfmademarket

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hiskytech.selfmademarket.Repo.MySharedPref
import com.hiskytech.selfmademarket.Ui.ActivityLogin
import com.hiskytech.selfmademarket.databinding.ActivityInvoicesBinding
import com.hiskytech.selfmademarket.databinding.ActivitySplashBinding

class ActivityInvoices : AppCompatActivity() {

    private lateinit var binding: ActivityInvoicesBinding
    private lateinit var mySharedPref: MySharedPref
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityInvoicesBinding.inflate(layoutInflater)
        setContentView(binding.root)

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


    }
}