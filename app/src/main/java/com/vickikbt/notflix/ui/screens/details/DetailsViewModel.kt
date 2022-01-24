package com.vickikbt.notflix.ui.screens.details

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.vickikbt.domain.models.Cast
import com.vickikbt.domain.models.MovieDetails
import com.vickikbt.domain.models.MovieVideo
import com.vickikbt.domain.models.SimilarMovies
import com.vickikbt.repository.repository.movie_details_repository.MovieDetailsRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

class DetailsViewModel(
    private val movieDetailsRepository: MovieDetailsRepository
) : ViewModel() {

    private val _movieDetails = MutableLiveData<MovieDetails>()
    val movieDetails: LiveData<MovieDetails> get() = _movieDetails

    private val _movieCast = MutableLiveData<Cast>()
    val movieCast: LiveData<Cast> get() = _movieCast

    private val _movieVideo = MutableLiveData<MovieVideo>()
    val movieVideo: LiveData<MovieVideo> get() = _movieVideo

    private val _similarMovies = MutableLiveData<SimilarMovies>()
    val similarMovies: LiveData<SimilarMovies> get() = _similarMovies

    private val _movieIsFavorite = MutableLiveData<Boolean>()
    val movieIsFavorite: LiveData<Boolean?> get() = _movieIsFavorite

    fun getMovieDetails(movieId: Int) = viewModelScope.launch {
        movieDetailsRepository.getMovieDetails(movieId).collectLatest {
            _movieDetails.value = it
        }
    }

    fun getMovieCast(movieId: Int) = viewModelScope.launch {
        movieDetailsRepository.getMovieCast(movieId).collectLatest {
            _movieCast.value = it
        }
    }

    fun getMovieVideo(movieId: Int) = viewModelScope.launch {
        movieDetailsRepository.getMovieVideos(movieId).collectLatest {
            _movieVideo.value = it
        }
    }

    fun fetchSimilarMovies(movieId: Int) = viewModelScope.launch {
        movieDetailsRepository.fetchSimilarMovies(movieId).collectLatest {
            _similarMovies.value = it
        }
    }

    fun getImagePalette(drawable: Drawable, onGenerated: (Palette.Swatch) -> Unit) {
        val bitmap = (drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)
        Palette.from(bitmap).generate { palette ->
            val vibrantSwatch = palette?.vibrantSwatch
            val dominantSwatch = palette?.vibrantSwatch

            if (vibrantSwatch != null) {
                onGenerated(vibrantSwatch)
            } else if (dominantSwatch != null) {
                onGenerated(dominantSwatch)
            }
        }
    }

    fun saveMovieDetails(movieDetails: MovieDetails, cast: Cast, movieVideo: MovieVideo?) =
        viewModelScope.launch {
            movieDetailsRepository.apply {
                saveMovieDetails(movieDetails)
                saveMovieCast(cast)
                if (movieVideo != null) {
                    saveMovieVideos(movieVideo)
                }
            }
        }

    fun updateFavorite(cacheId: Int, isFavorite: Boolean) {
        Timber.e("Updating : $cacheId to $isFavorite")
        viewModelScope.launch {
            movieDetailsRepository.updateMovieIsFavorite(cacheId, isFavorite)
        }
    }

    fun getIsMovieFavorite(movieId: Int) {
        viewModelScope.launch {
            movieDetailsRepository.isMovieFavorite(movieId).collectLatest {
                _movieIsFavorite.value = it
            }
        }
    }

}
