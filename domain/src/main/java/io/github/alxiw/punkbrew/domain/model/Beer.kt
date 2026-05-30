package io.github.alxiw.punkbrew.domain.model

data class Beer(
    val id: Int,
    val number: String,
    val name: String,
    val tagline: String,
    val image: String,
    val abv: String,
    val date: String,
    val description: String,
    val favorite: Boolean
)
