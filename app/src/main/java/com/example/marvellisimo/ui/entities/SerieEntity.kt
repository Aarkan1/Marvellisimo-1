package com.example.marvellisimo.ui.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class SerieEntity (
    val id: String,
    val title: String,
    val description: String,
    val uri: String,
    val endYear: Int,
    val startYear: Int,
    val rating: String): Parcelable {
}