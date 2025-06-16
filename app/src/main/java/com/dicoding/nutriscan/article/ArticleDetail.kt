package com.dicoding.nutriscan.article

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.dicoding.nutriscan.R
import com.dicoding.nutriscan.data.Article
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class ArticleDetail : AppCompatActivity() {
    private lateinit var articleImage: ImageView
    private lateinit var articleName: TextView
    private lateinit var articleDescription: TextView
    private lateinit var database: DatabaseReference
    private var articleTitle: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_detail)

        articleImage = findViewById(R.id.article_image)
        articleName = findViewById(R.id.tv_nama)
        articleDescription = findViewById(R.id.tv_keterangan)

        articleTitle = intent.getStringExtra("articleTitle")

        Log.d("ArticleDetail", "Received articleTitle: $articleTitle")
        val btnBackDetail = findViewById<AppCompatButton>(R.id.btn_back_detail)
        btnBackDetail.setOnClickListener {
            onBackPressed()
        }
        if (articleTitle != null) {
            database = FirebaseDatabase.getInstance("https://login-dan-register-8e341-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("articles")

            fetchArticleDetails(articleTitle!!)
        } else {
            Toast.makeText(this, "Article title is missing!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchArticleDetails(articleTitle: String) {

        database.orderByChild("title").equalTo(articleTitle).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val article = snapshot.children.firstOrNull()?.getValue(Article::class.java)
                if (article != null) {
                    articleName.text = article.title
                    articleDescription.text = article.description
                    Picasso.get().load(article.imageUrl).into(articleImage)
                } else {
                    Log.e("ArticleDetail", "Article with title $articleTitle not found in database")
                    Toast.makeText(this@ArticleDetail, "Article not found in database", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ArticleDetail", "Error fetching data: ${error.message}")
                Toast.makeText(this@ArticleDetail, "Error fetching data", Toast.LENGTH_SHORT).show()
            }
        })

    }
}
