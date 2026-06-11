package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies ORDER BY title ASC")
    fun getAllMovies(): Flow<List<Movie>>

    @Query("SELECT * FROM movies WHERE isFamilySafe = 1 ORDER BY title ASC")
    fun getFamilySafeMovies(): Flow<List<Movie>>

    @Query("SELECT * FROM movies WHERE isFavorite = 1 ORDER BY title ASC")
    fun getFavoriteMovies(): Flow<List<Movie>>

    @Query("SELECT * FROM movies WHERE isWatched = 1 ORDER BY watchedTimestamp DESC")
    fun getWatchHistory(): Flow<List<Movie>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: Movie): Long

    @Update
    suspend fun updateMovie(movie: Movie)

    @Delete
    suspend fun deleteMovie(movie: Movie)

    @Query("SELECT * FROM movies WHERE id = :id LIMIT 1")
    suspend fun getMovieById(id: Long): Movie?

    @Query("SELECT * FROM collections ORDER BY name ASC")
    fun getAllCollections(): Flow<List<MovieCollection>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollection(collection: MovieCollection): Long

    @Update
    suspend fun updateCollection(collection: MovieCollection)

    @Delete
    suspend fun deleteCollection(collection: MovieCollection)

    @Query("SELECT * FROM movies WHERE collectionId = :collectionId ORDER BY title ASC")
    fun getMoviesForCollection(collectionId: Long): Flow<List<Movie>>

    @Query("UPDATE movies SET collectionId = NULL WHERE collectionId = :collectionId")
    suspend fun clearCollectionReferences(collectionId: Long)
}
