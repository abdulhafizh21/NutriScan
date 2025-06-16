package com.dicoding.nutriscan.data

import java.io.Serializable

data class Article(
    val title: String = "",
    val description: String = "",
    val imageUrl: String = ""
) : Serializable
