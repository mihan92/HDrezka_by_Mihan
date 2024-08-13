package com.mihan.movie.library.common.models

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.mihan.movie.library.R
import com.mihan.movie.library.presentation.ui.theme.colorAnimationsCategory
import com.mihan.movie.library.presentation.ui.theme.colorCartoonsCategory
import com.mihan.movie.library.presentation.ui.theme.colorFilmsCategory
import com.mihan.movie.library.presentation.ui.theme.colorSerialsCategory
import com.mihan.movie.library.presentation.ui.theme.colorTvShowsCategory

enum class VideoCategory(@StringRes val titleResId: Int, val category: String, val genre: String, val color: Color) {
    All(R.string.all_title,"","", Color.Transparent),
    Film(R.string.film_title,"Фильм","&genre=1", colorFilmsCategory),
    Serial(R.string.serial_title,"Сериал","&genre=2", colorSerialsCategory),
    Cartoon(R.string.cartoon_title,"Мультфильм","&genre=3", colorCartoonsCategory),
    Animation(R.string.animation_title,"Аниме","&genre=82", colorAnimationsCategory),
    TvShow(R.string.tv_show_title,"ТВ шоу","&genre=4", colorTvShowsCategory);

    companion object {
        fun getColorFromCategory(category: String) = entries.lastOrNull { category.contains(it.category) }?.color ?: Film.color
    }
}