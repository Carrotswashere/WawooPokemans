package com.wawoo.pokemans.utils

object GenerationUtils {
    
    fun formatGenerationName(apiName: String): String {
        // Convert "generation-i" -> "Generation I"
        // Convert "generation-ii" -> "Generation II", etc.
        
        if (!apiName.startsWith("generation-")) {
            return apiName.replaceFirstChar { it.uppercase() }
        }
        
        val romanPart = apiName.removePrefix("generation-")
        val formattedRoman = when (romanPart.lowercase()) {
            "i" -> "I"
            "ii" -> "II"
            "iii" -> "III"
            "iv" -> "IV"
            "v" -> "V"
            "vi" -> "VI"
            "vii" -> "VII"
            "viii" -> "VIII"
            "ix" -> "IX"
            "x" -> "X"
            else -> romanPart.uppercase()
        }
        
        return "Generation $formattedRoman"
    }
    
    fun formatPokemonCount(count: Int): String {
        return when {
            count == 0 -> ""
            count == 1 -> "1 Pokémon"
            else -> "$count Pokémon"
        }
    }
}