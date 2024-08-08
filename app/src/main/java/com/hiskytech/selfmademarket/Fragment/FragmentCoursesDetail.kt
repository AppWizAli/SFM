package com.hiskytech.selfmademarket.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hiskytech.selfmademarket.R
import com.hiskytech.selfmademarket.Model.Video
import android.widget.Button
import android.widget.TextView
import de.hdodenhof.circleimageview.CircleImageView
import com.bumptech.glide.Glide
import com.hiskytech.selfmademarket.Adapter.AdapterCourse

class FragmentCoursesDetail : Fragment() {

    private lateinit var courseName: TextView
    private lateinit var courseImage: CircleImageView
    private lateinit var courseInstructor: TextView
    private lateinit var courseDuration: TextView
    private lateinit var courseVideos: RecyclerView
    private lateinit var beginnersButton: Button
    private lateinit var proButton: Button

    private lateinit var videoList: List<Video>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return view
    }
}
