package com.hiskytech.selfmademarket.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hiskytech.selfmademarket.Model.Video
import com.hiskytech.selfmademarket.Ui.playeractivity
import com.hiskytech.selfmademarket.databinding.ItemVideoBinding

class AdapterVideo(videoList: List<Video>) :
    RecyclerView.Adapter<AdapterVideo.VideoViewHolder>() {

    // Sort the videos by the created_at date in ascending order
    private val sortedVideoList = videoList.sortedBy { it.created_at }

    class VideoViewHolder(val binding: ItemVideoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = ItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VideoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val video = sortedVideoList[position]

        // Set the video count starting from 1
        holder.binding.videoCount.text = (position + 1).toString()

        // Handle item click
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, playeractivity::class.java).apply {
                putExtra("videourl1", video.video)  // High quality
                putExtra("videourl2", video.video2) // Medium quality
                putExtra("videourl3", video.video3) // Low quality
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return sortedVideoList.size
    }
}
