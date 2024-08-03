package com.hiskytech.selfmademarket.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.hiskytech.selfmademarket.Adapter.AdapterCourse
import com.hiskytech.selfmademarket.ApiInterface.CourseInterface
import com.hiskytech.selfmademarket.Model.ModelCourses
import com.hiskytech.selfmademarket.Model.ModelCoursesItem
import com.hiskytech.selfmademarket.Model.RetrofitBuilder
import com.hiskytech.selfmademarket.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentCourses : Fragment() {

    private lateinit var courseRecyclerView: RecyclerView
    private lateinit var courseAdapter: AdapterCourse
    private var courseList: MutableList<ModelCoursesItem> = mutableListOf()

    private lateinit var lottieAnimationView: LottieAnimationView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_courses, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        courseRecyclerView = view.findViewById(R.id.rvcourses)
        courseRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        courseAdapter = AdapterCourse(requireContext(), courseList)
        courseRecyclerView.adapter = courseAdapter
        lottieAnimationView = view.findViewById(R.id.lottieAnimationView)
        fetchCourse()
    }

    private fun fetchCourse() {
        lottieAnimationView.visibility = View.VISIBLE
        courseRecyclerView.visibility = View.GONE
        val quotesApi = RetrofitBuilder.getInstance().create(CourseInterface::class.java)
        val call = quotesApi.getCourses()

        call.enqueue(object : Callback<ModelCourses> {
            override fun onResponse(call: Call<ModelCourses>, response: Response<ModelCourses>) {
                lottieAnimationView.visibility = View.GONE
                courseRecyclerView.visibility = View.VISIBLE
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
                lottieAnimationView.visibility = View.GONE
                Log.e("FragmentCourses", "Error fetching courses", t)
            }
        })
    }
}
