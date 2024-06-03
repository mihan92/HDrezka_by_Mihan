package com.mihan.movie.library.domain.usecases

import com.mihan.movie.library.domain.FavouritesRepository
import com.mihan.movie.library.domain.models.FavouritesModel
import javax.inject.Inject

class AddToFavouritesUseCase @Inject constructor(private val favouritesRepository: FavouritesRepository) {

    suspend operator fun invoke(favouritesModel: FavouritesModel) =
        favouritesRepository.addToFavourites(favouritesModel)
}