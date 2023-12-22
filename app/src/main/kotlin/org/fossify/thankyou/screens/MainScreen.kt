@file:OptIn(ExperimentalMaterial3Api::class)

package org.fossify.thankyou.screens

import android.text.util.Linkify
import android.view.Gravity
import android.widget.TextView
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.collections.immutable.toImmutableList
import org.fossify.commons.R
import org.fossify.commons.compose.extensions.MyDevices
import org.fossify.commons.compose.lists.SimpleScaffold
import org.fossify.commons.compose.lists.simpleTopAppBarColors
import org.fossify.commons.compose.lists.topAppBarInsets
import org.fossify.commons.compose.lists.topAppBarPaddings
import org.fossify.commons.compose.menus.ActionItem
import org.fossify.commons.compose.menus.ActionMenu
import org.fossify.commons.compose.menus.OverflowMode
import org.fossify.commons.compose.theme.AppThemeSurface
import org.fossify.commons.compose.theme.SimpleTheme

@Composable
internal fun MainScreen(
    showMoreApps: Boolean,
    openSettings: () -> Unit,
    openAbout: () -> Unit,
    moreAppsFromUs: () -> Unit,
    linkColor: Color,
) {
    SimpleScaffold(customTopBar = { scrolledColor: Color, _: MutableInteractionSource, scrollBehavior: TopAppBarScrollBehavior, statusBarColor: Int, colorTransitionFraction: Float, contrastColor: Color ->
        TopAppBar(
            title = {},
            actions = {
                val actionMenus = rememberActionItems(openSettings, openAbout, showMoreApps, moreAppsFromUs)
                var isMenuVisible by remember { mutableStateOf(false) }
                ActionMenu(items = actionMenus, numIcons = 2, isMenuVisible = isMenuVisible, onMenuToggle = { isMenuVisible = it }, iconsColor = scrolledColor)
            },
            scrollBehavior = scrollBehavior,
            colors = simpleTopAppBarColors(statusBarColor, colorTransitionFraction, contrastColor),
            modifier = Modifier.topAppBarPaddings(),
            windowInsets = topAppBarInsets()
        )
    }) { paddingValues ->
        val textColor = SimpleTheme.colorScheme.onSurface.toArgb()

        AndroidView(
            factory = { context ->
                TextView(context).apply {
                    setText(org.fossify.thankyou.R.string.main_text)
                    textSize = 16.sp.value
                    setLineSpacing(3.dp.value, 1f)
                    gravity = Gravity.CENTER_HORIZONTAL
                    Linkify.addLinks(this, Linkify.WEB_URLS)
                    Linkify.addLinks(this, Linkify.EMAIL_ADDRESSES)
                }
            }, modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
                .padding(40.dp),
            update = { textView ->
                textView.setLinkTextColor(linkColor.toArgb())
                textView.setTextColor(textColor)
            }
        )
    }
}

@Composable
private fun rememberActionItems(
    openSettings: () -> Unit,
    openAbout: () -> Unit,
    showMoreApps: Boolean,
    moreAppsFromUs: () -> Unit
) = remember {
    val settings =
        ActionItem(R.string.settings, icon = Icons.Filled.Settings, doAction = openSettings, overflowMode = OverflowMode.NEVER_OVERFLOW)
    val about = ActionItem(R.string.about, icon = Icons.Outlined.Info, doAction = openAbout, overflowMode = OverflowMode.NEVER_OVERFLOW)

    val list = if (showMoreApps) {
        listOf(settings, about, ActionItem(R.string.more_apps_from_us, doAction = moreAppsFromUs, overflowMode = OverflowMode.ALWAYS_OVERFLOW))
    } else {
        listOf(settings, about)
    }
    list.toImmutableList()
}

@Composable
@MyDevices
private fun MainScreenPreview() {
    AppThemeSurface {
        MainScreen(showMoreApps = true, openSettings = {}, openAbout = {}, moreAppsFromUs = {}, linkColor = SimpleTheme.colorScheme.onSurface)
    }
}
