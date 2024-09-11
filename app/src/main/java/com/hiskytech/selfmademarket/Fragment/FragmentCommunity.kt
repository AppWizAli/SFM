package com.hiskytech.selfmademarket.Fragment

import android.Manifest
import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
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
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.hiskytech.selfmademarket.ActivityInvoices
import com.hiskytech.selfmademarket.Adapter.AdaterCommint
import com.hiskytech.selfmademarket.ApiInterface.CommentsInterface
import com.hiskytech.selfmademarket.Model.CommentX
import com.hiskytech.selfmademarket.Model.ModelCommint
import com.hiskytech.selfmademarket.Model.ModelNotification
import com.hiskytech.selfmademarket.Model.NotificationBuilder
import com.hiskytech.selfmademarket.Model.RetrofitBuilder
import com.hiskytech.selfmademarket.R
import com.hiskytech.selfmademarket.Repo.MySharedPref
import com.hiskytech.selfmademarket.databinding.FragmentCommunityBinding
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

    // Handler and Runnable for periodic data fetching
    private val handler = Handler(Looper.getMainLooper())
    private val fetchRunnable = object : Runnable {
        override fun run() {
            fetchComments()
            handler.postDelayed(this, 1000) // Repeat every 5 seconds
        }
    }
    private var lastFetchedComments: MutableList<CommentX> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCommunityBinding.inflate(inflater, container, false)
        mySharedPref = MySharedPref(requireContext())

        setupRecyclerView()
        setupListeners()

        fetchCommentsFromSharedPref()
        binding.swipeRefresherLayout.setOnRefreshListener {
            fetchComments()
        }

        val fullUrl = "https://en.selfmademarket.net/planemanger/uploads/${mySharedPref.getUserModel()?.user_image}"
        Glide.with(requireContext()).load(fullUrl).into(binding.img)
        binding.courseName.text = mySharedPref.getUserModel()?.name

        binding.img.setOnClickListener {
            val intent = Intent(requireContext(), ActivityInvoices::class.java)
            startActivity(intent)
        }

        // Start the periodic fetching of comments
        startFetchingCommentsPeriodically()


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
    private fun fetchComments() {
        // Check if the binding is not null before accessing it
        if (_binding == null) return

        val apiInterface = RetrofitBuilder.getInstance().create(CommentsInterface::class.java)
        val call = apiInterface.getCommints()

        call.enqueue(object : Callback<ModelCommint> {
            override fun onResponse(call: Call<ModelCommint>, response: Response<ModelCommint>) {
                if (response.isSuccessful) {
                    val comments = response.body()?.comments ?: emptyList()
                    adaterCommint.updateList(comments)
                    mySharedPref.storeCommentsInPref(comments)
                    // Check if the binding is still valid before updating UI components
                    if (_binding != null) {
                        binding.swipeRefresherLayout.isRefreshing = false

                    }
                }
            }

            override fun onFailure(call: Call<ModelCommint>, t: Throwable) {
                // Handle failure
            }
        })
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
        adaterCommint.updateList(json)
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
        stopFetchingCommentsPeriodically() // Stop periodic tasks when view is destroyed
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

    // Method to start periodic fetching of comments
    private fun startFetchingCommentsPeriodically() {
        handler.post(fetchRunnable)
    }

    // Method to stop periodic fetching of comments
    private fun stopFetchingCommentsPeriodically() {
        handler.removeCallbacks(fetchRunnable)
    }

    // Method to check for new items and send notification


}
