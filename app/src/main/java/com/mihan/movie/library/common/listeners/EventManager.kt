package com.mihan.movie.library.common.listeners

import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

interface EventManager {

    val eventsFlow: SharedFlow<String>

    fun sendEvent(eventMessage: String)
}

@ActivityRetainedScoped
class EventManagerImpl @Inject constructor(): EventManager {

    private val _sharedFlow = MutableSharedFlow<String>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    override val eventsFlow: SharedFlow<String> = _sharedFlow.asSharedFlow()

    override fun sendEvent(eventMessage: String) {
        _sharedFlow.tryEmit(eventMessage)
    }
}