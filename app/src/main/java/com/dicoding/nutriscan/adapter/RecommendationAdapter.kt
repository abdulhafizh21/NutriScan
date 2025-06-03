package com.dicoding.nutriscan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.nutriscan.R
import com.dicoding.nutriscan.data.FoodRecomendation

class RecommendationAdapter(private val recommendations: List<FoodRecomendation>) : RecyclerView.Adapter<RecommendationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recomendation, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recommendation = recommendations[position]
        holder.foodName.text = recommendation.name
        holder.foodCarb.text = "Karbohidrat ${recommendation.carbs}"
        holder.foodFiber.text = "Serat ${recommendation.fiber}"
        holder.foodImage.setImageResource(recommendation.imageResId)
    }

    override fun getItemCount(): Int {
        return recommendations.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val foodImage: ImageView = view.findViewById(R.id.foodImage)
        val foodName: TextView = view.findViewById(R.id.foodName)
        val foodCarb: TextView = view.findViewById(R.id.foodCarb)
        val foodFiber: TextView = view.findViewById(R.id.foodFiber)
    }
}
