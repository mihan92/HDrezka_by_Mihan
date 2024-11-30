package com.mihan.movie.library.presentation.screens.detail

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.mihan.movie.library.common.ApiResponse
import com.mihan.movie.library.common.Constants
import com.mihan.movie.library.common.extentions.logger
import com.mihan.movie.library.common.utils.EventManager
import com.mihan.movie.library.common.utils.SharedPrefs
import com.mihan.movie.library.domain.models.FavouritesModel
import com.mihan.movie.library.domain.models.SerialModel
import com.mihan.movie.library.domain.models.StreamModel
import com.mihan.movie.library.domain.models.VideoHistoryModel
import com.mihan.movie.library.domain.models.VideoInfoModel
import com.mihan.movie.library.domain.models.WatchedVideoModel
import com.mihan.movie.library.domain.usecases.auth.SendWatchedVideoUseCase
import com.mihan.movie.library.domain.usecases.favourites.AddToFavouritesUseCase
import com.mihan.movie.library.domain.usecases.favourites.DeleteFromFavouritesUseCase
import com.mihan.movie.library.domain.usecases.favourites.GetFavoriteByIdUseCase
import com.mihan.movie.library.domain.usecases.local_history.GetVideoHistoryByIdUseCase
import com.mihan.movie.library.domain.usecases.local_history.UpdateVideoHistoryUseCase
import com.mihan.movie.library.domain.usecases.parser.GetDetailVideoByUrlUseCase
import com.mihan.movie.library.domain.usecases.parser.GetSeasonsByTranslatorIdUseCase
import com.mihan.movie.library.domain.usecases.parser.GetStreamsBySeasonIdUseCase
import com.mihan.movie.library.domain.usecases.parser.GetStreamsByTranslatorIdUseCase
import com.mihan.movie.library.domain.usecases.parser.GetTranslationsByUrlUseCase
import com.ramcosta.composedestinations.generated.navArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
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
    private val sendWatchedVideoUseCase: SendWatchedVideoUseCase,
    private val eventManager: EventManager,
    private val sharedPrefs: SharedPrefs,
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
    private var _seasonAndEpisodeTitle = Pair(Constants.EMPTY_STRING, Constants.EMPTY_STRING)
    private val _videoHistoryModel = MutableStateFlow<VideoHistoryModel?>(null)
    private val _isVideoHasFavourites = MutableStateFlow(false)
    private var _filmId = Constants.EMPTY_STRING

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
            getVideoHistoryData(_filmId)
            getTranslationsByUrlUseCase(navArgs.movieUrl)
                .collect { result ->
                    handleApiResponse(result) { data ->
                        _videoInfo.value = data
                        _translatorId = data.translations.values.firstOrNull()
                        _translatorName = data.translations.keys.firstOrNull()
                        if (data.isVideoHasSeries) {
                            handleSeriesTranslations(data)
                        } else {
                            updateData()
                        }
                    }
                }
        }
    }

    fun selectTranslateForFilms(translatorId: String) {
        viewModelScope.launch {
            _translatorId = translatorId
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
            val videoPageUrl = Uri.parse(navArgs.movieUrl).path ?: Constants.EMPTY_STRING
            val videoInfo = _screenState.value.detailInfo
            if (_isVideoHasFavourites.value) {
                deleteFromFavouritesUseCase(videoInfo?.filmId ?: Constants.EMPTY_STRING)
            } else {
                addToFavouritesUseCase(
                    FavouritesModel(
                        videoId = videoInfo?.filmId ?: Constants.EMPTY_STRING,
                        videoPageUrl = videoPageUrl,
                        videoTitle = videoInfo?.title ?: Constants.EMPTY_STRING,
                        posterUrl = videoInfo?.imageUrl ?: Constants.EMPTY_STRING
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
                .collect { result ->
                    handleApiResponse(result) { data ->
                        _screenState.update { it.copy(detailInfo = data) }
                        _filmId = data.filmId
                        getFavourites(data.filmId)
                    }
                }
        }
    }

    private fun getFavourites(videoId: String) {
        getFavoriteByIdUseCase(videoId)
            .onEach { favouritesModel ->
                _isVideoHasFavourites.value = (favouritesModel != null)
            }.launchIn(viewModelScope)
    }

    private suspend fun getVideoHistoryData(videoId: String) {
        getVideoHistoryByIdUseCase(videoId).collectLatest { result ->
            handleApiResponse(result) { historyModel ->
                _videoHistoryModel.value = historyModel
            }
        }
    }

    private fun updateData() {
        when {
            _listOfSeasons.value.isEmpty() && !_videoInfo.value.isVideoHasTranslations -> {
                handleNoTranslations()
            }

            _listOfSeasons.value.isEmpty() -> {
                showFilmDialog()
                logger("Фильм с переводами")
            }

            _videoInfo.value.isVideoHasSeries && !_videoInfo.value.isVideoHasTranslations -> {
                showSerialDialog()
                logger("Сериал без переводов")
            }

            _videoInfo.value.isVideoHasSeries -> {
                showSerialDialog()
                logger("Сериал с переводами")
            }
        }
    }

    private suspend fun getStreamsByTranslatorId(translatorId: String) {
        getStreamsByTranslatorIdUseCase(translatorId, _filmId)
            .collect { result ->
                handleApiResponse(result) { data ->
                    _streamModel.emit(data)
                }
            }
    }

    private fun getSeasonsByTranslatorId(translatorId: String) {
        viewModelScope.launch {
            getSeasonsByTranslatorIdUseCase(translatorId, _filmId)
                .collect { result ->
                    handleApiResponse(result) { data ->
                        _listOfSeasons.value = data
                        updateData()
                    }
                }
        }
    }

    private fun getStreamsBySeasonId(translationId: String, videoId: String, season: String, episode: String) {
        viewModelScope.launch {
            getStreamsBySeasonIdUseCase(translationId, videoId, season, episode)
                .collect { result ->
                    handleApiResponse(result) { data ->
                        _streamModel.emit(data)
                    }
                }
        }
    }

    private fun updateListOfStreams() {
        _streamModel.onEach { stream ->
            sendIntent(stream)
        }.launchIn(viewModelScope)
    }

    private suspend fun sendIntent(stream: StreamModel) {
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

                val isAuthorized = sharedPrefs.getUserAuthStatus()
                if (isAuthorized) {
                    val model = WatchedVideoModel(
                        dataId = _filmId,
                        translatorId = _translatorId ?: Constants.EMPTY_STRING,
                        season = _seasonAndEpisodeTitle.first.ifEmpty { "0" },
                        episode = _seasonAndEpisodeTitle.second.ifEmpty { "0" },
                    )
                    sendWatchedVideoUseCase(model)
                    getVideoHistoryData(_filmId)
                } else {
                    val model = VideoHistoryModel(
                        videoId = _filmId,
                        dataId = Constants.EMPTY_STRING,
                        videoPageUrl = videoPageUrl ?: Constants.EMPTY_STRING,
                        videoTitle = detailInfo.title,
                        posterUrl = detailInfo.imageUrl,
                        translatorName = _translatorName ?: Constants.EMPTY_STRING,
                        translatorId = _translatorId ?: Constants.EMPTY_STRING,
                        season = _seasonAndEpisodeTitle.first,
                        episode = _seasonAndEpisodeTitle.second,
                        watchingTime = watchingTime
                    )
                    updateVideoHistory(model)
                    getVideoHistoryData(_filmId)
                }
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

    private fun getTranslatorIdByName(translatorName: String): String {
        val translations = _videoInfo.value.translations
        val matchingKey = translations.keys.firstOrNull { key -> key.contains(translatorName, ignoreCase = true) }
        return translations[matchingKey] ?: translations.values.first()
    }

    private inline fun <T> handleApiResponse(result: ApiResponse<T>, onSuccess: (T) -> Unit) {
        when (result) {
            is ApiResponse.Error -> {
                _screenState.update { it.copy(isLoading = false) }
                eventManager.sendEvent(result.errorMessage)
            }

            is ApiResponse.Loading -> _screenState.update { it.copy(isLoading = true) }
            is ApiResponse.Success -> {
                _screenState.update { it.copy(isLoading = false) }
                onSuccess(result.data)
            }
        }
    }

    private fun handleSeriesTranslations(data: VideoInfoModel) {
        if (_videoHistoryModel.value != null) {
            _translatorId = _videoHistoryModel.value!!.translatorId.takeIf { it.isNotEmpty() }
                ?: getTranslatorIdByName(_videoHistoryModel.value!!.translatorName)
            _translatorName = _videoHistoryModel.value!!.translatorName.takeIf { it.isNotEmpty() }
                ?: data.translations.keys.firstOrNull()
            getSeasonsByTranslatorId(_translatorId!!)
        } else {
            getSeasonsByTranslatorId(data.translations.values.first())
        }
    }

    private fun handleNoTranslations() {
        logger("Фильм без переводов")
        val defaultTranslate = _videoInfo.value.translations.entries.first().value
        viewModelScope.launch {
            getStreamsByTranslatorId(defaultTranslate)
        }
    }
}