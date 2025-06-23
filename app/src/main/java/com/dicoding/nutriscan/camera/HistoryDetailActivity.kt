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

        // Ambil data dari Intent
        val category = intent.getStringExtra("deskripsi")
        val imageUri = intent.getStringExtra("imageUri")
        val nama = intent.getStringExtra("nama")
        val deskripsi = intent.getStringExtra("kategori")
        val karbo = intent.getFloatExtra("karbo", 0f)
        val serat = intent.getFloatExtra("serat", 0f)
        val akurasi = intent.getFloatExtra("akurasi", 0f)

        // Tampilkan data pada layout
        Glide.with(this).load(imageUri).into(binding.captureImage) // Menampilkan gambar
        binding.tvNamaObjek.text = nama
        binding.tvKelas.text = category
        binding.tvKeterangan.text = deskripsi
        binding.tvNilaiKarbo.text = "$karbo gram"
        binding.tvNilaiSerat.text = "$serat gram"
        binding.tvAkurasi.text = "Kecocokan: ${String.format("%.1f", akurasi * 100)}%"

        // Update ProgressBar untuk Karbohidrat jika nilai karbohidrat tidak null
        if (karbo != null) {
            val nilaiPbKarbo = karbo * 10 // Atur konversi nilai yang sesuai
            setProgressBarKarbo(binding.pbKarbo, nilaiPbKarbo.toDouble()) // Karbohidrat max = 370
        }
        binding.tvKarbohidrat.text = "Karbohidrat:"

        // Update ProgressBar untuk Serat jika nilai serat tidak null
        if (serat != null) {
            val nilaiPbSerat = serat * 100 // Atur konversi nilai yang sesuai
            setProgressBarSerat(binding.pbSerat, nilaiPbSerat.toDouble()) // Serat max = 360
        }
        binding.tvSerat.text = "Serat:"
        binding.btnBack.setOnClickListener {
            onBackPressed() // Menutup activity ini dan kembali ke activity sebelumnya
        }
    }

    // Fungsi untuk mengupdate ProgressBar Karbohidrat
    private fun setProgressBarKarbo(pbKarbo: ProgressBar, value: Double) {
        val max = 370
        pbKarbo.max = max

        // Menentukan warna progressBar berdasarkan nilai karbohidrat
        if (value / max >= 0.5) {
            pbKarbo.progressDrawable = ContextCompat.getDrawable(this, R.drawable.layer_green)
        } else {
            pbKarbo.progressDrawable = ContextCompat.getDrawable(this, R.drawable.layer_red)
        }

        // Animasi progress dengan ValueAnimator untuk Karbohidrat
        val animator = ValueAnimator.ofInt(0, value.toInt())
        animator.duration = 2000 // Durasi animasi 2 detik
        animator.addUpdateListener { animation ->
            pbKarbo.progress = animation.animatedValue as Int
        }
        animator.start()
    }

    // Fungsi untuk mengupdate ProgressBar Serat
    private fun setProgressBarSerat(pbSerat: ProgressBar, value: Double) {
        val max = 360
        pbSerat.max = max

        // Menentukan warna progressBar berdasarkan nilai serat
        if (value / max >= 0.5) {
            pbSerat.progressDrawable = ContextCompat.getDrawable(this, R.drawable.layer_green)
        } else {
            pbSerat.progressDrawable = ContextCompat.getDrawable(this, R.drawable.layer_red)
        }

        // Animasi progress dengan ValueAnimator untuk Serat
        val animator = ValueAnimator.ofInt(0, value.toInt())
        animator.duration = 2000 // Durasi animasi 2 detik
        animator.addUpdateListener { animation ->
            pbSerat.progress = animation.animatedValue as Int
        }
        animator.start()
    }
}
