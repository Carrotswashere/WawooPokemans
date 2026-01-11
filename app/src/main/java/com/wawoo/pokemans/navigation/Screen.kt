package com.wawoo.pokemans.navigation

sealed class Screen(val route: String) {
    object GenerationList : Screen("generation_list")
    object PokemonList : Screen("pokemon_list/{generationId}/{generationName}") {
        fun createRoute(generationId: Int, generationName: String): String {
            return "pokemon_list/$generationId/$generationName"
        }
    }
    object PokemonDetail : Screen("pokemon_detail/{pokemonId}") {
        fun createRoute(pokemonId: Int): String {
            return "pokemon_detail/$pokemonId"
        }
    }
}