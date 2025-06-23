package com.dicoding.nutriscan.adapter

import android.content.Context
import android.content.Intent
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.nutriscan.data.HistoryItem
import com.dicoding.nutriscan.databinding.ItemHistoryBinding

class HistoryAdapter(
    private val context: Context,
    private val historyList: List<HistoryItem>,
    private val onItemClick: (HistoryItem) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val historyItem = historyList[position]
        holder.bind(historyItem)
    }

    override fun getItemCount(): Int = historyList.size

    inner class HistoryViewHolder(private val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(historyItem: HistoryItem) {
            binding.HistoryTitle.text = historyItem.name
            binding.HistoryKarbo.text = "Karbo: ${historyItem.carbohydrates} gram"
            binding.HistorySerat.text = "Serat: ${historyItem.fiber} gram"

            // Mengatur gambar dengan Glide
            Glide.with(context).load(historyItem.imageUri).into(binding.articleImage)

            // Mengatur klik listener
            itemView.setOnClickListener {
                onItemClick(historyItem) // Memanggil lambda ketika item di klik
            }
        }
    }
}
