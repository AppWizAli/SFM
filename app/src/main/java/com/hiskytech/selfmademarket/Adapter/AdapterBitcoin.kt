package com.hiskytech.selfmademarket.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hiskytech.selfmademarket.Model.DataX
import com.hiskytech.selfmademarket.databinding.BitcoinItemBinding

class AdapterBitcoin(val context: Context, var bitCoinList : List<DataX>) : RecyclerView.Adapter<AdapterBitcoin.MyViewHolder>() {

    inner  class MyViewHolder(val binding : BitcoinItemBinding): RecyclerView.ViewHolder(binding.root){}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterBitcoin.MyViewHolder {
        val binding = BitcoinItemBinding.inflate(LayoutInflater.from(context),parent,false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterBitcoin.MyViewHolder, position: Int) {
        val currentPosition = bitCoinList[position]

        holder.binding.butonPlan.setText(currentPosition.created_date)
        holder.binding.tvDescription.text = currentPosition.description
        val fullUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTrfWHXUI7BqoCp21ARDmz1vSl4Q0LYyPPFIQ&s"
        Glide.with(context).load(fullUrl)
            .into(holder.binding.img)
    }

    override fun getItemCount(): Int {
        return bitCoinList.size
    }
}