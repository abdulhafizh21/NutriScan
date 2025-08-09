package com.dicoding.nutriscan.About

import android.os.Bundle
import android.text.SpannableString
import android.text.style.BulletSpan
import android.view.View
import android.widget.GridLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicoding.nutriscan.R
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val backButton: AppCompatButton = findViewById(R.id.btn_back)
        backButton.setOnClickListener {
            onBackPressed()
        }

        val plants = listOf(
            "Bayam", "Jagung", "Jambu Air", "Jambu Biji", "Jeruk",
            "Kacang Panjang", "Kangkung", "Kelapa", "Kentang", "Kubis",
            "Mangga", "Melon", "Mentimun", "Nanas", "Pepaya", "Pisang",
            "Semangka", "Singkong", "Terung", "Tomat", "Ubi Ungu", "Wortel"
        )

        // GridLayout to display the plants in two columns
        val gridLayout: GridLayout = findViewById(R.id.gridLayout)

        // Loop through the plants list and add them to the GridLayout
        for (plant in plants) {
            // Create a new TextView for each plant
            val textView = TextView(this)
            textView.text = "• $plant"  // Add bullet point before the plant name
            textView.textSize = 16f
            textView.fontFeatureSettings = "font-family=Roboto-Medium"
            textView.setTextColor(resources.getColor(R.color.white)) // Or set the color you want
            textView.setPadding(8, 8, 8, 8) // Padding for spacing

            // Add each TextView dynamically to the GridLayout
            gridLayout.addView(textView)
        }
    }
}