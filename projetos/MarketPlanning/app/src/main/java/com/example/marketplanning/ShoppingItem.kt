package com.example.marketplanning

data class ShoppingItem(
    var name: String,
    var quantity: Int,
    var price: Double,
    var bought: Boolean = false
)