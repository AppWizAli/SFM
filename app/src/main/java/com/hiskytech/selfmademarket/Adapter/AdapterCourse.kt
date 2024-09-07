package com.hiskytech.selfmademarket.Adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hiskytech.selfmademarket.Model.ModelCoursesItem
import com.hiskytech.selfmademarket.R
import com.hiskytech.selfmademarket.databinding.CoursesDetailsitemBinding

class AdapterCourse(val context : Context , var courseList : List<ModelCoursesItem>) : RecyclerView.Adapter<AdapterCourse.MyViewHolder>(){

    inner class MyViewHolder(val binding : CoursesDetailsitemBinding) : RecyclerView.ViewHolder(binding.root){
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterCourse.MyViewHolder {
        val binding =CoursesDetailsitemBinding.inflate(LayoutInflater.from(context),parent,false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterCourse.MyViewHolder, position: Int) {
        val course = courseList[position]

        holder.binding.tvCourseName.text = course.course_name
        holder.binding.tvCourseInstructor.text = course.course_instructor
        holder.binding.tvDuration.text = "${course.course_duration}"
        holder.binding.tvVideosCount.text = "${course.total_videos}"

        val fullUrl = "https://en.selfmademarket.net/planemanger/uploads/${course.course_image}"
        Glide.with(context).load(fullUrl).into(holder.binding.imgCourse)

        // Set OnClickListener to pass data
        holder.itemView.setOnClickListener {
            val bundle = Bundle().apply {
                putParcelable("course", course) // Pass the course object
            }
            it.findNavController().navigate(R.id.fragmentCoursesDetail, bundle)
        }
    }
    fun updateList(newList: List<ModelCoursesItem>) {
        courseList = newList
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int {
       return courseList.size
    }
}