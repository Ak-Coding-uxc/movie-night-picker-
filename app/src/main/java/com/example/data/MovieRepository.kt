package com.example.data

import kotlinx.coroutines.flow.Flow

class MovieRepository(private val movieDao: MovieDao) {
    val allMovies: Flow<List<Movie>> = movieDao.getAllMovies()
    val familySafeMovies: Flow<List<Movie>> = movieDao.getFamilySafeMovies()
    val favoriteMovies: Flow<List<Movie>> = movieDao.getFavoriteMovies()
    val watchHistory: Flow<List<Movie>> = movieDao.getWatchHistory()

    // Collections flows
    val allCollections: Flow<List<MovieCollection>> = movieDao.getAllCollections()

    suspend fun insertMovie(movie: Movie): Long = movieDao.insertMovie(movie)

    suspend fun updateMovie(movie: Movie) = movieDao.updateMovie(movie)

    suspend fun deleteMovie(movie: Movie) = movieDao.deleteMovie(movie)

    suspend fun getMovieById(id: Long): Movie? = movieDao.getMovieById(id)

    // Collection Operations
    suspend fun insertCollection(collection: MovieCollection): Long = movieDao.insertCollection(collection)

    suspend fun updateCollection(collection: MovieCollection) = movieDao.updateCollection(collection)

    suspend fun deleteCollection(collection: MovieCollection) {
        movieDao.clearCollectionReferences(collection.id)
        movieDao.deleteCollection(collection)
    }

    fun getMoviesForCollection(collectionId: Long): Flow<List<Movie>> {
        return movieDao.getMoviesForCollection(collectionId)
    }
}
