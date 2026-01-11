package com.wawoo.pokemans.data.api

import com.wawoo.pokemans.data.models.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface  PokeApiService {
    @GET("generation")
    suspend fun getGenerations(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): Response<GenerationResponse>

    @GET("generation/{id}")
    suspend fun getGenerationById(@Path("id") id: Int): Response<GenerationDetail>

    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): Response<PokemonListResponse>

    @GET("pokemon/{id}")
    suspend fun getPokemonById(@Path("id") id: Int): Response<Pokemon>

    @GET("pokemon/{name}")
    suspend fun getPokemonByName(@Path("name") name: String): Response<Pokemon>
}