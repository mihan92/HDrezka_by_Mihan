package com.mihan.movie.library.domain.usecases.favourites

import com.mihan.movie.library.domain.FavouritesRepository
import com.mihan.movie.library.domain.models.FavouritesModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoritesUseCase @Inject constructor(private val favouritesRepository: FavouritesRepository) {

    operator fun invoke(): Flow<List<FavouritesModel>> =
        favouritesRepository.getFavourites()
}