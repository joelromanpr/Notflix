package com.vickikbt.cache.models

data class UpcomingMoviesEntity(

    val dates: DatesEntity?,

    val page: Int?,

    val movies: List<MovieEntity>?,

    val totalPages: Int?,

    val totalResults: Int?
)