package com.wawoo.pokemans

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.wawoo.pokemans.data.api.ApiClient
import com.wawoo.pokemans.data.database.PokemonDatabase
import com.wawoo.pokemans.data.repository.PokemonRepository

class PokemonApplication : Application(), ImageLoaderFactory {
    
    val database by lazy { PokemonDatabase.getDatabase(this) }
    val repository by lazy { 
        PokemonRepository(
            pokeApiService = ApiClient.pokeApiService,
            generationDao = database.generationDao(),
            pokemonDao = database.pokemonDao()
        ) 
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25) // Use 25% of available memory
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(50 * 1024 * 1024) // 50MB disk cache
                    .build()
            }
            .respectCacheHeaders(false) // Cache images even without cache headers
            .build()
    }
}