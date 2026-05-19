package com.example.pokehit.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritePokemonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToFavorites(entity: FavoritePokemonEntity)

    @Query("DELETE FROM favorite_pokemon WHERE pokemonId = :pokemonId")
    suspend fun removeFromFavorites(pokemonId: Int)

    @Query("SELECT * FROM favorite_pokemon")
    suspend fun getAllFavorites(): List<FavoritePokemonEntity>

    @Query("SELECT * FROM favorite_pokemon")
    fun getFavoritesFlow(): Flow<List<FavoritePokemonEntity>>

    @Query("SELECT COUNT(*) FROM favorite_pokemon WHERE pokemonId = :pokemonId")
    suspend fun isFavorite(pokemonId: Int): Int

    @Query("DELETE FROM favorite_pokemon")
    suspend fun clearAllFavorites()
}