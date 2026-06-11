package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "collections")
data class MovieCollection(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val iconName: String = "Movie" // Custom preset icon name (e.g. Comedy, Marvel, Horror, Anime)
)
