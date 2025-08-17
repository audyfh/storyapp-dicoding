package com.example.storyapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "story")
data class StoryEntity(
    val createdAt: String,
    val description: String,
    @PrimaryKey
    val id: String,
    val lat: Double?,
    val lon: Double?,
    val name: String,
    val photoUrl: String
)