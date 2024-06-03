package com.mihan.movie.library.domain

import com.mihan.movie.library.domain.models.FavouritesModel
import kotlinx.coroutines.flow.Flow

interface FavouritesRepository {

    fun getFavourites(): Flow<List<FavouritesModel>>

    fun getFavouriteById(videoId: String): Flow<FavouritesModel?>

    suspend fun addToFavourites(favouritesModel: FavouritesModel)

    suspend fun deleteFromFavourites(videoId: String)
}