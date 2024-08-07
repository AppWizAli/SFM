package com.hiskytech.selfmademarket.Adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.hiskytech.selfmademarket.Fragment.FragmentCoursesDetail
import com.hiskytech.selfmademarket.Model.ModelCoursesItem
import com.hiskytech.selfmademarket.R
import com.squareup.picasso.Picasso

class AdapterCourseDetails(private val context: FragmentActivity, private val courseList: List<ModelCoursesItem>) :
    RecyclerView.Adapter<AdapterCourseDetails.CourseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.courses_detailsitem, parent, false)
        return CourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = courseList[position]
        holder.courseNameTextView.text = course.course_name
        holder.courseInstructorTextView.text = course.course_instructor
        holder.courseDurationTextView.text = course.course_duration
        holder.courseVideosCountTextView.text = course.total_videos
        Picasso.get().load(course.course_image).into(holder.courseImageView)

        holder.itemView.setOnClickListener {
            val fragment = FragmentCoursesDetail()
            val bundle = Bundle().apply {
                putString("COURSE_ID", course.course_id)
                putString("COURSE_NAME", course.course_name)
                putString("COURSE_INSTRUCTOR", course.course_instructor)
                putString("COURSE_DURATION", course.course_duration)
                putString("COURSE_LEVEL", course.course_level)
                putString("COURSE_IMAGE", course.course_image)
                putString("COURSE_CREATED_AT", course.created_at)
                putString("COURSE_TOTAL_VIDEOS", course.total_videos)
                putParcelableArrayList("COURSE_VIDEOS", ArrayList(course.videos))
            }
            fragment.arguments = bundle
            context.supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentCoursesDetail, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun getItemCount(): Int {
        return courseList.size
    }

    class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val courseImageView: ImageView = itemView.findViewById(R.id.imgCourse)
        val courseNameTextView: TextView = itemView.findViewById(R.id.tvCourseName)
        val courseInstructorTextView: TextView = itemView.findViewById(R.id.tvCourseInstructor)
        val courseDurationTextView: TextView = itemView.findViewById(R.id.tvDuration)
        val courseVideosCountTextView: TextView = itemView.findViewById(R.id.tvVideosCount)
        val beginnerButton: TextView = itemView.findViewById(R.id.btnBeginner)
        val proButton: TextView = itemView.findViewById(R.id.btnPro)
    }
}
