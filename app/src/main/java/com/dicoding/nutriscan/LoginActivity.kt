package com.dicoding.nutriscan

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.nutriscan.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.LToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.LBtn1.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()

            if (email.isEmpty()) {
                binding.edtEmail.error = "Email Harus Diisi"
                binding.edtEmail.requestFocus()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.edtEmail.error = "Email tidak valid"
                binding.edtEmail.requestFocus()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.edtPassword.error = "Password Harus Diisi"
                binding.edtPassword.requestFocus()
                return@setOnClickListener
            }

            binding.progressBar.visibility = View.VISIBLE
            binding.LBtn1.isEnabled = false

            LoginFirebase(email, password)
        }

        // Fitur Lupa Kata Sandi
        binding.LForgotPassword.setOnClickListener {
            val email = binding.edtEmail.text.toString()

            if (email.isEmpty()) {
                binding.edtEmail.error = "Email harus diisi!"
                binding.edtEmail.requestFocus()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.edtEmail.error = "Email tidak valid"
                binding.edtEmail.requestFocus()
                return@setOnClickListener
            }

            resetPassword(email)
        }
    }

    private fun LoginFirebase(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                binding.progressBar.visibility = View.GONE
                binding.LBtn1.isEnabled = true

                if (task.isSuccessful) {
                    // Mendapatkan UID pengguna yang login
                    val userId = auth.currentUser?.uid

                    if (userId != null) {
                        // Mengambil username dari Firebase Realtime Database
                        val userRef = FirebaseDatabase.getInstance("https://login-dan-register-8e341-default-rtdb.asia-southeast1.firebasedatabase.app/")
                            .getReference("users").child(userId)
                        userRef.get().addOnSuccessListener { dataSnapshot ->
                            val username = dataSnapshot.child("name").getValue(String::class.java)

                            Toast.makeText(this, "Selamat Datang, $username!", Toast.LENGTH_SHORT).show()

                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                } else {
                    Toast.makeText(this, "${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Email reset password telah dikirim.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Gagal mengirim email reset password: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
