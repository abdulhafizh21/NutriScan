package com.dicoding.nutriscan.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.nutriscan.R
import com.dicoding.nutriscan.article.ArticleDetail
import com.dicoding.nutriscan.data.Article
import com.squareup.picasso.Picasso

class FavoriteAdapter(private val favoriteList: List<Article>) : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    var onItemClick: ((Article) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_favorite, parent, false)
        return FavoriteViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val article = favoriteList[position]
        holder.articleTitle.text = article.title
        holder.articleDescription.text = article.description
        Picasso.get().load(article.imageUrl).into(holder.articleImage)

        holder.itemView.setOnClickListener {
            val intent = Intent(it.context, ArticleDetail::class.java)
            intent.putExtra("articleTitle", article.title) // Mengirim title artikel ke ArticleDetail
            it.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = favoriteList.size

    inner class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val articleImage: ImageView = itemView.findViewById(R.id.articleImage)
        val articleTitle: TextView = itemView.findViewById(R.id.articleTitle)
        val articleDescription: TextView = itemView.findViewById(R.id.articleDescription)
    }
}
