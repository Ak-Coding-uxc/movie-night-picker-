package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.data.Movie
import com.example.data.MovieRepository
import com.example.ui.MovieViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: MovieRepository
    private lateinit var viewModel: MovieViewModel

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        
        // Setup in-memory Room database
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = MovieRepository(database.movieDao())
        viewModel = MovieViewModel(repository)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testAppName() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Movie Night Picker", appName)
    }

    @Test
    fun testFamilySafeModeFilteringAndStatistics() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)

        // Launch a coroutine to keep the StateFlows collecting/active (required for SharingStarted.WhileSubscribed)
        val moviesJob = launch(testDispatcher) {
            viewModel.movies.collect {}
        }
        val statsJob = launch(testDispatcher) {
            viewModel.statistics.collect {}
        }

        // Initially no movies inside database
        assertTrue(viewModel.movies.value.isEmpty())

        // Insert a Family-Safe movie and an Unsafe/Mature movie
        viewModel.addMovie(
            title = "A Beautiful Pixar Story",
            genre = "Animation",
            releaseYear = 2024,
            personalRating = 5f,
            isFamilySafe = true,
            isFavorite = true
        )
        viewModel.addMovie(
            title = "Scary Midnight Thriller",
            genre = "Horror",
            releaseYear = 2023,
            personalRating = 4f,
            isFamilySafe = false,
            isFavorite = false
        )

        // Retrieve initial state from Flow (unfiltered)
        val allMovies = viewModel.movies.first { it.size == 2 }
        assertEquals(2, allMovies.size)

        // Check general statistics (both movies are active in stats)
        val initialStats = viewModel.statistics.first { it.totalMovies == 2 }
        assertEquals(2, initialStats.totalMovies)
        assertEquals(1, initialStats.favoriteMovies)

        // Turn ON Family Safe Mode
        viewModel.setFamilySafeMode(true)

        // Verify that we only see the Family-Safe movie
        val filteredMovies = viewModel.movies.first { it.size == 1 }
        assertEquals("A Beautiful Pixar Story", filteredMovies[0].title)

        // Verify that statistics also updated to only reflect family safe movies (1 total, 1 favorite)
        val updatedStats = viewModel.statistics.first { it.totalMovies == 1 }
        assertEquals(1, updatedStats.totalMovies)
        assertEquals(1, updatedStats.favoriteMovies)

        // Clean up scopes
        moviesJob.cancel()
        statsJob.cancel()
    }

    @Test
    fun testPickedMovieAutoSanitizationWhenEnablingFamilyMode() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)

        // Launch monitoring jobs for flows to activate standard SharedFlow structures
        val moviesJob = launch(testDispatcher) {
            viewModel.movies.collect {}
        }
        val pickedJob = launch(testDispatcher) {
            viewModel.pickedMovie.collect {}
        }

        // Add a mature movie
        viewModel.addMovie(
            title = "Mature Adventure",
            genre = "Sci-Fi",
            releaseYear = 2025,
            personalRating = 4f,
            isFamilySafe = false
        )

        // Retrieve the movies list
        val allMovies = viewModel.movies.first { it.size == 1 }
        
        // Spin the roulette and pick it
        viewModel.pickRandomMovie()
        val pickedMovie = viewModel.pickedMovie.first { it != null }
        assertEquals("Mature Adventure", pickedMovie?.title)

        // Turn ON Family Safe Mode
        viewModel.setFamilySafeMode(true)

        // The picked movie should automatically clear itself (sanitize) because it is unsafe
        val sanitizedPicked = viewModel.pickedMovie.value
        assertNull(sanitizedPicked)

        // Clean up
        moviesJob.cancel()
        pickedJob.cancel()
    }
}
