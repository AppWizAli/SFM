package com.hiskytech.selfmademarket.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hiskytech.selfmademarket.Model.DataXX
import com.hiskytech.selfmademarket.databinding.ForexItemBinding
import java.text.SimpleDateFormat
import java.util.Locale

class AdapterForex(val context: Context, var forexList : List<DataXX>) : RecyclerView.Adapter<AdapterForex.MyViewHolder>(){

    inner class MyViewHolder(val binding : ForexItemBinding): RecyclerView.ViewHolder(binding.root){}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterForex.MyViewHolder {
        val binding = ForexItemBinding.inflate(LayoutInflater.from(context),parent,false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterForex.MyViewHolder, position: Int) {
        val currentPosition = forexList[position]

        val originalFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val targetFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = originalFormat.parse(currentPosition.created_date)
        val formattedDate = targetFormat.format(date)

        holder.binding.butonPlan.setText(formattedDate)
        holder.binding.tvForexTittle.text=currentPosition.title
        holder.binding.tvForexDescription.text = currentPosition.description
        val fullUrl = "https://hiskytechs.com/planemanger/uploads/${currentPosition.image}"
        Glide.with(context).load(fullUrl).into(holder.binding.img)

    }

    override fun getItemCount(): Int {
       return forexList.size
    }
}