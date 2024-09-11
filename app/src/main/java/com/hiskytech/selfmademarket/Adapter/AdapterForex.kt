package com.hiskytech.selfmademarket.Adapter

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hiskytech.selfmademarket.Model.DataXX
import com.hiskytech.selfmademarket.R
import com.hiskytech.selfmademarket.databinding.ForexItemBinding
import java.text.SimpleDateFormat
import java.util.Locale

class AdapterForex(
    private val context: Context,
    private var forexList: List<DataXX>
) : RecyclerView.Adapter<AdapterForex.MyViewHolder>() {

    inner class MyViewHolder(val binding: ForexItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ForexItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = forexList[position]

        // Parsing and formatting the date
        val originalFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val targetFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = originalFormat.parse(currentItem.created_date)
        val formattedDate = targetFormat.format(date)

        holder.binding.butonPlan.text = formattedDate
        holder.binding.tvForexTittle.text = currentItem.title
        holder.binding.tvForexDescription.text = currentItem.description

        // Set click listener for the zoom button
        holder.binding.zoomBtn.setOnClickListener {
            val dialogView = Dialog(context)
            dialogView.setContentView(R.layout.dialog_image_view)
            dialogView.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

            // Use PhotoView instead of ImageView for imgFull
            val imgFull = dialogView.findViewById<com.github.chrisbanes.photoview.PhotoView>(R.id.imgFull)
            val cross = dialogView.findViewById<ImageView>(R.id.cross)

            val fullUrl = "https://en.selfmademarket.net/planemanger/uploads/${currentItem.image}"
            Glide.with(context).load(fullUrl).into(imgFull)

            dialogView.show()

            cross.setOnClickListener {
                dialogView.dismiss()
            }

            dialogView.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

  holder.binding.cvImg.setOnClickListener {
            val dialogView = Dialog(context)
            dialogView.setContentView(R.layout.dialog_image_view)
            dialogView.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

            // Use PhotoView instead of ImageView for imgFull
            val imgFull = dialogView.findViewById<com.github.chrisbanes.photoview.PhotoView>(R.id.imgFull)
            val cross = dialogView.findViewById<ImageView>(R.id.cross)

            val fullUrl = "https://en.selfmademarket.net/planemanger/uploads/${currentItem.image}"
            Glide.with(context).load(fullUrl).into(imgFull)

            dialogView.show()

            cross.setOnClickListener {
                dialogView.dismiss()
            }

            dialogView.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }


        // Load image into item view
        val fullUrl = "https://en.selfmademarket.net/planemanger/uploads/${currentItem.image}"
        Glide.with(context).load(fullUrl).into(holder.binding.img)
    }

    // Update the list with sorting
    fun updateList(newList: List<DataXX>) {
        forexList = newList.sortedByDescending {
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(it.created_date)
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = forexList.size
}
