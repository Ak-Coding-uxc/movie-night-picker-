package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.Movie
import com.example.data.MovieCollection
import com.example.data.MovieRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MovieViewModel(private val repository: MovieRepository) : ViewModel() {

    // Global toggle for Family Safe Mode
    private val _isFamilySafeMode = MutableStateFlow(false)
    val isFamilySafeMode: StateFlow<Boolean> = _isFamilySafeMode.asStateFlow()

    // Search query input
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Selected Collection for the details screen
    private val _selectedCollectionId = MutableStateFlow<Long?>(null)
    val selectedCollectionId: StateFlow<Long?> = _selectedCollectionId.asStateFlow()

    // Active roulette collection selection for Picker screen
    private val _rouletteCollectionId = MutableStateFlow<Long?>(null)
    val rouletteCollectionId: StateFlow<Long?> = _rouletteCollectionId.asStateFlow()

    // Movies flow combined with search and family safe mode filter
    val movies: StateFlow<List<Movie>> = combine(
        repository.allMovies,
        _searchQuery,
        _isFamilySafeMode
    ) { allMovies, query, familySafeOnly ->
        allMovies.filter { movie ->
            val matchesSearch = movie.title.contains(query, ignoreCase = true) ||
                    movie.genre.contains(query, ignoreCase = true)
            val matchesFamily = !familySafeOnly || movie.isFamilySafe
            matchesSearch && matchesFamily
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All favorites flow (filtered by family safe mode if enabled)
    val favoriteMovies: StateFlow<List<Movie>> = combine(
        repository.favoriteMovies,
        _isFamilySafeMode
    ) { favorites, familySafeOnly ->
        if (familySafeOnly) {
            favorites.filter { it.isFamilySafe }
        } else {
            favorites
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Watch history (filtered by family safe mode if enabled)
    val watchHistory: StateFlow<List<Movie>> = combine(
        repository.watchHistory,
        _isFamilySafeMode
    ) { history, familySafeOnly ->
        if (familySafeOnly) {
            history.filter { it.isFamilySafe }
        } else {
            history
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Pure custom playlists/collections flow
    val collections: StateFlow<List<MovieCollection>> = repository.allCollections
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Combined Flow: Collections with active dynamic movie counts (filtered by Family Safe Mode)
    val collectionsWithCount: StateFlow<List<Pair<MovieCollection, Int>>> = combine(
        repository.allCollections,
        repository.allMovies,
        _isFamilySafeMode
    ) { allCols, allMovies, familySafeOnly ->
        allCols.map { col ->
            val count = allMovies.count { movie ->
                movie.collectionId == col.id && (!familySafeOnly || movie.isFamilySafe)
            }
            col to count
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Movies belonging specifically to the selected collection
    val moviesInSelectedCollection: StateFlow<List<Movie>> = combine(
        repository.allMovies,
        _selectedCollectionId,
        _searchQuery,
        _isFamilySafeMode
    ) { allMovies, collectionId, query, familySafeOnly ->
        if (collectionId == null) {
            emptyList()
        } else {
            allMovies.filter { movie ->
                movie.collectionId == collectionId &&
                        (movie.title.contains(query, ignoreCase = true) || movie.genre.contains(query, ignoreCase = true)) &&
                        (!familySafeOnly || movie.isFamilySafe)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Movies eligible for random roulette run
    val pickerEligibleMovies: StateFlow<List<Movie>> = combine(
        repository.allMovies,
        _rouletteCollectionId,
        _isFamilySafeMode
    ) { allMovies, collectionId, familySafeOnly ->
        allMovies.filter { movie ->
            val matchesCollection = collectionId == null || movie.collectionId == collectionId
            val matchesFamily = !familySafeOnly || movie.isFamilySafe
            matchesCollection && matchesFamily
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Picker states
    private val _pickedMovie = MutableStateFlow<Movie?>(null)
    val pickedMovie: StateFlow<Movie?> = _pickedMovie.asStateFlow()

    private val _isPicking = MutableStateFlow(false)
    val isPicking: StateFlow<Boolean> = _isPicking.asStateFlow()

    // Statistics derived from repository lists (filtered by family safe mode if active)
    val statistics: StateFlow<MovieStats> = combine(
        repository.allMovies,
        _isFamilySafeMode
    ) { allMovies, familySafeOnly ->
        val filteredMovies = if (familySafeOnly) allMovies.filter { it.isFamilySafe } else allMovies
        val total = filteredMovies.size
        val favorites = filteredMovies.count { it.isFavorite }
        val watchedMovies = filteredMovies.filter { it.isWatched }
        
        // Find most watched genre
        val genreRanking = watchedMovies
            .groupBy { it.genre.trim() }
            .mapValues { it.value.size }
        val topGenre = genreRanking.maxByOrNull { it.value }?.key ?: "None"

        MovieStats(
            totalMovies = total,
            favoriteMovies = favorites,
            mostWatchedGenre = topGenre
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MovieStats())

    fun toggleFamilySafeMode() {
        _isFamilySafeMode.value = !_isFamilySafeMode.value
        // Instantly sanitize picked selection if it violates the newly activated safety filter
        if (_isFamilySafeMode.value) {
            val current = _pickedMovie.value
            if (current != null && !current.isFamilySafe) {
                _pickedMovie.value = null
            }
        }
    }

    fun setFamilySafeMode(active: Boolean) {
        _isFamilySafeMode.value = active
        // Instantly sanitize picked selection if it violates the newly activated safety filter
        if (active) {
            val current = _pickedMovie.value
            if (current != null && !current.isFamilySafe) {
                _pickedMovie.value = null
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCollection(id: Long?) {
        _selectedCollectionId.value = id
    }

    fun setRouletteCollectionId(id: Long?) {
         _rouletteCollectionId.value = id
        // Sanitize active pick when switching filters
        _pickedMovie.value = null
    }

    // Movie list operations
    fun addMovie(
        title: String,
        genre: String,
        releaseYear: Int,
        personalRating: Float,
        isFamilySafe: Boolean,
        isFavorite: Boolean = false,
        collectionId: Long? = null
    ) {
        viewModelScope.launch {
            val movie = Movie(
                title = title,
                genre = genre,
                releaseYear = releaseYear,
                personalRating = personalRating,
                isFamilySafe = isFamilySafe,
                isFavorite = isFavorite,
                isWatched = false,
                collectionId = collectionId
            )
            repository.insertMovie(movie)
        }
    }

    fun deleteMovie(movie: Movie) {
        viewModelScope.launch {
            repository.deleteMovie(movie)
        }
    }

    fun toggleFavorite(movie: Movie) {
        viewModelScope.launch {
            repository.updateMovie(movie.copy(isFavorite = !movie.isFavorite))
        }
    }

    fun toggleWatched(movie: Movie) {
        viewModelScope.launch {
            val isNowWatched = !movie.isWatched
            val timestamp = if (isNowWatched) System.currentTimeMillis() else null
            repository.updateMovie(
                movie.copy(
                    isWatched = isNowWatched,
                    watchedTimestamp = timestamp
                )
            )
        }
    }

    // Collection management operations
    fun addCollection(name: String, iconName: String = "Movie") {
        viewModelScope.launch {
            val collection = MovieCollection(name = name, iconName = iconName)
            repository.insertCollection(collection)
        }
    }

    fun renameCollection(collection: MovieCollection, newName: String) {
        viewModelScope.launch {
            repository.updateCollection(collection.copy(name = newName))
        }
    }

    fun deleteCollection(collection: MovieCollection) {
        viewModelScope.launch {
            repository.deleteCollection(collection)
            // If the deleted collection is currently selected in UI, clear selection
            if (_selectedCollectionId.value == collection.id) {
                _selectedCollectionId.value = null
            }
            if (_rouletteCollectionId.value == collection.id) {
                _rouletteCollectionId.value = null
            }
        }
    }

    // Picker Operation with simulation of animation
    fun pickRandomMovie() {
        val eligibleMovies = pickerEligibleMovies.value
        if (eligibleMovies.isEmpty()) {
            _pickedMovie.value = null
            return
        }

        viewModelScope.launch {
            _isPicking.value = true
            // Play a rolling animation by randomly shuffling choices rapidly
            if (eligibleMovies.size > 1) {
                var delayMs = 50L
                val steps = 15
                for (i in 1..steps) {
                    _pickedMovie.value = eligibleMovies.random()
                    delay(delayMs)
                    delayMs += 20L // slow down gradually
                }
            }
            // Final pick
            _pickedMovie.value = eligibleMovies.random()
            _isPicking.value = false
        }
    }

    fun clearPickedMovie() {
        _pickedMovie.value = null
    }
}

data class MovieStats(
    val totalMovies: Int = 0,
    val favoriteMovies: Int = 0,
    val mostWatchedGenre: String = "None"
)

class MovieViewModelFactory(private val repository: MovieRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovieViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MovieViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
