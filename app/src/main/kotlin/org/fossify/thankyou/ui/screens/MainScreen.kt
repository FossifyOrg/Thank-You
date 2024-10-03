@file:OptIn(ExperimentalMaterial3Api::class)

package org.fossify.thankyou.ui.screens

import android.text.util.Linkify
import android.view.Gravity
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.collections.immutable.toImmutableList
import org.fossify.commons.compose.extensions.MyDevices
import org.fossify.commons.compose.lists.SimpleScaffold
import org.fossify.commons.compose.lists.simpleTopAppBarColors
import org.fossify.commons.compose.lists.topAppBarInsets
import org.fossify.commons.compose.lists.topAppBarPaddings
import org.fossify.commons.compose.menus.ActionItem
import org.fossify.commons.compose.menus.ActionMenu
import org.fossify.commons.compose.menus.OverflowMode
import org.fossify.commons.compose.settings.SettingsGroupTitle
import org.fossify.commons.compose.theme.AppThemeSurface
import org.fossify.commons.compose.theme.SimpleTheme
import org.fossify.thankyou.R
import org.fossify.thankyou.models.FossifyApp
import org.fossify.thankyou.ui.components.FossifyApp

@Composable
internal fun MainScreen(
    allApps: List<FossifyApp>,
    fakeApps: List<FossifyApp>,
    showMoreApps: Boolean,
    showThankYouNotice: Boolean,
    openSettings: () -> Unit,
    openAbout: () -> Unit,
    moreAppsFromUs: () -> Unit,
    launchApp: (packageName: String) -> Unit,
    uninstallApp: (packageName: String) -> Unit,
    hideThankYouNotice: () -> Unit,
    linkColor: Color,
) {
    SimpleScaffold(
        customTopBar = { scrolledColor: Color, _: MutableInteractionSource, scrollBehavior: TopAppBarScrollBehavior, statusBarColor: Int, colorTransitionFraction: Float, contrastColor: Color ->
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = org.fossify.commons.R.string.simple_thank_you),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = scrolledColor
                    )
                },
                actions = {
                    val actionMenus =
                        rememberActionItems(openSettings, openAbout, showMoreApps, moreAppsFromUs)
                    var isMenuVisible by remember { mutableStateOf(false) }
                    ActionMenu(
                        items = actionMenus,
                        numIcons = 2,
                        isMenuVisible = isMenuVisible,
                        onMenuToggle = { isMenuVisible = it },
                        iconsColor = scrolledColor
                    )
                },
                scrollBehavior = scrollBehavior,
                colors = simpleTopAppBarColors(
                    statusBarColor,
                    colorTransitionFraction,
                    contrastColor
                ),
                modifier = Modifier.topAppBarPaddings(),
                windowInsets = topAppBarInsets()
            )
        }) { paddingValues ->
        val textColor = SimpleTheme.colorScheme.onSurface

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (showThankYouNotice) {
                item(key = "thankyou") {
                    ThankYou(
                        modifier = Modifier.animateItem(),
                        textColor = textColor,
                        linkColor = linkColor,
                        hideThankYouNotice = hideThankYouNotice
                    )
                }
            }

            fossifyApps(
                titleId = R.string.potentially_unsafe_apps,
                apps = fakeApps,
                launchApp = launchApp,
                uninstallApp = uninstallApp
            )

            fossifyApps(
                titleId = R.string.installed_fossify_apps,
                apps = allApps,
                launchApp = launchApp,
                uninstallApp = uninstallApp
            )
        }
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
        ActionItem(
            org.fossify.commons.R.string.settings,
            icon = Icons.Filled.Settings,
            doAction = openSettings,
            overflowMode = OverflowMode.NEVER_OVERFLOW
        )
    val about = ActionItem(
        org.fossify.commons.R.string.about,
        icon = Icons.Outlined.Info,
        doAction = openAbout,
        overflowMode = OverflowMode.ALWAYS_OVERFLOW
    )

    val list = if (showMoreApps) {
        listOf(
            settings,
            about,
            ActionItem(
                org.fossify.commons.R.string.more_apps_from_us,
                doAction = moreAppsFromUs,
                overflowMode = OverflowMode.ALWAYS_OVERFLOW
            )
        )
    } else {
        listOf(settings, about)
    }
    list.toImmutableList()
}

@Composable
private fun ThankYou(
    modifier: Modifier,
    textColor: Color,
    linkColor: Color,
    hideThankYouNotice: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
            .then(modifier)
    ) {
        AndroidView(
            factory = { context ->
                TextView(context).apply {
                    setText(R.string.main_text)
                    textSize = 16.sp.value
                    setLineSpacing(3.dp.value, 1f)
                    gravity = Gravity.CENTER_HORIZONTAL
                    Linkify.addLinks(this, Linkify.WEB_URLS)
                    Linkify.addLinks(this, Linkify.EMAIL_ADDRESSES)
                }
            },
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp),
            update = { textView ->
                textView.setLinkTextColor(linkColor.toArgb())
                textView.setTextColor(textColor.toArgb())
            }
        )

        TextButton(
            onClick = hideThankYouNotice,
            modifier = Modifier
                .padding(vertical = 6.dp, horizontal = 12.dp)
                .align(Alignment.End),
        ) {
            Text(text = stringResource(org.fossify.commons.R.string.do_not_show_again))
        }
    }
}

private fun LazyListScope.fossifyApps(
    titleId: Int,
    apps: List<FossifyApp>,
    launchApp: (packageName: String) -> Unit,
    uninstallApp: (packageName: String) -> Unit,
) {
    if (apps.isNotEmpty()) {
        item(key = titleId) {
            SettingsGroupTitle(
                modifier = Modifier.animateItem(),
                title = {
                    Box(modifier = Modifier.padding(top = SimpleTheme.dimens.padding.large)) {
                        Text(
                            text = stringResource(titleId),
                            color = SimpleTheme.colorScheme.primary,
                            fontSize = 15.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            )
        }

        items(
            count = apps.size,
            key = { apps[it].packageName }
        ) {
            FossifyApp(
                app = apps[it],
                launchApp = launchApp,
                uninstallApp = uninstallApp,
                modifier = Modifier.animateItem()
            )
        }
    }
}

@Composable
@MyDevices
private fun MainScreenPreview() {
    AppThemeSurface {
        MainScreen(
            allApps = mutableListOf<FossifyApp>().apply {
                repeat(10) {
                    add(
                        FossifyApp(
                            name = "Fossify Thank You",
                            icon = AppCompatResources.getDrawable(
                                LocalContext.current,
                                R.mipmap.ic_launcher
                            ),
                            signerName = "Fossify",
                            packageName = "org.fossify.thankyou.$it",
                            versionName = "1.2.1",
                            installerName = "Fossify Store",
                            installerPackage = "org.fossify.store",
                            verified = true
                        )
                    )
                }
            },
            fakeApps = listOf(
                FossifyApp(
                    name = "Fossify Gallery",
                    icon = AppCompatResources.getDrawable(
                        LocalContext.current,
                        R.mipmap.ic_launcher
                    ),
                    signerName = null,
                    packageName = "org.fossify.gallery.tampered",
                    versionName = "1.4.0",
                    installerName = "Shell",
                    installerPackage = "com.android.shell",
                    verified = false
                )
            ),
            showMoreApps = true,
            openSettings = {},
            openAbout = {},
            moreAppsFromUs = {},
            launchApp = {},
            uninstallApp = {},
            linkColor = SimpleTheme.colorScheme.onSurface,
            showThankYouNotice = true,
            hideThankYouNotice = {}
        )
    }
}
