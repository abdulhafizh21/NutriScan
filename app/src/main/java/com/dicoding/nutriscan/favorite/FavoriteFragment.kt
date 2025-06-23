package com.dicoding.nutriscan.favorite

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.nutriscan.R
import com.dicoding.nutriscan.adapter.FavoriteAdapter
import com.dicoding.nutriscan.article.ArticleDetail
import com.dicoding.nutriscan.data.Article
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*



class FavoriteFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FavoriteAdapter
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorite, container, false)

        recyclerView = view.findViewById(R.id.rv_favorite)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            database = FirebaseDatabase.getInstance("https://login-dan-register-8e341-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("users").child(userId).child("favorites")

            fetchFavorites()
        }

        return view
    }

    private fun fetchFavorites() {
        // Mendapatkan data dari Firebase
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val favoritesList = mutableListOf<Article>()
                for (favoriteSnapshot in snapshot.children) {
                    val article = favoriteSnapshot.getValue(Article::class.java)
                    article?.let { favoritesList.add(it) }
                }
                // Set data ke adapter RecyclerView
                adapter = FavoriteAdapter(favoritesList)
                recyclerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity, "Error fetching data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
