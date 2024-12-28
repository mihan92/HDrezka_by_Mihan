package com.mihan.movie.library.presentation.navigation

import com.mihan.movie.library.presentation.animation.AnimatedScreenTransitions
import com.ramcosta.composedestinations.annotation.NavGraph
import com.ramcosta.composedestinations.annotation.RootGraph

@NavGraph<RootGraph>(
    defaultTransitions = AnimatedScreenTransitions::class,
    route = "app_nav_graph",
    start = true
)
annotation class AppNavGraph