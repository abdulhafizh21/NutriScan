package com.dicoding.nutriscan.data

data class FoodRecomendation(
    val nama: String = "",
    val karbohidrat: Double = 0.0,
    val serat: Double = 0.0,
    val gambarUrl: String = ""
) {
    constructor() : this("", 0.0, 0.0, "")
}
