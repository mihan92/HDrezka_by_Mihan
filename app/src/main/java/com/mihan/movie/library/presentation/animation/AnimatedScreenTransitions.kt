package com.mihan.movie.library.presentation.animation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavBackStackEntry
import com.ramcosta.composedestinations.spec.DestinationStyle


object AnimatedScreenTransitions : DestinationStyle.Animated() {

    const val TWEEN_DURATION = 800

    override val enterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?) =
        { fadeIn(TweenSpec(delay = TWEEN_DURATION)) }

    override val exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?) =
        { fadeOut(TweenSpec()) }

    override val popEnterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?) =
        { fadeIn(TweenSpec(delay = TWEEN_DURATION)) }

    override val popExitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?) =
        { fadeOut(TweenSpec()) }
}