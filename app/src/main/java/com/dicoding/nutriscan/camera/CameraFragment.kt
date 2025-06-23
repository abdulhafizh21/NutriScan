package com.dicoding.nutriscan.camera

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.nutriscan.adapter.HistoryAdapter
import com.dicoding.nutriscan.data.HistoryItem
import com.dicoding.nutriscan.databinding.FragmentCameraBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CameraFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var historyList: MutableList<HistoryItem>
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var binding: FragmentCameraBinding
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCameraBinding.inflate(inflater, container, false)

        recyclerView = binding.historyRecyclerView
        historyList = mutableListOf()
        historyAdapter = HistoryAdapter(requireContext(), historyList) { historyItem ->
            openDetailActivity(historyItem)
        }

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = historyAdapter

        progressBar = binding.progressBar

        binding.btnAdd.setImageTintList(ColorStateList.valueOf(resources.getColor(android.R.color.white)))
        binding.btnAdd.setOnClickListener {
            val intent = Intent(requireContext(), CameraOpenActivity::class.java)
            startActivity(intent)
        }

        fetchHistoryFromFirebase()

        return binding.root
    }

    private fun openDetailActivity(historyItem: HistoryItem) {
        val intent = Intent(requireContext(), HistoryDetailActivity::class.java).apply {
            putExtra("imageUri", historyItem.imageUri)
            putExtra("nama", historyItem.name)
            putExtra("deskripsi", historyItem.description)
            putExtra("karbo", historyItem.carbohydrates)
            putExtra("serat", historyItem.fiber)
            putExtra("akurasi", historyItem.accuracy)
            putExtra("kategori", historyItem.category)
        }
        startActivity(intent)
    }

    private fun fetchHistoryFromFirebase() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userId = user.uid
            val historyRef = FirebaseDatabase.getInstance("https://login-dan-register-8e341-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("users").child(userId).child("history")

            progressBar.visibility = View.VISIBLE

            historyRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    historyList.clear()
                    for (data in snapshot.children) {
                        val category = data.child("kategori").value.toString()
                        val name = data.child("nama").value.toString()
                        val description = data.child("deskripsi").value.toString()
                        val carbohydrates = (data.child("karbo").value as? Double)?.toFloat() ?: 0f
                        val fiber = (data.child("serat").value as? Double)?.toFloat() ?: 0f
                        val accuracy = (data.child("akurasi").value as? Double)?.toFloat() ?: 0f
                        val imageUri = data.child("imageUri").value.toString()

                        val historyItem = HistoryItem(imageUri, name, carbohydrates, fiber, accuracy, category, description)
                        historyList.add(historyItem)
                    }

                    progressBar.visibility = View.GONE

                    historyAdapter.notifyDataSetChanged()


                    checkRecyclerViewData()
                }

                override fun onCancelled(error: DatabaseError) {
                    progressBar.visibility = View.GONE
                }
            })
        }
    }

    private fun checkRecyclerViewData() {
        if (historyList.isEmpty()) {
            binding.cCamera.visibility = View.VISIBLE
            binding.cTxt1.visibility = View.VISIBLE
            binding.cTxt2.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            binding.cCamera.visibility = View.GONE
            binding.cTxt1.visibility = View.GONE
            binding.cTxt2.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }
}
