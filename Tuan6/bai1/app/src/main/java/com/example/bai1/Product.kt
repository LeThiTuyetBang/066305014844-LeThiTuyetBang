package com.example.bai1

import com.google.gson.annotations.SerializedName

data class Product(
    val id: String,
    val name: String,
    @SerializedName("des") val description: String,
    val price: Double,
    @SerializedName("imgURL") val image: String
)
