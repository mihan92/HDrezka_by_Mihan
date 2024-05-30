package com.mihan.movie.library.presentation.animation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavBackStackEntry
import com.ramcosta.composedestinations.spec.DestinationStyle

private const val TWEEN_DURATION_IN_MILLIS = 1000

object AnimatedScreenTransitions : DestinationStyle.Animated {
    override fun AnimatedContentTransitionScope<NavBackStackEntry>.enterTransition(): EnterTransition {
        return fadeIn(TweenSpec(delay = TWEEN_DURATION_IN_MILLIS))
    }

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.exitTransition(): ExitTransition {
        return fadeOut(TweenSpec())
    }

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.popEnterTransition(): EnterTransition {
        return fadeIn(TweenSpec(delay = TWEEN_DURATION_IN_MILLIS))
    }

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.popExitTransition(): ExitTransition {
        return fadeOut(TweenSpec())
    }
}