package com.masjidjalancahaya.kencelenganreminder.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.masjidjalancahaya.kencelenganreminder.databinding.ItemDonaturBinding
import com.masjidjalancahaya.kencelenganreminder.model.KencelenganModel
import timber.log.Timber

class KencelenganAdapter: ListAdapter<KencelenganModel, KencelenganAdapter.ViewHolder>(differCallback) {
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
        fun bind(data: KencelenganModel, listener: ((KencelenganModel) -> Unit)?){

            with(binding){
                tvName.text = data.name
                tvTelp.text = data.nomor.toString()
                tvAddress.text = data.address

                selected.setOnClickListener {
                    listener?.let {
                        listener(data)
                    }
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

    private var listener : ((KencelenganModel) -> Unit)? = null
    fun setOnItemClick(listener: (KencelenganModel) -> Unit){
        this.listener = listener
    }
}