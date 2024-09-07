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
            // Create the Dialog instance
            val dialogView = Dialog(context)

            // Set the custom layout for the dialog
            dialogView.setContentView(R.layout.dialog_image_view)
            dialogView.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            // Find the ImageView in the dialog layout
            val imgFull = dialogView.findViewById<ImageView>(R.id.imgFull)
            val cross = dialogView.findViewById<ImageView>(R.id.cross)


            // Load the image into the ImageView using Glide
            val fullUrl = "https://en.selfmademarket.net/planemanger/uploads/${currentPosition.image}"
            Glide.with(context)
                .load(fullUrl)
                .into(imgFull)

            // Show the dialog
            dialogView.show()


            cross.setOnClickListener()
            {
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
        commintList = newList
        notifyDataSetChanged()
    }
}
