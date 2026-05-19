package com.example.pokehit.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BasicPokemon(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val types: List<String>
) : Parcelable