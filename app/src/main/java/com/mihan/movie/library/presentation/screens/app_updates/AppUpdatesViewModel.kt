package com.mihan.movie.library.presentation.screens.app_updates

import android.app.DownloadManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mihan.movie.library.common.extentions.getColumn
import com.mihan.movie.library.common.extentions.logger
import com.mihan.movie.library.common.utils.EventManager
import com.mihan.movie.library.common.utils.AppUpdatesChecker
import com.mihan.movie.library.common.utils.IDownloadManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppUpdatesViewModel @Inject constructor(
    private val appUpdatesChecker: AppUpdatesChecker,
    private val downloadManager: IDownloadManager,
    private val systemDownloadManager: DownloadManager,
    private val eventManager: EventManager
) : ViewModel() {
    private val _downloadState = MutableStateFlow(DownloadState())
    val downloadState = _downloadState.asStateFlow()
    val changelogModel = appUpdatesChecker.changelog

    fun buttonUpdatePressed() {
        changelogModel?.apkUrl?.let {
            val id = downloadManager.downloadApk(it)
            _downloadState.update { state -> state.copy(isDownloading = true) }
            getStatus(id)
        }
    }

    private fun getStatus(id: Long) {
        viewModelScope.launch {
            while (_downloadState.value.isDownloading) {
                delay(UPDATE_TIME_DELAY)
                val query = DownloadManager.Query().setFilterById(id)
                val cursor = systemDownloadManager.query(query)
                if (cursor.moveToFirst()) {
                    val status = cursor.getColumn(DownloadManager.COLUMN_STATUS)
                    val downloadBytes = cursor.getColumn(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR).toFloat()
                    val totalBytes = cursor.getColumn(DownloadManager.COLUMN_TOTAL_SIZE_BYTES).toFloat()
                    val percentOfDownload = ((downloadBytes / totalBytes) * 100)
                    when (status) {
                        DownloadManager.STATUS_PAUSED -> logger("downloading status paused")
                        DownloadManager.STATUS_PENDING -> logger("downloading status pending")
                        DownloadManager.STATUS_RUNNING -> {
                            _downloadState.update { state ->
                                state.copy(isDownloading = true, downloadingProgress = percentOfDownload)
                            }
                            logger("downloading status running")
                        }

                        DownloadManager.STATUS_SUCCESSFUL -> {
                            _downloadState.update { state -> state.copy(isDownloading = false) }
                            logger("downloading status successful")
                        }

                        else -> {
                            _downloadState.update { state -> state.copy(isDownloading = false) }
                            eventManager.sendEvent("Что-то пошло не так..")
                            logger("error downloading status $status}")
                        }
                    }
                }
                cursor.close()
            }
        }
    }

    companion object {
        private const val UPDATE_TIME_DELAY = 1000L
    }
}