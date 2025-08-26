package com.dicoding.nutriscan.camera

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.dicoding.nutriscan.R
import com.dicoding.nutriscan.databinding.ActivityHistoryDetailBinding



class HistoryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val category = intent.getStringExtra("deskripsi")
        val imageUri = intent.getStringExtra("imageUri")
        val nama = intent.getStringExtra("nama")
        val deskripsi = intent.getStringExtra("kategori")
        val karbo = intent.getFloatExtra("karbo", 0f)
        val serat = intent.getFloatExtra("serat", 0f)
        val akurasi = intent.getFloatExtra("akurasi", 0f)

        Glide.with(this).load(imageUri).into(binding.captureImage)
        binding.tvNamaObjek.text = nama
        binding.tvKelas.text = category
        binding.tvKeterangan.text = deskripsi
        binding.tvNilaiKarbo.text = "$karbo gram"
        binding.tvNilaiSerat.text = "$serat gram"
        binding.tvAkurasi.text = "Kecocokan: ${String.format("%.1f", akurasi * 100)}%"

        if (karbo != null) {
            val nilaiPbKarbo = karbo * 10
            setProgressBarKarbo(binding.pbKarbo, nilaiPbKarbo.toDouble())
        }
        binding.tvKarbohidrat.text = "Karbohidrat:"

        if (serat != null) {
            val nilaiPbSerat = serat * 100
            setProgressBarSerat(binding.pbSerat, nilaiPbSerat.toDouble())
        }
        binding.tvSerat.text = "Serat:"
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setProgressBarKarbo(pbKarbo: ProgressBar, value: Double) {
        val max = 370
        pbKarbo.max = max

        if (value / max >= 0.5) {
            pbKarbo.progressDrawable = ContextCompat.getDrawable(this, R.drawable.layer_green)
        } else {
            pbKarbo.progressDrawable = ContextCompat.getDrawable(this, R.drawable.layer_red)
        }

        val animator = ValueAnimator.ofInt(0, value.toInt())
        animator.duration = 2000
        animator.addUpdateListener { animation ->
            pbKarbo.progress = animation.animatedValue as Int
        }
        animator.start()
    }

    private fun setProgressBarSerat(pbSerat: ProgressBar, value: Double) {
        val max = 360
        pbSerat.max = max

        if (value / max >= 0.5) {
            pbSerat.progressDrawable = ContextCompat.getDrawable(this, R.drawable.layer_green)
        } else {
            pbSerat.progressDrawable = ContextCompat.getDrawable(this, R.drawable.layer_red)
        }

        val animator = ValueAnimator.ofInt(0, value.toInt())
        animator.duration = 2000
        animator.addUpdateListener { animation ->
            pbSerat.progress = animation.animatedValue as Int
        }
        animator.start()
    }
}
