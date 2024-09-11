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
import com.hiskytech.selfmademarket.Adapter.AdapterCourse
import com.hiskytech.selfmademarket.ApiInterface.CourseInterface
import com.hiskytech.selfmademarket.Model.ModelCourses
import com.hiskytech.selfmademarket.Model.ModelCoursesItem
import com.hiskytech.selfmademarket.Model.ModelNotification
import com.hiskytech.selfmademarket.Model.NotificationBuilder
import com.hiskytech.selfmademarket.Model.RetrofitBuilder
import com.hiskytech.selfmademarket.Repo.MySharedPref
import com.hiskytech.selfmademarket.R
import com.hiskytech.selfmademarket.databinding.FragmentCoursesBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentCourses : Fragment() {

    private lateinit var mySharedPref: MySharedPref
    private var _binding: FragmentCoursesBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding is not initialized")
    private lateinit var dialog: Dialog
    private lateinit var courseAdapter: AdapterCourse
    private var courseList: MutableList<ModelCoursesItem> = mutableListOf()
    private val handler = Handler(Looper.getMainLooper())
    private val fetchRunnable = object : Runnable {
        override fun run() {
            fetchCourses()
            handler.postDelayed(this, 10000) // Repeat every 10 seconds
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCoursesBinding.inflate(inflater, container, false)
        mySharedPref = MySharedPref(requireContext())

        setupRecyclerView()
        setupListeners()
        loadCourses()
        startFetchingCoursesPeriodically()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views that depend on the binding
        binding.swipeRefresherLayout.setOnRefreshListener {
            fetchCourses()
        }

        val fullUrl = "https://en.selfmademarket.net/planemanger/uploads/${mySharedPref.getUserModel()?.user_image}"
        Glide.with(requireContext()).load(fullUrl).into(binding.profileImg)
        binding.courseName.text = mySharedPref.getUserModel()?.name

        binding.profileImg.setOnClickListener {
            startActivity(Intent(requireContext(), ActivityInvoices::class.java))
        }
    }

    private fun setupRecyclerView() {
        courseAdapter = AdapterCourse(requireContext(), courseList)
        binding.rccourses.layoutManager = LinearLayoutManager(requireContext())
        binding.rccourses.adapter = courseAdapter
    }

    private fun setupListeners() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterCourses(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnNotification.setOnClickListener {
            fetchNotificationsAndShowDialog()
        }
    }

    private fun fetchCourses() {
        // Show loading animation if necessary
        val apiInterface = RetrofitBuilder.getInstance().create(CourseInterface::class.java)
        val call = apiInterface.getCourses()

        call.enqueue(object : Callback<ModelCourses> {
            override fun onResponse(call: Call<ModelCourses>, response: Response<ModelCourses>) {
                // Hide loading animation if necessary
                if (isAdded && _binding != null) { // Check if fragment is added and binding is not null
                    if (response.isSuccessful) {
                        val courses = response.body() ?: emptyList()
                        binding.swipeRefresherLayout.isRefreshing = false
                        mySharedPref.storeCoursesInPref(courses)
                        courseList.clear()
                        courseList.addAll(courses)
                        courseAdapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(requireContext(), "Failed to fetch courses", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<ModelCourses>, t: Throwable) {
                // Hide loading animation if necessary
                if (isAdded && _binding != null) { // Check if fragment is added and binding is not null
                    Log.e("API_ERROR", "Failed to fetch courses: ${t.message}")
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun loadCourses() {
        courseList.clear()
        courseList.addAll(mySharedPref.retrieveStoredCourses())
        courseAdapter.notifyDataSetChanged()
    }

    private fun filterCourses(query: String) {
        val filteredList = courseList.filter { course ->
            course.course_name.contains(query, ignoreCase = true)
        }
        courseAdapter.updateList(filteredList)
    }

    private fun fetchNotificationsAndShowDialog() {
        val notificationInterface = NotificationBuilder.getNotificationInterface()
        val call = notificationInterface.getNotification()

        call.enqueue(object : Callback<ModelNotification> {
            override fun onResponse(call: Call<ModelNotification>, response: Response<ModelNotification>) {
                if (response.isSuccessful) {
                    val notifications = response.body()
                    if (!notifications.isNullOrEmpty()) {
                        val firstNotification = notifications[0]
                        showDialog(firstNotification.title, firstNotification.message)
                    } else {
                        showToast("No notifications available")
                    }
                } else {
                    showToast("Failed to get notifications")
                }
            }

            override fun onFailure(call: Call<ModelNotification>, t: Throwable) {
                Log.e("API_ERROR", "Failed to fetch notifications: ${t.message}")
                showToast("Error: ${t.message}")
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
                attributes = attributes?.apply {
                    y = 100
                }
            }
        }

        dialogView.findViewById<TextView>(R.id.notificationTitle).text = title
        dialogView.findViewById<TextView>(R.id.notificationDescription).text = message
        dialog.show()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun startFetchingCoursesPeriodically() {
        handler.post(fetchRunnable)
    }

    private fun stopFetchingCoursesPeriodically() {
        handler.removeCallbacks(fetchRunnable)
    }

    override fun onDestroyView() {
        stopFetchingCoursesPeriodically()
        _binding = null
        super.onDestroyView()
    }
}
