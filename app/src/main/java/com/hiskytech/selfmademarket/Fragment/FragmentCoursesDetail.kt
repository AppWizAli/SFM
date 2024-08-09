package com.hiskytech.selfmademarket.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.hiskytech.selfmademarket.Adapter.AdapterVideo
import com.hiskytech.selfmademarket.Model.ModelCoursesItem
import com.hiskytech.selfmademarket.databinding.FragmentCoursesDetailBinding

class FragmentCoursesDetail : Fragment() {

    private lateinit var course: ModelCoursesItem

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentCoursesDetailBinding.inflate(inflater, container, false)

        // Retrieve the course data from the bundle
        arguments?.let {
            course = it.getParcelable("course") ?: return@let
        }

        // Use the course data
        binding.tvCourseName.text = course.course_name
        binding.userName.text = course.course_instructor
        binding.tvDuration.text = course.course_duration

        val fullUrl = "https://hiskytechs.com/planemanger/uploads/${course.course_image}"
        Glide.with(requireContext()).load(fullUrl).into(binding.imgdetails)

        // Set up RecyclerView for videos
        binding.rvVideos.layoutManager = LinearLayoutManager(requireContext())
        binding.rvVideos.adapter = AdapterVideo(course.videos)

        return binding.root
    }
}
