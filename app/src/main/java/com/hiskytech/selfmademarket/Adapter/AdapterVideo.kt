package com.hiskytech.selfmademarket.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hiskytech.selfmademarket.Model.Video
import com.hiskytech.selfmademarket.Ui.playeractivity
import com.hiskytech.selfmademarket.databinding.ItemVideoBinding

class AdapterVideo(private val videoList: List<Video>) :
    RecyclerView.Adapter<AdapterVideo.VideoViewHolder>() {

    class VideoViewHolder(val binding: ItemVideoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = ItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VideoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val video = videoList[position]
        holder.binding.videoDescription.text = video.video_description
        holder.binding.videoCount.text = video.video_id

        // Handle item click
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, playeractivity::class.java)
            intent.putExtra("videourl", video.video) // Pass the video URL to the playeractivity
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return videoList.size
    }
}
