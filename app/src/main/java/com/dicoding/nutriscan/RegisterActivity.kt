package com.dicoding.nutriscan

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.nutriscan.data.User
import com.dicoding.nutriscan.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {
    lateinit var binding: ActivityRegisterBinding
    lateinit var auth: FirebaseAuth
    lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance("https://login-dan-register-8e341-default-rtdb.asia-southeast1.firebasedatabase.app/")

        binding.RToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.RBtnDaftar.setOnClickListener {
            val name = binding.edtUsername.text.toString()
            val email = binding.edtEmailR.text.toString()
            val password = binding.edtPasswordR.text.toString()
            val confirmPassword = binding.edtKonfirmasiPassword.text.toString()

            if (name.isEmpty()) {
                binding.edtUsername.error = "Nama Harus Diisi"
                binding.edtUsername.requestFocus()
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                binding.edtEmailR.error = "Email Harus Diisi"
                binding.edtEmailR.requestFocus()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.edtEmailR.error = "Email Tidak Valid"
                binding.edtEmailR.requestFocus()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.edtPasswordR.error = "Password Harus Diisi"
                binding.edtPasswordR.requestFocus()
                return@setOnClickListener
            }
            if (password.length < 8) {
                binding.edtPasswordR.error = "Password Minimal 8 Karakter"
                binding.edtPasswordR.requestFocus()
                return@setOnClickListener
            }
            if (password != confirmPassword) {
                binding.edtKonfirmasiPassword.error = "Password Tidak Sama"
                binding.edtKonfirmasiPassword.requestFocus()
                return@setOnClickListener
            }

            binding.progressBar.visibility = View.VISIBLE
            binding.RBtnDaftar.isEnabled = false

            RegisterFirebase(name, email, password)
        }
    }

    private fun RegisterFirebase(name: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                binding.progressBar.visibility = View.GONE
                binding.RBtnDaftar.isEnabled = true

                if (task.isSuccessful) {
                    // Mendapatkan UID pengguna yang baru didaftarkan
                    val userId = auth.currentUser?.uid

                    // Membuat objek user untuk disimpan ke Firebase Realtime Database
                    val user = User(name, email, favorites = emptyMap())

                    // Menyimpan data pengguna ke Firebase Database
                    if (userId != null) {
                        val userRef = database.getReference("users").child(userId)
                        userRef.setValue(user).addOnCompleteListener { databaseTask ->
                            if (databaseTask.isSuccessful) {
                                Toast.makeText(this, "Register Berhasil", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this, "Gagal menyimpan data pengguna", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
