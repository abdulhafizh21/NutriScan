package com.dicoding.nutriscan.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.nutriscan.R
import com.dicoding.nutriscan.adapter.ArticleAdapter
import com.dicoding.nutriscan.adapter.RecommendationAdapter
import com.dicoding.nutriscan.camera.CameraFragment
import com.dicoding.nutriscan.data.Article
import com.dicoding.nutriscan.data.FoodRecomendation
import com.dicoding.nutriscan.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var recommendationAdapter: RecommendationAdapter
    private lateinit var articleAdapter: ArticleAdapter

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentHomeBinding.bind(view)
        binding.progressBar.visibility = View.VISIBLE

        auth = FirebaseAuth.getInstance()
        // Menggunakan URL Firebase Realtime Database yang sesuai
        database = FirebaseDatabase.getInstance("https://login-dan-register-8e341-default-rtdb.asia-southeast1.firebasedatabase.app/")
        // Mengambil data pengguna yang sedang login
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
                Toast.makeText(
                    requireContext(),
                    "Gagal mengambil data pengguna",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        // Set up RecyclerViews
        binding.rvRecomendation.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recommendationAdapter = RecommendationAdapter(getRecommendations())
        binding.rvRecomendation.adapter = recommendationAdapter

        binding.rvArticle.layoutManager = LinearLayoutManager(requireContext())
        articleAdapter = ArticleAdapter(getArticles())
        binding.rvArticle.adapter = articleAdapter

        // Handle the scan button click (this could be an action for scanning)
        binding.scanBtn.setOnClickListener {
            // Membuat instance dari CameraFragment
            val cameraFragment = CameraFragment()

            // Mulai transaksi fragment untuk mengganti fragment yang sedang berjalan
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, cameraFragment) // Ganti dengan ID container tempat fragment ditempatkan
                .addToBackStack(null) // Menambahkan fragment ke backstack agar bisa kembali ke fragment sebelumnya
                .commit()
        }

    }

    // Dummy data for Recommendations
    private fun getRecommendations(): List<FoodRecomendation> {
        return listOf(
            FoodRecomendation("Wortel", "10g", "2.3g", R.drawable.timun),
            FoodRecomendation("timun", "14g", "2.4g", R.drawable.timun),
            FoodRecomendation("Apel", "24g", "2.4g", R.drawable.timun)
        )
    }

    // Dummy data for Articles
    private fun getArticles(): List<Article> {
        return listOf(
            Article("Wortel", "Wortel memiliki warna oranye cerah yang dikenal dengan rasa manis alami.", R.drawable.timun),
            Article("Timun", "Timun memiliki kandungan air yang tinggi dan rendah kalori.", R.drawable.timun),
            Article("Apel", "Timun memiliki kandungan air yang tinggi dan rendah kalori.", R.drawable.timun)
        )
    }
}