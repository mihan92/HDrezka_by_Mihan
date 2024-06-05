package com.mihan.movie.library.data.repository

import com.mihan.movie.library.data.local.db.FavouritesDao
import com.mihan.movie.library.data.models.toFavouritesModel
import com.mihan.movie.library.domain.FavouritesRepository
import com.mihan.movie.library.domain.models.FavouritesModel
import com.mihan.movie.library.domain.models.toFavouritesDbModel
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ActivityRetainedScoped
class FavouritesRepositoryImpl @Inject constructor(
    private val favouritesDao: FavouritesDao
): FavouritesRepository {

    override fun getFavourites(): Flow<List<FavouritesModel>> {
        return favouritesDao.getFavourites().map { list -> list.map { it.toFavouritesModel() } }
    }

    override fun getFavouriteById(videoId: String): Flow<FavouritesModel?> {
        return favouritesDao.getFavouriteVideoById(videoId).map { it?.toFavouritesModel() }
    }

    override suspend fun addToFavourites(favouritesModel: FavouritesModel) {
        favouritesDao.addToFavourites(favouritesModel.toFavouritesDbModel())
    }

    override suspend fun deleteFromFavourites(videoId: String) {
        favouritesDao.deleteFromFavourites(videoId)
    }
}