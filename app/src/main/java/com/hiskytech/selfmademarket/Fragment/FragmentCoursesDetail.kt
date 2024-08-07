package com.hiskytech.selfmademarket.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hiskytech.selfmademarket.Adapter.VideoAdapter
import com.hiskytech.selfmademarket.Model.ModelVideo
import com.hiskytech.selfmademarket.R
import com.squareup.picasso.Picasso
import android.widget.Button
import android.widget.TextView
import com.hiskytech.selfmademarket.Adapter.AdapterCourseDetails
import de.hdodenhof.circleimageview.CircleImageView

class FragmentCoursesDetail : Fragment() {

    private lateinit var courseName: TextView
    private lateinit var courseImage: CircleImageView
    private lateinit var courseInstructor: TextView
    private lateinit var courseDuration: TextView
    private lateinit var courseVideos: RecyclerView
    private lateinit var videoList: List<ModelVideo>
    private lateinit var beginnersButton: Button
    private lateinit var proButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_courses_detail, container, false)

        // Initialize views
        courseName = view.findViewById(R.id.course_name)
        courseImage = view.findViewById(R.id.imgdetails) // Updated ID
        courseInstructor = view.findViewById(R.id.user_name) // Updated ID
        courseDuration = view.findViewById(R.id.hour) // Updated ID
        courseVideos = view.findViewById(R.id.rvcourses)
        beginnersButton = view.findViewById(R.id.beginner_button) // Updated ID
        proButton = view.findViewById(R.id.pro_button) // Updated ID

        // Retrieve data from arguments
        val args = arguments
        if (args != null) {
            courseName.text = args.getString("COURSE_NAME")
            courseInstructor.text = args.getString("COURSE_INSTRUCTOR")
            courseDuration.text = args.getString("COURSE_DURATION")
            videoList = args.getParcelableArrayList("COURSE_VIDEOS") ?: emptyList()
            Picasso.get().load(args.getString("COURSE_IMAGE")).into(courseImage)
        }

        // Set up RecyclerView
        courseVideos.layoutManager = LinearLayoutManager(context)
        courseVideos.adapter = AdapterCourseDetails(videoList)

        // Set up Button click listeners if needed
        beginnersButton.setOnClickListener {
            // Handle Beginners button click
        }

        proButton.setOnClickListener {
            // Handle Pro button click
        }

        return view
    }
}
