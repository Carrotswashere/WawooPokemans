package com.wawoo.pokemans.data.database

import androidx.room.*
import com.wawoo.pokemans.data.models.Generation
import kotlinx.coroutines.flow.Flow

@Dao
interface GenerationDao {
    @Query("SELECT * FROM generations ORDER BY id")
    fun getAllGenerations(): Flow<List<Generation>>

    @Query("SELECT * FROM generations WHERE id = :id")
    suspend fun getGenerationById(id: Int): Generation?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGeneration(generation: Generation)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGenerations(generations: List<Generation>)

    @Query("DELETE FROM generations")
    suspend fun deleteAllGenerations()
}