package com.hiskytech.selfmademarket.Fragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hiskytech.selfmademarket.Adapter.AdapterForex
import com.hiskytech.selfmademarket.ApiInterface.ForexInterface
import com.hiskytech.selfmademarket.Model.DataXX
import com.hiskytech.selfmademarket.Model.ForexBuilder
import com.hiskytech.selfmademarket.Model.ModelForex
import com.hiskytech.selfmademarket.Model.ModelNotification
import com.hiskytech.selfmademarket.Model.NotificationBuilder
import com.hiskytech.selfmademarket.R
import com.hiskytech.selfmademarket.databinding.FragmentForexBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentForex : Fragment() {

    private var _binding: FragmentForexBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentForexBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerView
        binding.rvForex.layoutManager = LinearLayoutManager(context)

        fetchForex()
        binding.btnNotification.setOnClickListener(){
            fetchNotificationsAndShowDialog()
        }
    }

    private fun fetchForex() {
        val apiInterface = ForexBuilder.getInstance().create(ForexInterface::class.java)
        val call = apiInterface.getForex()

        call.enqueue(object : Callback<ModelForex> {
            override fun onResponse(call: Call<ModelForex>, response: Response<ModelForex>) {
                if (response.isSuccessful) {
                    val forexList = response.body()?.data ?: emptyList()
                    binding.rvForex.adapter = AdapterForex(requireContext(), forexList)
                } else {
                    Log.e("FetchError", "Response code: ${response.code()}")
                    Log.e("FetchError", "Response message: ${response.message()}")
                    Log.e("FetchError", "Error body: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ModelForex>, t: Throwable) {
                Log.e("FetchError", "API call failed: ${t.message}")
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun fetchNotificationsAndShowDialog() {
        val notificationInterface = NotificationBuilder.getNotificationInterface()
        val call = notificationInterface.getNotification()

        call.enqueue(object : Callback<ModelNotification> {
            override fun onResponse(
                call: Call<ModelNotification>,
                response: Response<ModelNotification>
            ) {
                if (response.isSuccessful) {
                    val notifications = response.body()
                    if (!notifications.isNullOrEmpty()) {
                        // Show the first notification in the dialog
                        val firstNotification = notifications[0]
                        showDialog(firstNotification.title, firstNotification.message)
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
        val dialog = Dialog(requireContext())
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.notification_dialog, null)

        dialog.setContentView(dialogView)

        // Set the dialog to show at the top
        dialog.window?.setGravity(Gravity.TOP)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes = dialog.window?.attributes?.apply {
            y = 100 // Adjust the vertical offset if needed
        }

        // Find and set up views in the custom dialog layout
        val dialogTitle = dialogView.findViewById<TextView>(R.id.notificationTitle)
        val dialogMessage = dialogView.findViewById<TextView>(R.id.notificationDescription)

        dialogTitle.text = title
        dialogMessage.text = message

        dialog.show()
    }
}
