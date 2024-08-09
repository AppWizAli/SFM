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
import com.airbnb.lottie.LottieAnimationView
import com.hiskytech.selfmademarket.Adapter.AdapterCourse
import com.hiskytech.selfmademarket.ApiInterface.CourseInterface
import com.hiskytech.selfmademarket.Model.ModelCourses
import com.hiskytech.selfmademarket.Model.ModelCoursesItem
import com.hiskytech.selfmademarket.Model.ModelNotification
import com.hiskytech.selfmademarket.Model.NotificationBuilder
import com.hiskytech.selfmademarket.Model.RetrofitBuilder
import com.hiskytech.selfmademarket.R
import com.hiskytech.selfmademarket.databinding.FragmentCoursesBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentCourses : Fragment() {

    private var _binding: FragmentCoursesBinding? = null
    private val binding get() = _binding!!
    private lateinit var dialog: Dialog
    private lateinit var courseAdapter: AdapterCourse
    private var courseList: MutableList<ModelCoursesItem> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCoursesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvcourses.layoutManager = LinearLayoutManager(requireContext())
        courseAdapter = AdapterCourse(requireContext(), courseList)
        binding.rvcourses.adapter = courseAdapter
        fetchCourse()
        binding.btnNotification.setOnClickListener {
            fetchNotificationsAndShowDialog()
        }
    }

    private fun fetchCourse() {
       showAnimation()
        val quotesApi = RetrofitBuilder.getInstance().create(CourseInterface::class.java)
        val call = quotesApi.getCourses()

        call.enqueue(object : Callback<ModelCourses> {
            override fun onResponse(call: Call<ModelCourses>, response: Response<ModelCourses>) {
               closeAnimation()
                if (response.isSuccessful) {
                    val courses = response.body()
                    if (courses != null) {
                        courseList.clear()
                        courseList.addAll(courses)
                        courseAdapter.notifyDataSetChanged()
                        Log.e("FragmentCoursesSuccess", courses.toString())
                    } else {
                        Log.e("FragmentCourses", "No courses found")
                    }
                } else {
                    Log.e("FragmentCourses", "Failed to load courses: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ModelCourses>, t: Throwable) {
                closeAnimation()
                Log.e("FragmentCourses", "Error fetching courses", t)
            }
        })
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
