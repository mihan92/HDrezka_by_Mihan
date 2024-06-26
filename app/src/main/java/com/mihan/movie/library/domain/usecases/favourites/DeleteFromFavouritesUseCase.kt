package com.mihan.movie.library.domain.usecases.favourites

import com.mihan.movie.library.domain.FavouritesRepository
import javax.inject.Inject

class DeleteFromFavouritesUseCase @Inject constructor(private val favouritesRepository: FavouritesRepository) {

    suspend operator fun invoke(videoId: String) =
        favouritesRepository.deleteFromFavourites(videoId)
}