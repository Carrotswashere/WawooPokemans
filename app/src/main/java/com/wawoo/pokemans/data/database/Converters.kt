package com.wawoo.pokemans.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wawoo.pokemans.data.models.*

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromNameUrlPairList(value: List<NameUrlPair>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toNameUrlPairList(value: String): List<NameUrlPair> {
        val listType = object : TypeToken<List<NameUrlPair>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromSprites(sprites: Sprites): String {
        return gson.toJson(sprites)
    }

    @TypeConverter
    fun toSprites(value: String): Sprites {
        return gson.fromJson(value, Sprites::class.java)
    }

    @TypeConverter
    fun fromTypeSlotList(value: List<TypeSlot>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toTypeSlotList(value: String): List<TypeSlot> {
        val listType = object : TypeToken<List<TypeSlot>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromAbilitySlotList(value: List<AbilitySlot>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toAbilitySlotList(value: String): List<AbilitySlot> {
        val listType = object : TypeToken<List<AbilitySlot>>() {}.type
        return gson.fromJson(value, listType)
    }
}