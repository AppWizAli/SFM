package com.hiskytech.selfmademarket.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hiskytech.selfmademarket.Model.Comment
import com.hiskytech.selfmademarket.databinding.CommunityItemBinding

class AdaterCommint(val context: Context, var commintList: List<Comment>) : RecyclerView.Adapter<AdaterCommint.MyViewHolder>() {

    inner class MyViewHolder(val binding: CommunityItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = CommunityItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentPosition = commintList[position]

        holder.binding.description.text = currentPosition.description
        holder.binding.username.text = currentPosition.user_name
        Glide.with(context).load(currentPosition.image)
            .into(holder.binding.accountImage)
    }

    override fun getItemCount(): Int {
        return commintList.size
    }
}
