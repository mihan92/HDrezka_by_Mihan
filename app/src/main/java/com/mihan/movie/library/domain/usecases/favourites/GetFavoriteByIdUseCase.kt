package com.mihan.movie.library.domain.usecases.favourites

import com.mihan.movie.library.domain.FavouritesRepository
import com.mihan.movie.library.domain.models.FavouritesModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoriteByIdUseCase @Inject constructor(private val favouritesRepository: FavouritesRepository) {

    operator fun invoke(videoId: String): Flow<FavouritesModel?> =
        favouritesRepository.getFavouriteById(videoId)
}