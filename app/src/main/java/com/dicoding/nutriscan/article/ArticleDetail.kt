package com.dicoding.nutriscan.article

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.nutriscan.R
import com.dicoding.nutriscan.data.Article
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.graphics.ColorFilter
import android.graphics.PorterDuff

class ArticleDetail : AppCompatActivity() {
    private lateinit var articleImage: ImageView
    private lateinit var articleName: TextView
    private lateinit var articleDescription: TextView
    private lateinit var database: DatabaseReference

    private var articleTitle: String? = null
    private lateinit var btnFavorite: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_detail)

        articleImage = findViewById(R.id.article_image)
        articleName = findViewById(R.id.tv_nama)
        articleDescription = findViewById(R.id.tv_keterangan)
        btnFavorite = findViewById(R.id.btn_add_fav)

        articleTitle = intent.getStringExtra("articleTitle")

        val btnBackDetail = findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btn_back_detail)
        btnBackDetail.setOnClickListener {
            onBackPressed()
        }

        btnFavorite.setOnClickListener {
            articleTitle?.let { title -> toggleFavorite(title) }
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

                    checkIfFavorite(articleTitle)
                } else {
                    Toast.makeText(this@ArticleDetail, "Article not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ArticleDetail, "Error fetching data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun checkIfFavorite(articleTitle: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val favoritesRef = FirebaseDatabase.getInstance("https://login-dan-register-8e341-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("users").child(userId).child("favorites")

            // Cek apakah artikel ada di favorit
            favoritesRef.child(articleTitle).get().addOnSuccessListener {
                if (it.exists()) {
                    btnFavorite.setColorFilter(resources.getColor(R.color.hijautua), PorterDuff.Mode.SRC_IN) // Warna oranye
                } else {
                    btnFavorite.setColorFilter(resources.getColor(R.color.grey), PorterDuff.Mode.SRC_IN) // Warna abu-abu
                }
            }
        }
    }

    private fun toggleFavorite(articleTitle: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val favoritesRef = FirebaseDatabase.getInstance("https://login-dan-register-8e341-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("users").child(userId).child("favorites")

            // Cek apakah artikel sudah ada di favorit
            favoritesRef.child(articleTitle).get().addOnSuccessListener {
                if (it.exists()) {
                    // Hapus artikel dari favorit
                    removeFavorite(articleTitle)
                } else {
                    // Tambahkan artikel ke favorit
                    addFavorite(articleTitle)
                }
            }
        } else {
            Toast.makeText(this@ArticleDetail, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addFavorite(articleTitle: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val favoritesRef = FirebaseDatabase.getInstance("https://login-dan-register-8e341-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("users").child(userId).child("favorites")

            database.orderByChild("title").equalTo(articleTitle).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val article = snapshot.children.firstOrNull()?.getValue(Article::class.java)
                    if (article != null) {
                        val articleData = mapOf(
                            "title" to article.title,
                            "imageUrl" to article.imageUrl,
                            "description" to article.description
                        )

                        favoritesRef.child(articleTitle).setValue(articleData).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                btnFavorite.setColorFilter(resources.getColor(R.color.hijautua), PorterDuff.Mode.SRC_IN)  // Warna berubah menjadi oranye
                                Toast.makeText(this@ArticleDetail, "Telah di tambahkan ke Favorite", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@ArticleDetail, "Gagal menambahkan ke favorites", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this@ArticleDetail, "Article not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ArticleDetail, "Error fetching data", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun removeFavorite(articleTitle: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val favoritesRef = FirebaseDatabase.getInstance("https://login-dan-register-8e341-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("users").child(userId).child("favorites")

            favoritesRef.child(articleTitle).removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    btnFavorite.setColorFilter(resources.getColor(R.color.grey), PorterDuff.Mode.SRC_IN)  // Warna kembali menjadi abu-abu
                    Toast.makeText(this@ArticleDetail, "Article di hapus dari favorites", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ArticleDetail, "Failed to remove from favorites", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
