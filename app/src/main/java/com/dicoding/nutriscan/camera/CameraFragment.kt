package com.dicoding.nutriscan.camera

//import android.os.Bundle
//import androidx.fragment.app.Fragment
//import android.view.View
//import com.dicoding.nutriscan.R
//import android.content.Intent
//import android.content.res.ColorStateList
//import com.google.android.material.floatingactionbutton.FloatingActionButton
//
//class CameraFragment : Fragment(R.layout.fragment_camera) {
//
//    private lateinit var btnAdd: FloatingActionButton
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        // Inisialisasi FloatingActionButton
//        btnAdd = view.findViewById(R.id.btn_add)
//
//        // Mengubah warna ikon tombol menjadi putih
//        btnAdd.setImageTintList(ColorStateList.valueOf(resources.getColor(android.R.color.white)))
//
//        // Set listener untuk button
//        btnAdd.setOnClickListener {
//            // Mengarahkan ke CameraOpenActivity saat tombol ditekan
//            openCamera()
//        }
//    }
//
//    private fun openCamera() {
//        // Membuka CameraOpenActivity menggunakan Intent
//        val intent = Intent(requireContext(), CameraOpenActivity::class.java)
//        startActivity(intent)
//    }
//}

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dicoding.nutriscan.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CameraFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = inflater.inflate(R.layout.fragment_camera, container, false)

        val btnAdd: FloatingActionButton = binding.findViewById(R.id.btn_add)
        btnAdd.setImageTintList(ColorStateList.valueOf(resources.getColor(android.R.color.white)))

        // Menangani klik pada btn_add
        btnAdd.setOnClickListener {
            val intent = Intent(activity, CameraOpenActivity::class.java)
            startActivity(intent)
        }

        return binding
    }
}
