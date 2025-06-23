package com.dicoding.nutriscan.data

data class HistoryItem(
    val imageUri: String,
    val name: String,
    val carbohydrates: Float,
    val fiber: Float,
    val accuracy: Float,
    val description: String,
    val category: String
)

