package com.example.hub.screens.hub

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hub.databinding.ItemAppCardBinding

class AppCardAdapter(private val items: List<AppItem>) :
    RecyclerView.Adapter<AppCardAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemAppCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AppItem) {
            binding.txtName.text = item.title
            binding.txtSubtitle.text = item.subtitle
            binding.imgIcon.setImageResource(item.iconRes)
            binding.root.setOnClickListener { item.onClick() }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAppCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
