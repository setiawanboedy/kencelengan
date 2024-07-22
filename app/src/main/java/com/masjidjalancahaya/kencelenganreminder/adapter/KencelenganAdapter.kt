package com.masjidjalancahaya.kencelenganreminder.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.masjidjalancahaya.kencelenganreminder.R
import com.masjidjalancahaya.kencelenganreminder.databinding.ItemDonaturBinding
import com.masjidjalancahaya.kencelenganreminder.data.model.KencelenganModel
import com.masjidjalancahaya.kencelenganreminder.utils.OnItemAdapterListener


class KencelenganAdapter(private val context: Context, private val listener: OnItemAdapterListener): ListAdapter<KencelenganModel, KencelenganAdapter.ViewHolder>(differCallback) {

    companion object{
        val differCallback = object : DiffUtil.ItemCallback<KencelenganModel>(){
            override fun areItemsTheSame(
                oldItem: KencelenganModel,
                newItem: KencelenganModel
            ): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: KencelenganModel,
                newItem: KencelenganModel
            ): Boolean = oldItem == newItem

        }
    }


    inner class ViewHolder(private val binding: ItemDonaturBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(data: KencelenganModel, listener: OnItemAdapterListener){


            with(binding){
                tvName.text = data.name
                tvTelp.text = data.nomor.toString()
                tvAddress.text = data.address

                if (data.isBlue!!){
                    val cyanColor = ContextCompat.getColor(context, R.color.cyan)
                    itemDonatur.setCardBackgroundColor(cyanColor)
                }else{
                    val cyanColor = ContextCompat.getColor(context, R.color.grey)
                    itemDonatur.setCardBackgroundColor(cyanColor)
                }

                selected.setOnClickListener {
                    listener.onPrimaryClick(data)
                }
                ibDirection.setOnClickListener {
                    listener.onSecondaryClick(data)
                }
                selected.setOnLongClickListener {
                    listener.onLongPressedClick(data)
                    true
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDonaturBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) holder.bind(currentItem, listener)
    }


}