package com.hiskytech.selfmademarket.Adapter

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hiskytech.selfmademarket.Model.CommentX
import com.hiskytech.selfmademarket.R
import com.hiskytech.selfmademarket.databinding.CommunityItemBinding
import java.text.SimpleDateFormat
import java.util.Locale

class AdaterCommint(val context: Context, private var commintList: List<CommentX>) : RecyclerView.Adapter<AdaterCommint.MyViewHolder>() {
    inner class MyViewHolder(val binding: CommunityItemBinding) : RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = CommunityItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyViewHolder(binding)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentPosition = commintList[position]
        holder.binding.description.text = currentPosition.description
        holder.binding.username.text = currentPosition.user_name
        holder.binding.zoomBtn.setOnClickListener {
            val dialogView = Dialog(context)
            dialogView.setContentView(R.layout.dialog_image_view)
            dialogView.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

            // Use PhotoView instead of ImageView for imgFull
            val imgFull = dialogView.findViewById<com.github.chrisbanes.photoview.PhotoView>(R.id.imgFull)
            val cross = dialogView.findViewById<ImageView>(R.id.cross)

            val fullUrl = "https://en.selfmademarket.net/planemanger/uploads/${currentPosition.image}"
            Glide.with(context).load(fullUrl).into(imgFull)

            dialogView.show()

            cross.setOnClickListener {
                dialogView.dismiss()
            }

            dialogView.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
   holder.binding.accountImage.setOnClickListener {
            val dialogView = Dialog(context)
            dialogView.setContentView(R.layout.dialog_image_view)
            dialogView.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

            // Use PhotoView instead of ImageView for imgFull
            val imgFull = dialogView.findViewById<com.github.chrisbanes.photoview.PhotoView>(R.id.imgFull)
            val cross = dialogView.findViewById<ImageView>(R.id.cross)

            val fullUrl = "https://en.selfmademarket.net/planemanger/uploads/${currentPosition.image}"
            Glide.with(context).load(fullUrl).into(imgFull)

            dialogView.show()

            cross.setOnClickListener {
                dialogView.dismiss()
            }

            dialogView.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }





        val fullUrlimage = "https://en.selfmademarket.net/planemanger/uploads/${currentPosition.user_image}"
        Glide.with(context).load(fullUrlimage).into(holder.binding.profileImage)
        val fullUrl = "https://en.selfmademarket.net/planemanger/uploads/${currentPosition.image}"
        Glide.with(context).load(fullUrl).into(holder.binding.accountImage)
    }

    override fun getItemCount(): Int {
        return commintList.size
    }

    fun updateList(newList: List<CommentX>) {
        commintList = newList.sortedByDescending {
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(it.created_at)
        }
        notifyDataSetChanged()
    }
}
