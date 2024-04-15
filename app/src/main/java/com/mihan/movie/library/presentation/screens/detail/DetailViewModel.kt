package com.mihan.movie.library.presentation.screens.detail

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.mihan.movie.library.common.ApiResponse
import com.mihan.movie.library.common.Constants.EMPTY_STRING
import com.mihan.movie.library.common.DataStorePrefs
import com.mihan.movie.library.common.extentions.logger
import com.mihan.movie.library.common.listeners.EventManager
import com.mihan.movie.library.common.utils.whileUiSubscribed
import com.mihan.movie.library.domain.models.FavouritesModel
import com.mihan.movie.library.domain.models.SeasonModel
import com.mihan.movie.library.domain.models.StreamModel
import com.mihan.movie.library.domain.models.VideoHistoryModel
import com.mihan.movie.library.domain.models.VideoModel
import com.mihan.movie.library.domain.usecases.AddToFavouritesUseCase
import com.mihan.movie.library.domain.usecases.DeleteFromFavouritesUseCase
import com.mihan.movie.library.domain.usecases.GetDetailVideoByUrlUseCase
import com.mihan.movie.library.domain.usecases.GetFavoriteByIdUseCase
import com.mihan.movie.library.domain.usecases.GetSeasonsByTranslatorIdUseCase
import com.mihan.movie.library.domain.usecases.GetStreamsBySeasonIdUseCase
import com.mihan.movie.library.domain.usecases.GetStreamsByTranslatorIdUseCase
import com.mihan.movie.library.domain.usecases.GetTranslationsByUrlUseCase
import com.mihan.movie.library.domain.usecases.GetVideoHistoryByIdUseCase
import com.mihan.movie.library.domain.usecases.UpdateVideoHistoryUseCase
import com.mihan.movie.library.presentation.screens.navArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getDetailVideoByUrlUseCase: GetDetailVideoByUrlUseCase,
    private val getTranslationsByUrlUseCase: GetTranslationsByUrlUseCase,
    private val getStreamsBySeasonIdUseCase: GetStreamsBySeasonIdUseCase,
    private val getSeasonsByTranslatorIdUseCase: GetSeasonsByTranslatorIdUseCase,
    private val getStreamsByTranslatorIdUseCase: GetStreamsByTranslatorIdUseCase,
    private val updateVideoHistoryUseCase: UpdateVideoHistoryUseCase,
    private val getVideoHistoryByIdUseCase: GetVideoHistoryByIdUseCase,
    private val getFavoriteByIdUseCase: GetFavoriteByIdUseCase,
    private val addToFavouritesUseCase: AddToFavouritesUseCase,
    private val deleteFromFavouritesUseCase: DeleteFromFavouritesUseCase,
    private val eventManager: EventManager,
    private val dataStorePrefs: DataStorePrefs,
    savedStateHandle: SavedStateHandle,
    application: Application
) : AndroidViewModel(application) {
    private val navArgs: DetailScreenNavArgs = savedStateHandle.navArgs()
    private val _screenState = MutableStateFlow(DetailScreenState())
    private val _showFilmDialog = MutableStateFlow(false)
    private val _showSerialDialog = MutableStateFlow(false)
    private val _listOfStreams = MutableStateFlow<List<StreamModel>>(emptyList())
    private val _videoData = MutableStateFlow(VideoModel())
    private var _translatorId = _videoData.value.translations.values.firstOrNull()
    private var _translatorName = _videoData.value.translations.keys.firstOrNull()
    private val _listOfSeasons = MutableStateFlow<List<SeasonModel>>(emptyList())
    private var _seasonAndEpisodeTitle = Pair(EMPTY_STRING, EMPTY_STRING)
    private val _videoQuality = dataStorePrefs.getVideoQuality().shareIn(viewModelScope, whileUiSubscribed, 1)
    private val _videoHistoryModel = MutableStateFlow<VideoHistoryModel?>(null)
    private val _isVideoHasFavourites = MutableStateFlow(false)

    val videoData = _videoData.asStateFlow()
    val showFilmDialog = _showFilmDialog.asStateFlow()
    val showSerialDialog = _showSerialDialog.asStateFlow()
    val screenState = _screenState.asStateFlow()
    val listOfSeasons = _listOfSeasons.asStateFlow()
    val videoHistoryModel = _videoHistoryModel.asStateFlow()
    val isVideoHasFavourites = _isVideoHasFavourites.asStateFlow()

    init {
        getVideoDetailInfo()
        updateListOfStreams()
    }

    fun getTranslations() {
        viewModelScope.launch {
            getTranslationsByUrlUseCase(navArgs.movieUrl)
                .onEach { result ->
                    when (result) {
                        is ApiResponse.Error -> {
                            _screenState.value = DetailScreenState(detailInfo = _screenState.value.detailInfo)
                            eventManager.sendEvent(result.errorMessage)
                        }

                        is ApiResponse.Loading -> _screenState.value = DetailScreenState(
                            detailInfo = _screenState.value.detailInfo,
                            isLoading = true
                        )

                        is ApiResponse.Success -> {
                            _screenState.value = DetailScreenState(detailInfo = _screenState.value.detailInfo)
                            _videoData.value = result.data
                            _translatorId = result.data.translations.values.first()
                            _translatorName = result.data.translations.keys.first()
                            if (result.data.isVideoHasSeries) {
                                if (_videoHistoryModel.value != null) {
                                    _translatorId = _videoHistoryModel.value?.translatorId!!
                                    _translatorName = _videoHistoryModel.value?.translatorName
                                    getSeasonsByTranslatorId(_videoHistoryModel.value?.translatorId!!)
                                } else
                                    getSeasonsByTranslatorId(result.data.translations.values.first())
                            } else
                                updateData()
                        }
                    }
                }.last()
        }
    }

    fun selectTranslateForFilms(translatorId: String) {
        viewModelScope.launch {
            getStreamsByTranslatorId(translatorId)
        }
    }

    fun selectTranslateForSerials(translationId: String) {
        _translatorId = translationId
        _translatorName = _videoData.value.translations.entries.firstOrNull { it.value == translationId }?.key
        getSeasonsByTranslatorId(translationId)
    }

    fun onButtonFavouritesClick() {
        viewModelScope.launch {
            val videoPageUrl = Uri.parse(navArgs.movieUrl).path ?: EMPTY_STRING
            val videoInfo = _screenState.value.detailInfo
            if (_isVideoHasFavourites.value)
                deleteFromFavouritesUseCase(videoInfo?.videoId ?: EMPTY_STRING)
            else {
                addToFavouritesUseCase(
                    FavouritesModel(
                        videoId = videoInfo?.videoId ?: EMPTY_STRING,
                        videoPageUrl = videoPageUrl,
                        videoTitle = videoInfo?.title ?: EMPTY_STRING,
                        posterUrl = videoInfo?.imageUrl ?: EMPTY_STRING
                    )
                )
            }
        }
    }

    fun onEpisodeClicked(season: String, episode: String) {
        _seasonAndEpisodeTitle = season to episode
        _translatorId?.let { id ->
            getStreamsBySeasonId(id, _videoData.value.videoId, season, episode)
        }
    }

    fun onDialogDismiss() {
        _showFilmDialog.value = false
        _showSerialDialog.value = false
    }

    private fun getVideoDetailInfo() {
        viewModelScope.launch {
            getDetailVideoByUrlUseCase(navArgs.movieUrl)
                .onEach { result ->
                    when (result) {
                        is ApiResponse.Error -> {
                            _screenState.value = DetailScreenState(isLoading = false)
                            eventManager.sendEvent(result.errorMessage)
                        }

                        is ApiResponse.Loading -> _screenState.value = DetailScreenState(isLoading = true)
                        is ApiResponse.Success -> {
                            _screenState.value = DetailScreenState(detailInfo = result.data)
                            getFavourites(result.data.videoId)
                            getVideoHistoryData(result.data.videoId)
                        }
                    }
                }.last()
        }
    }

    private fun getFavourites(videoId: String) {
        getFavoriteByIdUseCase(videoId)
            .onEach { favouritesModel ->
                _isVideoHasFavourites.update { favouritesModel != null }
            }.launchIn(viewModelScope)
    }

    private fun getVideoHistoryData(videoId: String) {
        getVideoHistoryByIdUseCase(videoId)
            .onEach { historyModel ->
                _videoHistoryModel.update { historyModel }
            }.launchIn(viewModelScope)
    }

    private fun updateData() {
        if (_listOfSeasons.value.isEmpty() && !_videoData.value.isVideoHasTranslations) {
            //Фильм без переводов
            logger("Фильм без переводов")
            val defaultTranslate = _videoData.value.translations.entries.first().value
            viewModelScope.launch {
                getStreamsByTranslatorId(defaultTranslate)
            }
        } else if (_listOfSeasons.value.isEmpty()) {
            //Фильм с переводами
            showFilmDialog()
            logger("Фильм с переводами")
        } else if (_videoData.value.isVideoHasSeries && !_videoData.value.isVideoHasTranslations) {
            //Сериал без переводов
            showSerialDialog()
            logger("Сериал без переводов")
        } else if (_videoData.value.isVideoHasSeries) {
            //Сериал с переводами
            showSerialDialog()
            logger("Сериал с переводами")
        }
    }

    private suspend fun getStreamsByTranslatorId(translatorId: String) {
        getStreamsByTranslatorIdUseCase(translatorId)
            .onEach { result ->
                when (result) {
                    is ApiResponse.Error -> {
                        _screenState.value = DetailScreenState(detailInfo = _screenState.value.detailInfo)
                        eventManager.sendEvent(result.errorMessage)
                    }

                    is ApiResponse.Loading -> _screenState.value = DetailScreenState(
                        detailInfo = _screenState.value.detailInfo,
                        isLoading = true
                    )

                    is ApiResponse.Success -> {
                        _listOfStreams.update { result.data }
                        _screenState.value = DetailScreenState(
                            detailInfo = _screenState.value.detailInfo,
                            isLoading = false
                        )
                    }
                }
            }.last()
    }

    private fun getSeasonsByTranslatorId(translatorId: String) {
        viewModelScope.launch {
            getSeasonsByTranslatorIdUseCase(translatorId)
                .onEach { result ->
                    when (result) {
                        is ApiResponse.Error -> {
                            _screenState.value = DetailScreenState(detailInfo = _screenState.value.detailInfo)
                            eventManager.sendEvent(result.errorMessage)
                        }

                        is ApiResponse.Loading -> Unit
                        is ApiResponse.Success -> {
                            _listOfSeasons.update { result.data }
                            updateData()
                        }
                    }
                }.last()
        }
    }

    private fun getStreamsBySeasonId(translationId: String, videoId: String, season: String, episode: String) {
        viewModelScope.launch {
            getStreamsBySeasonIdUseCase(translationId, videoId, season, episode)
                .onEach { result ->
                    when (result) {
                        is ApiResponse.Error -> {
                            _screenState.value = DetailScreenState(detailInfo = _screenState.value.detailInfo)
                            eventManager.sendEvent(result.errorMessage)
                        }

                        is ApiResponse.Loading -> _screenState.value = DetailScreenState(
                            detailInfo = _screenState.value.detailInfo,
                            isLoading = true
                        )

                        is ApiResponse.Success -> {
                            _screenState.value = DetailScreenState(detailInfo = _screenState.value.detailInfo)
                            _listOfStreams.value = result.data
                        }
                    }
                }.last()
        }
    }

    private fun updateListOfStreams() {
        _listOfStreams.onEach { list ->
            if (list.isNotEmpty())
                sendIntent(list)
        }.launchIn(viewModelScope)
    }

    private fun sendIntent(selectedStream: List<StreamModel>) {
        viewModelScope.launch {
            try {
                val videoStream =
                    selectedStream.firstOrNull { it.quality == _videoQuality.first().quality } ?: selectedStream.last()
                val videoUrl = videoStream.url
                var title = screenState.value.detailInfo?.title
                if (videoData.value.isVideoHasSeries)
                    title += getSeasonTitle(_seasonAndEpisodeTitle.first, _seasonAndEpisodeTitle.second)
                title += "  (${videoStream.quality})"

                val context = getApplication<Application>().applicationContext
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl)).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    setDataAndType(Uri.parse(videoUrl), "video/*")
                    putExtra("title", title)
                }
                context.startActivity(intent)
                _screenState.value.detailInfo?.let { detailInfo ->
                    val watchingTime = System.currentTimeMillis()
                    val videoPageUrl = Uri.parse(navArgs.movieUrl).path
                    val model = VideoHistoryModel(
                        videoId = detailInfo.videoId,
                        videoPageUrl = videoPageUrl ?: EMPTY_STRING,
                        videoTitle = detailInfo.title,
                        posterUrl = detailInfo.imageUrl,
                        translatorName = _translatorName ?: EMPTY_STRING,
                        translatorId = _translatorId ?: EMPTY_STRING,
                        season = _seasonAndEpisodeTitle.first,
                        episode = _seasonAndEpisodeTitle.second,
                        watchingTime = watchingTime
                    )
                    updateVideoHistoryUseCase(model)
                }

                _listOfStreams.update { emptyList() }
            } catch (e: Exception) {
                _screenState.value = DetailScreenState(detailInfo = _screenState.value.detailInfo)
                eventManager.sendEvent(e.message.toString())
            }
        }
    }

    private fun getSeasonTitle(season: String, episode: String) = "  /  Сезон $season  Серия $episode"

    private fun showSerialDialog() {
        _showSerialDialog.value = true
    }

    private fun showFilmDialog() {
        _showFilmDialog.value = true
    }
}