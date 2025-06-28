package com.dicoding.nutriscan.profil

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.dicoding.nutriscan.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.bumptech.glide.Glide
import android.content.Intent
import android.util.Log
import com.dicoding.nutriscan.LoginActivity
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import com.dicoding.nutriscan.R
import com.google.firebase.storage.StorageException

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference

    // Activity Result for choosing image from gallery
    private val pickImageForProfile =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                uploadProfileImageToFirebase(it)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        database =
            FirebaseDatabase.getInstance("https://login-dan-register-8e341-default-rtdb.asia-southeast1.firebasedatabase.app/")
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        // Mengambil data pengguna yang sedang login
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val userRef = database.getReference("users").child(userId)
            userRef.get().addOnSuccessListener { dataSnapshot ->
                val username = dataSnapshot.child("name").getValue(String::class.java)
                val email = auth.currentUser?.email
                val profileImageUrl =
                    dataSnapshot.child("profileImageUrl").getValue(String::class.java)

                // Menampilkan nama dan email di UI
                binding.profileName.text = "$username"
                binding.profileEmail.text = "$email"
                if (profileImageUrl != null) {
                    // Memuat gambar profil jika URL tersedia
                    Glide.with(requireContext()).load(profileImageUrl).into(binding.profileImage)
                }
            }.addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Gagal mengambil data pengguna",
                    Toast.LENGTH_SHORT
                ).show()
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
            // Aksi untuk tombol Settings
            Toast.makeText(requireContext(), "Settings clicked", Toast.LENGTH_SHORT).show()
        }

        // Tombol About
        binding.aboutButton.setOnClickListener {
            // Aksi untuk tombol About
            Toast.makeText(requireContext(), "About clicked", Toast.LENGTH_SHORT).show()
        }

        // Menangani klik untuk mengubah gambar profil
        binding.profileImage.setOnClickListener {
            // Membuka galeri untuk memilih gambar baru
            pickImageForProfile.launch("image/*")
        }

        return binding.root
    }

    private fun uploadProfileImageToFirebase(imageUri: Uri) {
        // Menampilkan ProgressBar saat gambar diunggah
        binding.progressBar.visibility = View.VISIBLE

        // Menyimpan gambar ke Firebase Storage
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val profileImageRef = FirebaseStorage.getInstance("gs://login-dan-register-8e341.firebasestorage.app")
            .reference
            .child("profile_images/$userId/profile_picture.jpg")

        profileImageRef.putFile(imageUri)
            .addOnSuccessListener {
                // Mendapatkan URL gambar setelah upload selesai
                profileImageRef.downloadUrl.addOnSuccessListener { uri ->
                    val profileImageUrl = uri.toString() // Mendapatkan URL gambar

                    // Memperbarui URL gambar profil di Firebase Realtime Database
                    val userRef = FirebaseDatabase.getInstance("https://login-dan-register-8e341-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("users").child(userId!!)
                    userRef.child("profileImageUrl").setValue(profileImageUrl)
                        .addOnSuccessListener {
                            // Gambar berhasil diperbarui, tampilkan gambar
                            Glide.with(requireContext()).load(profileImageUrl)
                                .into(binding.profileImage)
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(
                                requireContext(),
                                "Profile image updated successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .addOnFailureListener { e ->
                            Log.e("ProfileFragment", "Failed to update profile image URL", e)
                            Toast.makeText(
                                requireContext(),
                                "Failed to update profile image URL",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.progressBar.visibility = View.GONE
                        }
                }
            }
            .addOnFailureListener { exception ->
                // Jika upload gagal, tampilkan error
                Log.e("ProfileFragment", "Failed to upload image: ${exception.message}", exception)
                Toast.makeText(
                    requireContext(),
                    "Failed to upload image: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
                binding.progressBar.visibility = View.GONE
            }
    }

}
