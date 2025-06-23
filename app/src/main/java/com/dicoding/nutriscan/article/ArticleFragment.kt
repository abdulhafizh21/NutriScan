package com.dicoding.nutriscan.article

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.nutriscan.R
import com.dicoding.nutriscan.adapter.ArticleAdapter
import com.dicoding.nutriscan.data.Article
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ArticleFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ArticleAdapter
    private lateinit var searchView: SearchView
    private lateinit var database: DatabaseReference
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_article, container, false)

        recyclerView = view.findViewById(R.id.rv_article)
        searchView = view.findViewById(R.id.searchBar)
        progressBar = view.findViewById(R.id.progressBar)

        recyclerView.layoutManager = LinearLayoutManager(activity)

        database = FirebaseDatabase.getInstance("https://login-dan-register-8e341-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("articles")

        fetchArticles()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    searchArticles(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    fetchArticles()
                }
                return true
            }
        })

        return view
    }

    private fun fetchArticles() {
        progressBar.visibility = View.VISIBLE

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val articles = mutableListOf<Article>()
                for (articleSnapshot in snapshot.children) {
                    val article = articleSnapshot.getValue(Article::class.java)
                    article?.let { articles.add(it) }
                }

                adapter = ArticleAdapter(articles)
                recyclerView.adapter = adapter

                progressBar.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity, "Error fetching data", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
            }
        })
    }

    private fun searchArticles(query: String) {
        progressBar.visibility = View.VISIBLE

        database.orderByChild("title").startAt(query).endAt(query + "\uf8ff")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val articles = mutableListOf<Article>()
                    for (articleSnapshot in snapshot.children) {
                        val article = articleSnapshot.getValue(Article::class.java)
                        article?.let { articles.add(it) }
                    }

                    adapter = ArticleAdapter(articles)
                    recyclerView.adapter = adapter

                    progressBar.visibility = View.GONE
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(activity, "Error fetching data", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                }
            })
    }
}
