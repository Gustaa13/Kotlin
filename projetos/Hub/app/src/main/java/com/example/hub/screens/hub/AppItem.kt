package com.example.hub.screens.hub

data class AppItem(
    val title: String,
    val subtitle: String,
    val iconRes: Int,
    val onClick: () -> Unit
)
