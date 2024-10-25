package com.mihan.movie.library.di

import android.content.Context
import com.mihan.movie.library.common.utils.EventManager
import com.mihan.movie.library.common.utils.EventManagerImpl
import com.mihan.movie.library.common.utils.VoiceRecognizer
import com.mihan.movie.library.data.repository.AuthRepositoryImpl
import com.mihan.movie.library.data.repository.FavouritesRepositoryImpl
import com.mihan.movie.library.data.repository.GsonApiRepositoryImpl
import com.mihan.movie.library.data.repository.LocalVideoVideoHistoryRepositoryImpl
import com.mihan.movie.library.data.repository.ParserRepositoryImpl
import com.mihan.movie.library.domain.AuthRepository
import com.mihan.movie.library.domain.FavouritesRepository
import com.mihan.movie.library.domain.GsonApiRepository
import com.mihan.movie.library.domain.LocalVideoHistoryRepository
import com.mihan.movie.library.domain.ParserRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped

@[Module InstallIn(ActivityRetainedComponent::class)]
interface ActivityRetainedModule {

    @Binds
    fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    fun bindsParserRepository(impl: ParserRepositoryImpl): ParserRepository

    @Binds
    fun bindsAppUpdateRepository(impl: GsonApiRepositoryImpl): GsonApiRepository

    @Binds
    fun bindsLocalVideoHistoryRepository(impl: LocalVideoVideoHistoryRepositoryImpl): LocalVideoHistoryRepository

    @Binds
    fun bindsFavouritesRepository(impl: FavouritesRepositoryImpl): FavouritesRepository

    @Binds
    fun bindsEventManager(impl: EventManagerImpl): EventManager

    companion object {

        @[Provides ActivityRetainedScoped]
        fun provideVoiceRecognizer(@ApplicationContext context: Context) = VoiceRecognizer(context)
    }
}