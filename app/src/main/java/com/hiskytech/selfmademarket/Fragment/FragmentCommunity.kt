package com.hiskytech.selfmademarket.Fragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.hiskytech.selfmademarket.Adapter.AdaterCommint
import com.hiskytech.selfmademarket.ApiInterface.CommentsInterface
import com.hiskytech.selfmademarket.Model.CommintsBuilder
import com.hiskytech.selfmademarket.Model.ModelCommint
import com.hiskytech.selfmademarket.Model.ModelNotification
import com.hiskytech.selfmademarket.Model.NotificationBuilder
import com.hiskytech.selfmademarket.R
import com.hiskytech.selfmademarket.databinding.FragmentCommunityBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentCommunity : Fragment() {

    private var _binding: FragmentCommunityBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCommunityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvCommunity.layoutManager = LinearLayoutManager(context)
        fetchComments()

        binding.btnNotification.setOnClickListener {
            fetchNotificationsAndShowDialog()
        }
    }

    private fun fetchComments() {
        val apiInterface = CommintsBuilder.getInstance().create(CommentsInterface::class.java)
        val call = apiInterface.getCommints()

        call.enqueue(object : Callback<ModelCommint> {

            override fun onResponse(p0: Call<ModelCommint>, p1: Response<ModelCommint>) {
                if (p1.isSuccessful) {
                    val commentsList = p1.body()?.comments ?: emptyList()
                    Log.d("FetchSuccess", "Fetched ${commentsList.size} comments")
                    binding.rvCommunity.adapter = AdaterCommint(requireContext(), commentsList)
                } else {
                    Log.e("FetchError", "Response code: ${p1.code()}")
                    Log.e("FetchError", "Response message: ${p1.message()}")
                    Log.e("FetchError", "Error body: ${p1.errorBody()?.string()}")
                }
            }

            override fun onFailure(p0: Call<ModelCommint>, p1: Throwable) {
                Log.e("FetchError", "API call failed: ${p1.message}")
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
