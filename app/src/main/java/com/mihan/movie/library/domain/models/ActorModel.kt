package com.mihan.movie.library.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class ActorModel(
    val actorId: String,
    val actorName: String
)
