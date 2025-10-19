package com.example.btvn1.model

import androidx.compose.runtime.mutableStateListOf

data class Student(
    val id: Int,
    var name: String,
    val borrowedIds: MutableList<Int> = mutableStateListOf() // ✅ sửa ở đây
)
