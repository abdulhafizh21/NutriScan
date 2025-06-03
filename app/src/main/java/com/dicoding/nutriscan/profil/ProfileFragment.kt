package com.dicoding.nutriscan.profil

import com.dicoding.nutriscan.LoginActivity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.Toolbar
import com.dicoding.nutriscan.R
import com.dicoding.nutriscan.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

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
                binding.profileName.text = "$username"
                binding.profileEmail.text = "$email"
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Gagal mengambil data pengguna", Toast.LENGTH_SHORT).show()
            }
        }

        // Set up Toolbar
        val toolbar: Toolbar = binding.root.findViewById(R.id.toolbar)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        // Sembunyikan judul default
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        val toolbarTitle: TextView = binding.root.findViewById(R.id.toolbar_title)
        toolbarTitle.text = "Profile"

        // Tombol Logout
        binding.logoutButton.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        // Tombol Settings
        binding.settingsButton.setOnClickListener {
            // Aksi untuk tombol Settings, misalnya membuka fragment atau activity baru
            Toast.makeText(requireContext(), "Settings clicked", Toast.LENGTH_SHORT).show()
        }

        // Tombol About
        binding.aboutButton.setOnClickListener {
            // Aksi untuk tombol About, misalnya membuka fragment atau activity baru
            Toast.makeText(requireContext(), "About clicked", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }
}
