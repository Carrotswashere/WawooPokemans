package com.wawoo.pokemans.data.database

import androidx.room.*
import com.wawoo.pokemans.data.models.Pokemon
import kotlinx.coroutines.flow.Flow

@Dao
interface PokemonDao {
    @Query("SELECT * FROM pokemon WHERE generationId = :generationId ORDER BY id")
    fun getPokemonByGeneration(generationId: Int): Flow<List<Pokemon>>

    @Query("SELECT * FROM pokemon WHERE id = :id")
    suspend fun getPokemonById(id: Int): Pokemon?

    @Query("SELECT * FROM pokemon WHERE name = :name")
    suspend fun getPokemonByName(name: String): Pokemon?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemon(pokemon: Pokemon)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemonList(pokemon: List<Pokemon>)

    @Query("DELETE FROM pokemon WHERE generationId = :generationId")
    suspend fun deletePokemonByGeneration(generationId: Int)

    @Query("DELETE FROM pokemon")
    suspend fun deleteAllPokemon()
}