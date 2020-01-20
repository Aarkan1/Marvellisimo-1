package com.example.marvellisimo.ui.entities

import java.io.Serializable

data class SerieEntity (
    val id: String,
    val title: String,
    val description: String,
    val uri: String,
    val endYear: Int,
    val startYear: Int,
    val rating: String): Serializable {
}