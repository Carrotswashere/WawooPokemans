package com.wawoo.pokemans.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "generations")
data class Generation(
    @PrimaryKey
    val id: Int,
    val name: String,
    val pokemonSpeciesJson: String = "[]", // Store as JSON string for Room
    val pokemonCount: Int = 0 // Track number of Pokemon in this generation
)

data class NameUrlPair(
    val name: String,
    val url: String
)

data class GenerationResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<NameUrlPair>
)

// API response for generation details - separate from Room entity
data class GenerationDetail(
    val id: Int,
    val name: String,
    @SerializedName("pokemon_species")
    val pokemonSpecies: List<NameUrlPair> = emptyList()
)