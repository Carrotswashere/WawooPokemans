package com.wawoo.pokemans.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.wawoo.pokemans.data.models.Generation
import com.wawoo.pokemans.data.models.Pokemon

@Database(
    entities = [Generation::class, Pokemon::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class PokemonDatabase : RoomDatabase() {
    abstract fun generationDao(): GenerationDao
    abstract fun pokemonDao(): PokemonDao

    companion object {
        @Volatile
        private var INSTANCE: PokemonDatabase? = null

        fun getDatabase(context: Context): PokemonDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PokemonDatabase::class.java,
                    "pokemon_database"
                )
                .fallbackToDestructiveMigration() // Handle schema changes by recreating DB
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}