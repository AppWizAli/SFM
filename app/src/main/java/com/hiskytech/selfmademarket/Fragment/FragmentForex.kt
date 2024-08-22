package com.hiskytech.selfmademarket.Fragment

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
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
import com.hiskytech.selfmademarket.Adapter.AdapterForex
import com.hiskytech.selfmademarket.Model.DataXX
import com.hiskytech.selfmademarket.Model.ModelNotification
import com.hiskytech.selfmademarket.Model.NotificationBuilder
import com.hiskytech.selfmademarket.R
import com.hiskytech.selfmademarket.Repo.MySharedPref
import com.hiskytech.selfmademarket.databinding.FragmentForexBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentForex : Fragment() {

    private lateinit var mySharedPref: MySharedPref
    private var _binding: FragmentForexBinding? = null
    private val binding get() = _binding!!
    private lateinit var dialog: Dialog
    private lateinit var adapterForex: AdapterForex
    private var forexList: MutableList<DataXX> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentForexBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mySharedPref = MySharedPref(requireContext())

        setupRecyclerView()
        setupListeners()

        val fullUrl = "https://hiskytechs.com/planemanger/uploads/${mySharedPref.getUserModel()?.user_image}"
        Glide.with(requireContext()).load(fullUrl).into(binding.img)
        binding.courseName.text = mySharedPref.getUserModel()?.name

        // Load forex data from SharedPreferences
        loadForexData()

        binding.img.setOnClickListener {
            startActivity(Intent(requireContext(), ActivityInvoices::class.java))
        }
    }

    private fun setupRecyclerView() {
        adapterForex = AdapterForex(requireContext(), emptyList())
        binding.rvForex.layoutManager = LinearLayoutManager(context)
        binding.rvForex.adapter = adapterForex
    }

    private fun setupListeners() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterForex(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnNotification.setOnClickListener {
            fetchNotificationsAndShowDialog()
        }
    }

    private fun loadForexData() {
        forexList = mySharedPref.retrieveStoredForexData().toMutableList()
        adapterForex.updateList(forexList)
    }

    private fun filterForex(query: String) {
        val filteredList = forexList.filter { it.description.contains(query, ignoreCase = true) }
        adapterForex.updateList(filteredList)
    }

    private fun fetchNotificationsAndShowDialog() {
        // This method remains unchanged
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
        val dialog = Dialog(requireContext()).apply {
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
        dialog.dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
