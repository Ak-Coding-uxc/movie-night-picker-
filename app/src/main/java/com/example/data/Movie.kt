package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val genre: String,
    val releaseYear: Int,
    val personalRating: Float, // 1 to 5 stars
    val isFavorite: Boolean = false,
    val isFamilySafe: Boolean = false,
    val isWatched: Boolean = false,
    val watchedTimestamp: Long? = null,
    val collectionId: Long? = null
)
