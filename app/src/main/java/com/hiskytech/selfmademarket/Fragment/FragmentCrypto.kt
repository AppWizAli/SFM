package com.hiskytech.selfmademarket.Fragment

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.hiskytech.selfmademarket.ActivityInvoices
import com.hiskytech.selfmademarket.Adapter.AdapterBitcoin
import com.hiskytech.selfmademarket.ApiInterface.BitCoinInterface
import com.hiskytech.selfmademarket.Model.DataX
import com.hiskytech.selfmademarket.Model.ModelBitCoin
import com.hiskytech.selfmademarket.Model.ModelNotification
import com.hiskytech.selfmademarket.Model.NotificationBuilder
import com.hiskytech.selfmademarket.Model.RetrofitBuilder
import com.hiskytech.selfmademarket.R
import com.hiskytech.selfmademarket.Repo.MySharedPref
import com.hiskytech.selfmademarket.databinding.FragmentCryptoBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentCrypto : Fragment() {

    private lateinit var mySharedPref: MySharedPref
    private var _binding: FragmentCryptoBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding is not initialized")
    private lateinit var dialog: Dialog
    private lateinit var adapterBitcoin: AdapterBitcoin
    private var cryptoList: MutableList<DataX> = mutableListOf()
    private val handler = Handler(Looper.getMainLooper())
    private val fetchRunnable = object : Runnable {
        override fun run() {
            fetchBitCoins()
            handler.postDelayed(this, 1000) // Repeat every 1 second
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCryptoBinding.inflate(inflater, container, false)
        mySharedPref = MySharedPref(requireContext())

        setupRecyclerView()
        setupListeners()

        binding.swipeRefresherLayout.setOnRefreshListener {
            fetchBitCoins()
        }

        val fullUrl = "https://en.selfmademarket.net/planemanger/uploads/${mySharedPref.getUserModel()?.user_image}"
        Glide.with(requireContext()).load(fullUrl).into(binding.img)
        binding.courseName.text = mySharedPref.getUserModel()?.name

        // Load crypto data from SharedPreferences
        loadCryptoData()

        binding.img.setOnClickListener {
            startActivity(Intent(requireContext(), ActivityInvoices::class.java))
        }

        startFetchingBitCoinsPeriodically()
        return binding.root
    }

    private fun fetchBitCoins() {
        val apiInterface = RetrofitBuilder.getInstance().create(BitCoinInterface::class.java)
        val call = apiInterface.getBitCoin()

        call.enqueue(object : Callback<ModelBitCoin> {
            override fun onResponse(call: Call<ModelBitCoin>, response: Response<ModelBitCoin>) {
                if (_binding != null && response.isSuccessful) {
                    val bitCoins = response.body()?.data ?: emptyList()
                    binding.swipeRefresherLayout.isRefreshing = false
                    mySharedPref.storeBitCoinsInPref(bitCoins)
                    adapterBitcoin.updateList(bitCoins)
                }
            }

            override fun onFailure(call: Call<ModelBitCoin>, t: Throwable) {
                Log.e("API_ERROR", "Failed to fetch bit coins: ${t.message}")
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupRecyclerView() {
        adapterBitcoin = AdapterBitcoin(requireContext(), emptyList())
        binding.rvCoin.layoutManager = LinearLayoutManager(context)
        binding.rvCoin.adapter = adapterBitcoin
    }

    private fun setupListeners() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterBitCoins(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnNotification.setOnClickListener {
            fetchNotificationsAndShowDialog()
        }
    }

    private fun loadCryptoData() {
        cryptoList = mySharedPref.retrieveStoredBitCoins().toMutableList()
        adapterBitcoin.updateList(cryptoList)
    }

    private fun filterBitCoins(query: String) {
        val filteredList = cryptoList.filter { it.description.contains(query, ignoreCase = true) }
        adapterBitcoin.updateList(filteredList)
    }

    private fun fetchNotificationsAndShowDialog() {
        val notificationInterface = NotificationBuilder.getNotificationInterface()
        val call = notificationInterface.getNotification()

        call.enqueue(object : Callback<ModelNotification> {
            override fun onResponse(call: Call<ModelNotification>, response: Response<ModelNotification>) {
                if (response.isSuccessful) {
                    val notifications = response.body()
                    if (!notifications.isNullOrEmpty()) {
                        showDialog(notifications[0].title, notifications[0].message)
                    } else {
                        Toast.makeText(requireContext(), "No notifications available", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to get notifications", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ModelNotification>, t: Throwable) {
                Log.e("API_ERROR", "Failed to fetch notifications: ${t.message}")
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showDialog(title: String, message: String) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.notification_dialog, null)
        dialog = Dialog(requireContext()).apply {
            setContentView(dialogView)
            window?.apply {
                setGravity(Gravity.TOP)
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                attributes = attributes?.apply { y = 100 }
            }
        }

        dialogView.findViewById<TextView>(R.id.notificationTitle).text = title
        dialogView.findViewById<TextView>(R.id.notificationDescription).text = message
        dialog.show()
    }

    private fun showAnimation() {
        dialog = Dialog(requireContext()).apply {
            setContentView(R.layout.loadingdialog)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            show()
        }
    }

    private fun closeAnimation() {
        if (::dialog.isInitialized && dialog.isShowing) {
            dialog.dismiss()
        }
    }

    private fun startFetchingBitCoinsPeriodically() {
        handler.post(fetchRunnable)
    }

    private fun stopFetchingBitCoinsPeriodically() {
        handler.removeCallbacks(fetchRunnable)
    }

    override fun onDestroyView() {
        stopFetchingBitCoinsPeriodically()
        _binding = null
        super.onDestroyView()
    }
}
