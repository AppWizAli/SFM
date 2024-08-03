package com.hiskytech.selfmademarket.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hiskytech.selfmademarket.Model.DataXX
import com.hiskytech.selfmademarket.databinding.ForexItemBinding

class AdapterForex(val context: Context, var forexList : List<DataXX>) : RecyclerView.Adapter<AdapterForex.MyViewHolder>(){

    inner class MyViewHolder(val binding : ForexItemBinding): RecyclerView.ViewHolder(binding.root){}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterForex.MyViewHolder {
        val binding = ForexItemBinding.inflate(LayoutInflater.from(context),parent,false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterForex.MyViewHolder, position: Int) {
        val currentPosition = forexList[position]

        holder.binding.butonPlan.setText(currentPosition.created_date)
        holder.binding.tvForexDescription.text = currentPosition.description

        val url = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ3i2zZQq5CTOf6rmKK_EtSbEsaEIkJQl2YQQ&s"
        Glide.with(context).load(url)
            .into(holder.binding.img)
    }

    override fun getItemCount(): Int {
       return forexList.size
    }
}