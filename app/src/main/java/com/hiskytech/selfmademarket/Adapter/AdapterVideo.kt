package com.hiskytech.selfmademarket.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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
        holder.binding.videoDescription.text = video.video_description ?: "No description available"
        holder.binding.videoCount.text = video.video_id ?: "N/A"

        // Uncomment the below line to load the video thumbnail if available
        // Glide.with(holder.itemView.context).load(video.video).into(holder.binding.imgVideoThumbnail)

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, playeractivity::class.java)
            intent.putExtra("videourl", video.video) // Pass the video URL to PlayerActivity
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return videoList.size
    }
}
