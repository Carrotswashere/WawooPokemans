package com.wawoo.pokemans.data.repository

import com.wawoo.pokemans.data.api.PokeApiService
import com.wawoo.pokemans.data.database.GenerationDao
import com.wawoo.pokemans.data.database.PokemonDao
import com.wawoo.pokemans.data.models.Generation
import com.wawoo.pokemans.data.models.GenerationDetail
import com.wawoo.pokemans.data.models.Pokemon
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class PokemonRepository(
    private val pokeApiService: PokeApiService,
    private val generationDao: GenerationDao,
    private val pokemonDao: PokemonDao
) {
    
    fun getAllGenerations(): Flow<List<Generation>> = generationDao.getAllGenerations()

    suspend fun hasInitialData(): Boolean {
        val generations = generationDao.getAllGenerations().first()
        return generations.isNotEmpty()
    }

    suspend fun hasPokemonForGeneration(generationId: Int): Boolean {
        val pokemon = pokemonDao.getPokemonByGeneration(generationId).first()
        return pokemon.isNotEmpty()
    }

    suspend fun refreshGenerations() {
        try {
            val response = pokeApiService.getGenerations(limit = 20)
            if (response.isSuccessful) {
                val generationList = response.body()?.results?.mapIndexed { index, nameUrl ->
                    val id = nameUrl.url.split("/").dropLast(1).last().toInt()
                    Generation(id = id, name = nameUrl.name, pokemonCount = 0) // Count will be updated later
                } ?: emptyList()
                
                generationDao.insertGenerations(generationList)
            }
        } catch (e: Exception) {
            // Handle error - for now just log or ignore
        }
    }

    suspend fun getGenerationDetails(generationId: Int): GenerationDetail? {
        return try {
            val response = pokeApiService.getGenerationById(generationId)
            if (response.isSuccessful) {
                response.body()?.also { generationDetail ->
                    // Convert to Room entity and save with Pokemon count
                    val generation = Generation(
                        id = generationDetail.id,
                        name = generationDetail.name,
                        pokemonSpeciesJson = "[]", // We'll store this as JSON if needed
                        pokemonCount = generationDetail.pokemonSpecies.size
                    )
                    generationDao.insertGeneration(generation)
                }
            } else {
                // Try to get from local database and convert
                generationDao.getGenerationById(generationId)?.let { localGen ->
                    GenerationDetail(
                        id = localGen.id,
                        name = localGen.name,
                        pokemonSpecies = emptyList()
                    )
                }
            }
        } catch (e: Exception) {
            generationDao.getGenerationById(generationId)?.let { localGen ->
                GenerationDetail(
                    id = localGen.id,
                    name = localGen.name,
                    pokemonSpecies = emptyList()
                )
            }
        }
    }

    fun getPokemonByGeneration(generationId: Int): Flow<List<Pokemon>> = 
        pokemonDao.getPokemonByGeneration(generationId)

    suspend fun updateGenerationPokemonCount(generationId: Int) {
        val pokemonCount = pokemonDao.getPokemonByGeneration(generationId).first().size
        val currentGeneration = generationDao.getGenerationById(generationId)
        if (currentGeneration != null && currentGeneration.pokemonCount != pokemonCount) {
            val updatedGeneration = currentGeneration.copy(pokemonCount = pokemonCount)
            generationDao.insertGeneration(updatedGeneration)
        }
    }
    suspend fun refreshPokemonForGeneration(generationId: Int) = coroutineScope {
        try {
            val generationDetail = getGenerationDetails(generationId)
            if (generationDetail != null && generationDetail.pokemonSpecies.isNotEmpty()) {
                // Launch coroutines to fetch Pokemon concurrently for faster loading
                val pokemonJobs = generationDetail.pokemonSpecies.map { pokemonSpecies ->
                    async {
                        try {
                            val pokemonId = pokemonSpecies.url.split("/").dropLast(1).last().toInt()
                            val response = pokeApiService.getPokemonById(pokemonId)
                            if (response.isSuccessful) {
                                response.body()?.let { pokemon ->
                                    val pokemonWithGeneration = pokemon.copy(generationId = generationId)
                                    // Insert immediately - this will trigger Flow updates in real-time
                                    pokemonDao.insertPokemon(pokemonWithGeneration)
                                }
                            }
                        } catch (e: Exception) {
                            // Continue with next pokemon if one fails
                        }
                    }
                }
                
                // Wait for all Pokemon to be fetched
                pokemonJobs.forEach { it.await() }
                
                // Update the generation with actual loaded Pokemon count
                updateGenerationPokemonCount(generationId)
            } else {
                // If we can't get generation details from API, we can't proceed
                throw Exception("Unable to load generation data from API. Internet connection required for first run.")
            }
        } catch (e: Exception) {
            // Re-throw the exception so the UI can handle it
            throw e
        }
    }

    suspend fun getPokemonById(pokemonId: Int): Pokemon? {
        return try {
            // First check if we have it locally
            val localPokemon = pokemonDao.getPokemonById(pokemonId)
            
            val response = pokeApiService.getPokemonById(pokemonId)
            if (response.isSuccessful) {
                response.body()?.let { apiPokemon ->
                    // Preserve the generationId from local database if it exists
                    val pokemonToSave = if (localPokemon != null) {
                        apiPokemon.copy(generationId = localPokemon.generationId)
                    } else {
                        apiPokemon
                    }
                    pokemonDao.insertPokemon(pokemonToSave)
                    pokemonToSave
                }
            } else {
                localPokemon
            }
        } catch (e: Exception) {
            pokemonDao.getPokemonById(pokemonId)
        }
    }
}