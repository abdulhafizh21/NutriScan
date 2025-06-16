package com.dicoding.nutriscan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.nutriscan.R
import com.dicoding.nutriscan.data.Article
import android.content.Intent
import com.dicoding.nutriscan.article.ArticleDetail
import com.squareup.picasso.Picasso

class ArticleAdapter(private val articles: List<Article>) :
    RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_article, parent, false)
        return ArticleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = articles[position]
        holder.articleTitle.text = article.title
        holder.articleDescription.text = article.description
        Picasso.get().load(article.imageUrl).into(holder.articleImage)

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            // Pindah ke detail artikel saat artikel diklik
            val intent = Intent(context, ArticleDetail::class.java)
            intent.putExtra("articleTitle", article.title)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = articles.size

    class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val articleTitle: TextView = itemView.findViewById(R.id.articleTitle)
        val articleDescription: TextView = itemView.findViewById(R.id.articleDescription)
        val articleImage: ImageView = itemView.findViewById(R.id.articleImage)
    }
}
