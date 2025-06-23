package com.dicoding.nutriscan.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.nutriscan.R
import com.dicoding.nutriscan.adapter.ArticleAdapter
import com.dicoding.nutriscan.adapter.RekomendasiAdapter
import com.dicoding.nutriscan.article.ArticleFragment
import com.dicoding.nutriscan.camera.CameraOpenActivity
import com.dicoding.nutriscan.data.Article
import com.dicoding.nutriscan.data.FoodRecomendation
import com.dicoding.nutriscan.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var rekomendasiAdapter: RekomendasiAdapter
    private lateinit var articleAdapter: ArticleAdapter

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentHomeBinding.bind(view)
        binding.progressBar.visibility = View.VISIBLE
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance("https://login-dan-register-8e341-default-rtdb.asia-southeast1.firebasedatabase.app/")

        val userId = auth.currentUser?.uid
        if (userId != null) {
            val userRef = database.getReference("users").child(userId)
            userRef.get().addOnSuccessListener { dataSnapshot ->
                val username = dataSnapshot.child("name").getValue(String::class.java)
                val email = auth.currentUser?.email

                // Menampilkan nama dan email di UI
                binding.tvNewsName.text = "$username"
                binding.progressBar.visibility = View.GONE
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Gagal mengambil data pengguna", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
            }
        }

        binding.rvRecomendation.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        getRecommendations()

        binding.rvArticle.layoutManager = LinearLayoutManager(requireContext())
        fetchArticlesFromFirebase()

        binding.tvSeeAllArticle.setOnClickListener {
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            val articleFragment = ArticleFragment()
            fragmentTransaction.replace(R.id.frame_layout, articleFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        binding.scanBtn.setOnClickListener {
            val intent = Intent(activity, CameraOpenActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchArticlesFromFirebase() {
        val articlesRef = database.getReference("articles")

        binding.progressBar.visibility = View.VISIBLE

        articlesRef.get().addOnSuccessListener { dataSnapshot ->
            val articles = mutableListOf<Article>()
            for (articleSnapshot in dataSnapshot.children) {
                val article = articleSnapshot.getValue(Article::class.java)
                article?.let { articles.add(it) }
            }

            val randomArticles = articles.shuffled().take(3)

            articleAdapter = ArticleAdapter(randomArticles)
            binding.rvArticle.adapter = articleAdapter

            binding.progressBar.visibility = View.GONE
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Gagal mengambil data artikel", Toast.LENGTH_SHORT).show()
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun getRecommendations() {
        val recommendationsRef = database.getReference("rekomendasi")
        binding.progressBar.visibility = View.VISIBLE

        recommendationsRef.get().addOnSuccessListener { dataSnapshot ->
            val foodRecommendations = mutableListOf<FoodRecomendation>()
            for (foodSnapshot in dataSnapshot.children) {
                val foodRecommendation = foodSnapshot.getValue(FoodRecomendation::class.java)
                foodRecommendation?.let { foodRecommendations.add(it) }
            }

            Log.d("FirebaseData", "Fetched Recommendations: $foodRecommendations")

            if (foodRecommendations.isNotEmpty()) {
                val randomRecommendations = foodRecommendations.shuffled().take(3)

                rekomendasiAdapter = RekomendasiAdapter(randomRecommendations)
                binding.rvRecomendation.adapter = rekomendasiAdapter
            } else {
                Log.e("Firebase", "Tidak ada rekomendasi yang ditemukan!")
            }

            binding.progressBar.visibility = View.GONE
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Gagal mengambil data rekomendasi", Toast.LENGTH_SHORT).show()
            binding.progressBar.visibility = View.GONE
        }
    }
}
