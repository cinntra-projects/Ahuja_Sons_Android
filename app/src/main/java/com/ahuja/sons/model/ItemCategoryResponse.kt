package com.ahuja.sons.model

data class ItemCategoryResponse(
    val `data`: List<ItemCategoryData>,
    val message: String,
    val status: Int
)