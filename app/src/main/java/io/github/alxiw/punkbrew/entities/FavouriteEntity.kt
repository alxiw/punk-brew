package io.github.alxiw.punkbrew.entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "favourites")
data class FavouriteEntity(@PrimaryKey val id: Int)