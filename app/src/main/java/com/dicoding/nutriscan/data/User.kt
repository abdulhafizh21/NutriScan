package com.dicoding.nutriscan.data

data class User(
    val name: String = "",
    val email: String = "",
    val favorites: Map<String, Boolean> = emptyMap()
)

