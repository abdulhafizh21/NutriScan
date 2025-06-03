package com.dicoding.nutriscan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.nutriscan.R
import com.dicoding.nutriscan.data.Article

class ArticleAdapter(private val articles: List<Article>) : RecyclerView.Adapter<ArticleAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_article, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = articles[position]
        holder.articleTitle.text = article.title
        holder.articleDescription.text = article.description
        holder.articleImage.setImageResource(article.imageResId)
    }

    override fun getItemCount(): Int {
        return articles.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val articleImage: ImageView = view.findViewById(R.id.articleImage)
        val articleTitle: TextView = view.findViewById(R.id.articleTitle)
        val articleDescription: TextView = view.findViewById(R.id.articleDescription)
    }
}
