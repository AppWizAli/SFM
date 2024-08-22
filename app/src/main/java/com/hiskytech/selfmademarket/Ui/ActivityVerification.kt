package com.hiskytech.selfmademarket.Ui

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hiskytech.selfmademarket.R
import com.hiskytech.selfmademarket.databinding.ActivityVerificationBinding

class ActivityVerification : AppCompatActivity() {
    private lateinit var binding:ActivityVerificationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val layoutParams = binding.main.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.setMargins(-2, -2, -2, -2)
        binding.main.layoutParams = layoutParams


        findViewById<AppCompatButton>(R.id.btnLogin).setOnClickListener()
        {
            startActivity(Intent(this@ActivityVerification,ActivityLogin::class.java))
        }









    }
}