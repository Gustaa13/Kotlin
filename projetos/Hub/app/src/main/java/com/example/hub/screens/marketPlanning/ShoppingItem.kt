package com.example.hub.screens.marketPlanning

data class ShoppingItem(
    var name: String,
    var quantity: Int,
    var price: Double,
    var bought: Boolean = false
)