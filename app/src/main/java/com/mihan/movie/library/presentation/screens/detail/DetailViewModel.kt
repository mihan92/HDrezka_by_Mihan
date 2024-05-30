package com.mihan.movie.library.presentation.screens.detail

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.mihan.movie.library.common.ApiResponse
import com.mihan.movie.library.common.Constants.EMPTY_STRING
import com.mihan.movie.library.common.extentions.logger
import com.mihan.movie.library.common.utils.EventManager
import com.mihan.movie.library.domain.models.FavouritesModel
import com.mihan.movie.library.domain.models.SerialModel
import com.mihan.movie.library.domain.models.StreamModel
import com.mihan.movie.library.domain.models.VideoHistoryModel
import com.mihan.movie.library.domain.models.VideoInfoModel
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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
    savedStateHandle: SavedStateHandle,
    application: Application
) : AndroidViewModel(application) {
    private val navArgs: DetailScreenNavArgs = savedStateHandle.navArgs()
    private val _screenState = MutableStateFlow(DetailScreenState())
    private val _showFilmDialog = MutableStateFlow(false)
    private val _showSerialDialog = MutableStateFlow(false)
    private val _streamModel = MutableSharedFlow<StreamModel>()
    private val _videoInfo = MutableStateFlow(VideoInfoModel())
    private var _translatorId = _videoInfo.value.translations.values.firstOrNull()
    private var _translatorName = _videoInfo.value.translations.keys.firstOrNull()
    private val _listOfSeasons = MutableStateFlow<List<SerialModel>>(emptyList())
    private var _seasonAndEpisodeTitle = Pair(EMPTY_STRING, EMPTY_STRING)
    private val _videoHistoryModel = MutableStateFlow<VideoHistoryModel?>(null)
    private val _isVideoHasFavourites = MutableStateFlow(false)
    private var _filmId = EMPTY_STRING

    val videoInfo = _videoInfo.asStateFlow()
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

    fun onButtonWatchClicked() {
        viewModelScope.launch {
            getTranslationsByUrlUseCase(navArgs.movieUrl)
                .onEach { result ->
                    when (result) {
                        is ApiResponse.Error -> {
                            _screenState.update { DetailScreenState(detailInfo = _screenState.value.detailInfo) }
                            eventManager.sendEvent(result.errorMessage)
                        }

                        is ApiResponse.Loading -> _screenState.update {
                            DetailScreenState(
                                detailInfo = _screenState.value.detailInfo,
                                isLoading = true
                            )
                        }

                        is ApiResponse.Success -> {
                            _screenState.update { DetailScreenState(detailInfo = _screenState.value.detailInfo) }
                            _videoInfo.update { result.data }
                            _translatorId = result.data.translations.values.first()
                            _translatorName = result.data.translations.keys.first()
                            if (result.data.isVideoHasSeries) {
                                if (_videoHistoryModel.value != null) { // Если история просмотров не пустая
                                    _translatorId = _videoHistoryModel.value?.translatorId!!
                                    _translatorName = _videoHistoryModel.value?.translatorName
                                    getSeasonsByTranslatorId(_videoHistoryModel.value?.translatorId!!)
                                } else {
                                    getSeasonsByTranslatorId(result.data.translations.values.first())
                                }
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

    fun selectTranslateForSerials(translatorId: String) {
        _translatorId = translatorId
        _translatorName = _videoInfo.value.translations.entries.firstOrNull { it.value == translatorId }?.key
        getSeasonsByTranslatorId(translatorId)
    }

    fun onButtonFavouritesClicked() {
        viewModelScope.launch {
            val videoPageUrl = Uri.parse(navArgs.movieUrl).path ?: EMPTY_STRING
            val videoInfo = _screenState.value.detailInfo
            if (_isVideoHasFavourites.value)
                deleteFromFavouritesUseCase(videoInfo?.filmId ?: EMPTY_STRING)
            else {
                addToFavouritesUseCase(
                    FavouritesModel(
                        videoId = videoInfo?.filmId ?: EMPTY_STRING,
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
            getStreamsBySeasonId(id, _filmId, season, episode)
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
                            _screenState.update { DetailScreenState(isLoading = false) }
                            eventManager.sendEvent(result.errorMessage)
                        }

                        is ApiResponse.Loading -> _screenState.update { DetailScreenState(isLoading = true) }
                        is ApiResponse.Success -> {
                            _screenState.update { DetailScreenState(detailInfo = result.data) }
                            _filmId = result.data.filmId
                            getFavourites(result.data.filmId)
                            getVideoHistoryData(result.data.filmId)
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
        if (_listOfSeasons.value.isEmpty() && !_videoInfo.value.isVideoHasTranslations) {
            //Фильм без переводов
            logger("Фильм без переводов")
            val defaultTranslate = _videoInfo.value.translations.entries.first().value
            viewModelScope.launch {
                getStreamsByTranslatorId(defaultTranslate)
            }
        } else if (_listOfSeasons.value.isEmpty()) {
            //Фильм с переводами
            showFilmDialog()
            logger("Фильм с переводами")
        } else if (_videoInfo.value.isVideoHasSeries && !_videoInfo.value.isVideoHasTranslations) {
            //Сериал без переводов
            showSerialDialog()
            logger("Сериал без переводов")
        } else if (_videoInfo.value.isVideoHasSeries) {
            //Сериал с переводами
            showSerialDialog()
            logger("Сериал с переводами")
        }
    }

    private suspend fun getStreamsByTranslatorId(translatorId: String) {
        getStreamsByTranslatorIdUseCase(translatorId, _filmId)
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
                        _streamModel.emit(result.data)
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
            getSeasonsByTranslatorIdUseCase(translatorId, _filmId)
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
                            _streamModel.emit(result.data)
                        }
                    }
                }.last()
        }
    }

    private fun updateListOfStreams() {
        _streamModel.onEach { stream ->
            sendIntent(stream)
        }.launchIn(viewModelScope)
    }

    private fun sendIntent(stream: StreamModel) {
        runCatching {
            val videoUrl = stream.url
            var title = screenState.value.detailInfo?.title
            if (_videoInfo.value.isVideoHasSeries)
                title += getSeasonTitle(_seasonAndEpisodeTitle.first, _seasonAndEpisodeTitle.second)
            title += "  (${stream.quality})"

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
                    videoId = detailInfo.filmId,
                    videoPageUrl = videoPageUrl ?: EMPTY_STRING,
                    videoTitle = detailInfo.title,
                    posterUrl = detailInfo.imageUrl,
                    translatorName = _translatorName ?: EMPTY_STRING,
                    translatorId = _translatorId ?: EMPTY_STRING,
                    season = _seasonAndEpisodeTitle.first,
                    episode = _seasonAndEpisodeTitle.second,
                    watchingTime = watchingTime
                )
                updateVideoHistory(model)
            }
        }.onFailure { error ->
            _screenState.value = DetailScreenState(detailInfo = _screenState.value.detailInfo)
            eventManager.sendEvent(error.message.toString())
        }
    }

    private fun updateVideoHistory(model: VideoHistoryModel) {
        viewModelScope.launch {
            updateVideoHistoryUseCase(model)
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