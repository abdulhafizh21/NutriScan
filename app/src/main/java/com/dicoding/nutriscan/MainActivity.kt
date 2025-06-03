@file:Suppress("DEPRECATION")

package com.dicoding.nutriscan

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.dicoding.nutriscan.article.ArticleFragment
import com.dicoding.nutriscan.camera.CameraFragment
import com.dicoding.nutriscan.databinding.ActivityMainBinding
import com.dicoding.nutriscan.favorite.FavoriteFragment
import com.dicoding.nutriscan.home.HomeFragment
import com.dicoding.nutriscan.profil.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set default fragment to be displayed
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        // Set listener for Bottom Navigation
        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.navigation_favorite -> {
                    loadFragment(FavoriteFragment())
                    true
                }
                R.id.navigation_camera -> {
                    loadFragment(CameraFragment())
                    true
                }
                R.id.navigation_article -> {
                    loadFragment(ArticleFragment())
                    true
                }
                R.id.navigation_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .commit()
    }
}