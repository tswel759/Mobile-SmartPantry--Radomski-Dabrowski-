package com.example.mobile_smart_pantry_project_iv

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Product(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val quantity: Int,
    val category: String,
    val imageRef: String // Reference to drawable name or URL
)
