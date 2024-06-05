package com.mihan.movie.library.common.models

import com.mihan.movie.library.R

enum class CategoryFilter(val titleResId: Int) {
    FILMS(R.string.film_title),
    SERIES(R.string.serial_title),
    CARTOONS(R.string.cartoon_title),
    ANIMATION(R.string.animation_title),
}