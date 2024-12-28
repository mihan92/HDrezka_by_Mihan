package com.mihan.movie.library.presentation.ui.view

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.DrawerState
import androidx.tv.material3.DrawerValue
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.mihan.movie.library.R
import com.mihan.movie.library.presentation.navigation.Screens
import com.mihan.movie.library.presentation.navigation.popUpToMain
import com.mihan.movie.library.presentation.ui.size18sp
import com.mihan.movie.library.presentation.ui.size4dp

private const val DRAWER_WIDTH_FRACTION = 0.13f
private const val CONTENT_ALPHA = 0.7f

enum class DrawerItems(val route: String, @DrawableRes val icon: Int, @StringRes val titleResId: Int) {
    AppUpdates(Screens.AppUpdatesScreen.route, R.drawable.ic_notifications, R.string.updates_route_title),
    NewSeries(Screens.NewSeriesScreen.route, R.drawable.ic_new, R.string.new_series),
    Search(Screens.Search.route, R.drawable.ic_search, R.string.search_route_title),
    Home(Screens.Home.route, R.drawable.ic_home, R.string.home_route_title),
    History(Screens.HistoryScreen.route, R.drawable.ic_history, R.string.history_route_title),
    Favourites(Screens.FavouritesScreen.route, R.drawable.ic_favourites, R.string.favourites_route_title),
    Settings(Screens.Settings.route, R.drawable.ic_settings, R.string.settings_route_title)
}

@Composable
fun DrawerContent(
    drawerState: DrawerState,
    currentDestination: String?,
    navController: NavHostController,
    isAppUpdatesAvailable: Boolean,
    isNewSeriesAvailable: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background.copy(CONTENT_ALPHA))
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center
    ) {
        DrawerItems.entries.forEach { item ->
            NavigationItem(
                drawerState = drawerState,
                icon = ImageVector.vectorResource(item.icon),
                titleResId = item.titleResId,
                selected = item.route == currentDestination,
                isVisible = when (item.route) {
                    Screens.AppUpdatesScreen.route -> isAppUpdatesAvailable
                    Screens.NewSeriesScreen.route -> isNewSeriesAvailable
                    else -> true
                },
                onItemClick = {
                    navController.navigate(item.route) {
                        popUpToMain()
                    }
                }
            )
        }
    }
}

@Composable
private fun NavigationItem(
    drawerState: DrawerState,
    icon: ImageVector,
    titleResId: Int,
    selected: Boolean,
    isVisible: Boolean,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var buttonBackgroundColor by remember { mutableStateOf(Color.Transparent) }
    if (isVisible)
        Button(
            modifier = modifier
                .padding(vertical = size4dp)
                .background(buttonBackgroundColor)
                .onFocusChanged { focusState ->
                    buttonBackgroundColor = if (focusState.isFocused) Color.White.copy(0.1f) else Color.Transparent
                },
            onClick = onItemClick,
            colors = ButtonDefaults.colors(
                containerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
            ),
            shape = ButtonDefaults.shape(RectangleShape),
        ) {
            val selectedContentColor =
                if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.padding(end = size4dp),
                tint = selectedContentColor
            )
            AnimatedVisibility(visible = drawerState.currentValue == DrawerValue.Open) {
                Text(
                    text = stringResource(id = titleResId),
                    textAlign = TextAlign.Start,
                    color = selectedContentColor,
                    fontSize = size18sp,
                    fontWeight = FontWeight.W700,
                    modifier = Modifier.fillMaxWidth(DRAWER_WIDTH_FRACTION)
                )
            }
        }
}