package com.mihan.movie.library.di

import android.app.DownloadManager
import android.content.Context
import androidx.room.Room
import com.mihan.movie.library.common.Constants
import com.mihan.movie.library.common.utils.DownloadManagerImpl
import com.mihan.movie.library.common.utils.IDownloadManager
import com.mihan.movie.library.data.local.db.FavouritesDao
import com.mihan.movie.library.data.local.db.FavouritesDataBase
import com.mihan.movie.library.data.local.db.VideoHistoryDao
import com.mihan.movie.library.data.local.db.VideoHistoryDataBase
import com.mihan.movie.library.data.remote.GsonApiService
import com.mihan.movie.library.data.remote.RemoteParserApiService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
interface AppModule {

    @Binds
    fun provideIDownloadManager(impl: DownloadManagerImpl): IDownloadManager

    companion object {

        @Provides
        @Singleton
        fun provideSystemDownloadManager(@ApplicationContext context: Context): DownloadManager =
            context.getSystemService(DownloadManager::class.java)

        @Provides
        @Singleton
        fun provideRemoteParserApiService(retrofit: Retrofit.Builder): RemoteParserApiService = retrofit
            .baseUrl(Constants.REMOTE_PARSER_API_BASE_URL)
            .build()
            .create(RemoteParserApiService::class.java)

        @Provides
        @Singleton
        fun provideGsonApiService(retrofit: Retrofit.Builder): GsonApiService = retrofit
            .baseUrl(Constants.GSON_API_BASE_URL)
            .build()
            .create(GsonApiService::class.java)

        @Provides
        @Singleton
        fun provideInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BASIC)

        @Provides
        @Singleton
        fun provideOkHttpClient(interceptor: HttpLoggingInterceptor): OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        @Provides
        @Singleton
        fun provideRetrofit(client: OkHttpClient): Retrofit.Builder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)

        @Provides
        @Singleton
        fun provideHistoryDataBase(@ApplicationContext appContext: Context): VideoHistoryDataBase =
            Room.databaseBuilder(
                appContext,
                VideoHistoryDataBase::class.java,
                HISTORY_DB_NAME
            ).build()

        @Provides
        @Singleton
        fun provideVideoHistoryDao(videoHistoryDataBase: VideoHistoryDataBase): VideoHistoryDao =
            videoHistoryDataBase.videoHistoryDao()

        @Provides
        @Singleton
        fun providesFavouritesDataBase(@ApplicationContext appContext: Context): FavouritesDataBase =
            Room.databaseBuilder(
                appContext,
                FavouritesDataBase::class.java,
                FAVOURITES_DB_NAME
            ).build()

        @Provides
        @Singleton
        fun providesFavouritesDao(favouritesDataBase: FavouritesDataBase): FavouritesDao =
            favouritesDataBase.favouritesDao()

        private const val HISTORY_DB_NAME = "video_history"
        private const val FAVOURITES_DB_NAME = "video_favourites"
    }
}