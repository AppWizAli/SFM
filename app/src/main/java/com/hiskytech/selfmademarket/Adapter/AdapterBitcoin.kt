package com.hiskytech.selfmademarket.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hiskytech.selfmademarket.Model.DataX
import com.hiskytech.selfmademarket.databinding.BitcoinItemBinding
import java.text.SimpleDateFormat
import java.util.Locale

class AdapterBitcoin(private val context: Context, private var bitCoinList: List<DataX>) : RecyclerView.Adapter<AdapterBitcoin.MyViewHolder>() {

    inner class MyViewHolder(val binding: BitcoinItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = BitcoinItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentPosition = bitCoinList[position]
        val originalFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val targetFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val date = originalFormat.parse(currentPosition.created_date)
        val formattedDate = targetFormat.format(date)

        holder.binding.butonPlan.text = formattedDate
        holder.binding.tvDescription.text = currentPosition.description

        val fullUrl = "https://hiskytechs.com/planemanger/uploads/${currentPosition.image}"
        Glide.with(context).load(fullUrl).into(holder.binding.img)

        val fullCoinUrl = "https://hiskytechs.com/planemanger/uploads/${currentPosition.icon}"
        Glide.with(context).load(fullCoinUrl).into(holder.binding.bitcoinImg)
    }

    fun updateList(newList: List<DataX>) {
        bitCoinList = newList
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int {
        return bitCoinList.size
    }
}