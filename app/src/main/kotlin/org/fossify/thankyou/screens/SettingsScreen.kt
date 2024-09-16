package org.fossify.thankyou.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import org.fossify.commons.R
import org.fossify.commons.compose.extensions.MyDevices
import org.fossify.commons.compose.lists.SimpleColumnScaffold
import org.fossify.commons.compose.settings.*
import org.fossify.commons.compose.theme.AppThemeSurface
import org.fossify.commons.compose.theme.SimpleTheme
import org.fossify.commons.helpers.isTiramisuPlus

@Composable
internal fun SettingsScreen(
    displayLanguage: String,
    isUseEnglishEnabled: Boolean,
    isUseEnglishChecked: Boolean,
    isHidingLauncherIcon: Boolean,
    onUseEnglishPress: (Boolean) -> Unit,
    onSetupLanguagePress: () -> Unit,
    hideLauncherIconClick: (Boolean) -> Unit,
    customizeColors: () -> Unit,
    goBack: () -> Unit,
) {
    SimpleColumnScaffold(title = stringResource(id = R.string.settings), goBack = goBack) {
        SettingsGroup(title = {
            SettingsTitleTextComponent(text = stringResource(id = R.string.color_customization))
        }) {
            SettingsPreferenceComponent(
                label = stringResource(id = R.string.customize_colors),
                doOnPreferenceClick = customizeColors,
            )
        }
        SettingsHorizontalDivider()
        SettingsGroup(title = {
            SettingsTitleTextComponent(text = stringResource(id = R.string.general_settings))
        }) {

            if (isUseEnglishEnabled) {
                SettingsCheckBoxComponent(
                    label = stringResource(id = R.string.use_english_language),
                    initialValue = isUseEnglishChecked,
                    onChange = onUseEnglishPress,
                )
            }
            if (isTiramisuPlus()) {
                SettingsPreferenceComponent(
                    label = stringResource(id = R.string.language),
                    value = displayLanguage,
                    doOnPreferenceClick = onSetupLanguagePress,
                    preferenceLabelColor = SimpleTheme.colorScheme.onSurface,
                )
            }
            SettingsSwitchComponent(
                label = stringResource(id = org.fossify.thankyou.R.string.hide_launcher_icon),
                initialValue = isHidingLauncherIcon,
                onChange = hideLauncherIconClick,
            )
        }
    }
}

@Composable
@MyDevices
private fun SettingsScreenPreview() {
    AppThemeSurface {
        SettingsScreen(
            displayLanguage = "English",
            isUseEnglishEnabled = false,
            isUseEnglishChecked = false,
            isHidingLauncherIcon = false,
            onUseEnglishPress = {},
            onSetupLanguagePress = {},
            hideLauncherIconClick = {},
            customizeColors = {},
            goBack = {},
        )
    }
}

