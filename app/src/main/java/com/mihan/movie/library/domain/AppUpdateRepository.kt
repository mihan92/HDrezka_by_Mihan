package com.mihan.movie.library.domain

import com.mihan.movie.library.domain.models.ChangelogModel

interface AppUpdateRepository {
    suspend fun checkUpdates(): ChangelogModel
}