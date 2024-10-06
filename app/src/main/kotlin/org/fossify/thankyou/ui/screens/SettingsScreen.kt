package org.fossify.thankyou.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import org.fossify.commons.R
import org.fossify.commons.compose.extensions.MyDevices
import org.fossify.commons.compose.lists.SimpleColumnScaffold
import org.fossify.commons.compose.settings.SettingsCheckBoxComponent
import org.fossify.commons.compose.settings.SettingsGroup
import org.fossify.commons.compose.settings.SettingsHorizontalDivider
import org.fossify.commons.compose.settings.SettingsPreferenceComponent
import org.fossify.commons.compose.settings.SettingsSwitchComponent
import org.fossify.commons.compose.settings.SettingsTitleTextComponent
import org.fossify.commons.compose.theme.AppThemeSurface
import org.fossify.commons.compose.theme.SimpleTheme
import org.fossify.commons.helpers.isTiramisuPlus

@Composable
internal fun SettingsScreen(
    displayLanguage: String,
    isUseEnglishEnabled: Boolean,
    isUseEnglishChecked: Boolean,
    isShowingCheckmarksOnSwitches: Boolean,
    onUseEnglishPress: (Boolean) -> Unit,
    onSetupLanguagePress: () -> Unit,
    showCheckmarksOnSwitches: (Boolean) -> Unit,
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

        if (isUseEnglishEnabled || isTiramisuPlus()) {
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
            }
        }

        SettingsHorizontalDivider()
        SettingsGroup(title = {
            SettingsTitleTextComponent(text = stringResource(id = R.string.all_fossify_apps))
        }) {
            SettingsSwitchComponent(
                label = stringResource(id = org.fossify.thankyou.R.string.show_checkmarks_on_switches),
                initialValue = isShowingCheckmarksOnSwitches,
                onChange = showCheckmarksOnSwitches,
                showCheckmark = isShowingCheckmarksOnSwitches
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
            isShowingCheckmarksOnSwitches = false,
            onUseEnglishPress = {},
            onSetupLanguagePress = {},
            showCheckmarksOnSwitches = {},
            customizeColors = {},
            goBack = {},
        )
    }
}

