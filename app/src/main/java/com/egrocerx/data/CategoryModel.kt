package com.egrocerx.data

data class CategoryModel(
    val CategoryIconPath: String = "",
    val CategoryName: String = "",
    val CategoryOrder: String = "",
    val created_at: String = "",
    val id: String = "",
    val isActive: String = "",
    val subcategories: List<Subcategory> = listOf(),
    val updated_at: String = ""
)