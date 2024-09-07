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
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.hiskytech.selfmademarket.ActivityInvoices
import com.hiskytech.selfmademarket.Adapter.AdaterCommint
import com.hiskytech.selfmademarket.Model.CommentX
import com.hiskytech.selfmademarket.Model.ModelCommint
import com.hiskytech.selfmademarket.Model.NotificationBuilder
import com.hiskytech.selfmademarket.Model.ModelNotification
import com.hiskytech.selfmademarket.R
import com.hiskytech.selfmademarket.Repo.MySharedPref
import com.hiskytech.selfmademarket.databinding.FragmentCommunityBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentCommunity : Fragment() {

    private lateinit var mySharedPref: MySharedPref
    private var _binding: FragmentCommunityBinding? = null
    private val binding get() = _binding!!
    private lateinit var dialog: Dialog
    private lateinit var adaterCommint: AdaterCommint
    private var courseList: MutableList<CommentX> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCommunityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mySharedPref = MySharedPref(requireContext())

        setupRecyclerView()
        setupListeners()

        fetchCommentsFromSharedPref()

        val fullUrl = "https://en.selfmademarket.net/planemanger/uploads/${mySharedPref.getUserModel()?.user_image}"
        Glide.with(requireContext()).load(fullUrl).into(binding.img)
        binding.courseName.text = mySharedPref.getUserModel()?.name

        binding.img.setOnClickListener {
            val intent = Intent(requireContext(), ActivityInvoices::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        adaterCommint = AdaterCommint(requireContext(), courseList)
        binding.rvCommunity.layoutManager = LinearLayoutManager(context)
        binding.rvCommunity.adapter = adaterCommint
    }

    private fun setupListeners() {
        binding.btnNotification.setOnClickListener {
            fetchNotificationsAndShowDialog()
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterCourses(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun fetchCommentsFromSharedPref() {

        val json = mySharedPref.retrieveStoredComments() // Replace this with the actual method to get the comments JSON from SharedPreferences

                courseList.clear()
                courseList.addAll(json)
                adaterCommint.notifyDataSetChanged()


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

    private fun filterCourses(query: String) {
        val filteredList = courseList.filter { course ->
            course.description?.contains(query, ignoreCase = true) ?: false
        }
        adaterCommint.updateList(filteredList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showAnimation() {
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.loadingdialog)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    private fun closeAnimation() {
        dialog.dismiss()
    }
}
