package com.wawoo.pokemans.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.wawoo.pokemans.data.repository.PokemonRepository
import com.wawoo.pokemans.presentation.screens.GenerationListScreen
import com.wawoo.pokemans.presentation.screens.PokemonDetailScreen
import com.wawoo.pokemans.presentation.screens.PokemonListScreen
import com.wawoo.pokemans.presentation.viewmodels.*

@Composable
fun PokemonNavigation(
    navController: NavHostController,
    repository: PokemonRepository,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.GenerationList.route,
        modifier = modifier
    ) {
        composable(Screen.GenerationList.route) {
            GenerationListScreen(
                onGenerationClick = { generation ->
                    navController.navigate(
                        Screen.PokemonList.createRoute(
                            generationId = generation.id,
                            generationName = generation.name
                        )
                    )
                },
                viewModelFactory = GenerationListViewModelFactory(repository)
            )
        }
        
        composable(
            route = Screen.PokemonList.route,
            arguments = listOf(
                navArgument("generationId") { type = NavType.IntType },
                navArgument("generationName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val generationId = backStackEntry.arguments?.getInt("generationId") ?: 1
            val generationName = backStackEntry.arguments?.getString("generationName") ?: ""
            
            PokemonListScreen(
                generationName = generationName,
                onPokemonClick = { pokemon ->
                    navController.navigate(
                        Screen.PokemonDetail.createRoute(pokemon.id)
                    )
                },
                onBackClick = {
                    navController.popBackStack()
                },
                viewModelFactory = PokemonListViewModelFactory(repository, generationId)
            )
        }
        
        composable(
            route = Screen.PokemonDetail.route,
            arguments = listOf(
                navArgument("pokemonId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val pokemonId = backStackEntry.arguments?.getInt("pokemonId") ?: 1
            
            PokemonDetailScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                viewModelFactory = PokemonDetailViewModelFactory(repository, pokemonId)
            )
        }
    }
}