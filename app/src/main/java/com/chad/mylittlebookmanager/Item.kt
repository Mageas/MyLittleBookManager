package com.chad.mylittlebookmanager

data class Item(
    val id: Int,
    val title: String,
    val year: Int,
    val genre: List<String>,
    val rating: Double,
    val director: String,
    val actors: List<String>,
    val plot: String,
    val poster: String,
    val trailer: String,
    val runtime: Int,
    val awards: String,
    val country: String,
    val language: String,
    val boxOffice: String,
    val production: String,
    val website: String
)