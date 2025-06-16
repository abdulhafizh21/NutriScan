package com.dicoding.nutriscan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.nutriscan.R
import com.dicoding.nutriscan.data.FoodRecomendation

class RekomendasiAdapter(private val recommendations: List<FoodRecomendation>) : RecyclerView.Adapter<RekomendasiAdapter.RecommendationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recomendation, parent, false)
        return RecommendationViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecommendationViewHolder, position: Int) {
        val recommendation = recommendations[position]
        holder.bind(recommendation)
    }

    override fun getItemCount(): Int = recommendations.size

    class RecommendationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.foodName)
        private val carbsTextView: TextView = itemView.findViewById(R.id.foodCarb)
        private val fiberTextView: TextView = itemView.findViewById(R.id.foodFiber)
        private val imageView: ImageView = itemView.findViewById(R.id.foodImage)

        fun bind(recommendation: FoodRecomendation) {
            nameTextView.text = recommendation.nama
            carbsTextView.text = "Karbohidrat: ${recommendation.karbohidrat} g"
            fiberTextView.text = "Serat: ${recommendation.serat} g"
            Glide.with(itemView.context).load(recommendation.gambarUrl).into(imageView)
        }
    }
}
