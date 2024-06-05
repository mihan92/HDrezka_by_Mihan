package com.mihan.movie.library.common.models

import androidx.annotation.DrawableRes
import com.mihan.movie.library.R

enum class MovieCollections(@DrawableRes val iconResId: Int) {
    NETFLIX(R.drawable.netflix),
    HBO(R.drawable.hbo),
    AMAZON_PRIME(R.drawable.amazon_prime),
    DISNEY(R.drawable.disney),
    APPLE_TV(R.drawable.apple_tv),
    HULU(R.drawable.hulu),
    EPIX(R.drawable.epix);

    companion object {
        fun getCollectionsMap() = mapOf(
            CategoryFilter.FILMS to listOf(NETFLIX, HBO, AMAZON_PRIME, DISNEY, APPLE_TV),
            CategoryFilter.SERIES to listOf(NETFLIX, HBO, AMAZON_PRIME, DISNEY, APPLE_TV, HULU, EPIX),
            CategoryFilter.CARTOONS to listOf(NETFLIX, DISNEY),
            CategoryFilter.ANIMATION to listOf(NETFLIX, DISNEY),
        )
    }
}