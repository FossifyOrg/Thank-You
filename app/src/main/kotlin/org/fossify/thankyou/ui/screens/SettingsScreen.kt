package org.fossify.thankyou.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import kotlinx.collections.immutable.toImmutableList
import org.fossify.commons.R
import org.fossify.commons.compose.alert_dialog.rememberAlertDialogStateSaveable
import org.fossify.commons.compose.extensions.MyDevices
import org.fossify.commons.compose.lists.SimpleColumnScaffold
import org.fossify.commons.compose.settings.SettingsGroup
import org.fossify.commons.compose.settings.SettingsHorizontalDivider
import org.fossify.commons.compose.settings.SettingsPreferenceComponent
import org.fossify.commons.compose.settings.SettingsSwitchComponent
import org.fossify.commons.compose.settings.SettingsTitleTextComponent
import org.fossify.commons.compose.theme.AppThemeSurface
import org.fossify.commons.compose.theme.SimpleTheme
import org.fossify.commons.dialogs.RadioGroupAlertDialog
import org.fossify.commons.helpers.isTiramisuPlus
import org.fossify.commons.models.RadioItem

@Composable
internal fun SettingsScreen(
    displayLanguage: String,
    isUseEnglishEnabled: Boolean,
    isUseEnglishChecked: Boolean,
    isShowingCheckmarksOnSwitches: Boolean,
    useBinaryStorageUnits: Boolean,
    onUseEnglishPress: (Boolean) -> Unit,
    onSetupLanguagePress: () -> Unit,
    showCheckmarksOnSwitches: (Boolean) -> Unit,
    onStorageUnitsChange: (Boolean) -> Unit,
    customizeColors: () -> Unit,
    goBack: () -> Unit,
) {
    val storageUnitsDialogState = rememberAlertDialogStateSaveable()
    val decimalUnits = stringResource(id = org.fossify.thankyou.R.string.storage_units_decimal)
    val binaryUnits = stringResource(id = org.fossify.thankyou.R.string.storage_units_binary)

    storageUnitsDialogState.DialogMember {
        RadioGroupAlertDialog(
            alertDialogState = storageUnitsDialogState,
            items = listOf(
                RadioItem(0, decimalUnits, false),
                RadioItem(1, binaryUnits, true),
            ).toImmutableList(),
            selectedItemId = if (useBinaryStorageUnits) 1 else 0,
            titleId = org.fossify.thankyou.R.string.storage_units,
        ) { onStorageUnitsChange(it as Boolean) }
    }

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
                    SettingsSwitchComponent(
                        label = stringResource(id = R.string.use_english_language),
                        initialValue = isUseEnglishChecked,
                        onChange = onUseEnglishPress,
                        showCheckmark = isShowingCheckmarksOnSwitches
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
            SettingsPreferenceComponent(
                label = stringResource(id = org.fossify.thankyou.R.string.storage_units),
                value = if (useBinaryStorageUnits) binaryUnits else decimalUnits,
                doOnPreferenceClick = storageUnitsDialogState::show,
            )
        }
    }
}

@Composable
@MyDevices
private fun SettingsScreenPreview() {
    var useBinaryStorageUnits by remember { mutableStateOf(false) }
    AppThemeSurface {
        SettingsScreen(
            displayLanguage = "English",
            isUseEnglishEnabled = false,
            isUseEnglishChecked = false,
            isShowingCheckmarksOnSwitches = false,
            useBinaryStorageUnits = useBinaryStorageUnits,
            onUseEnglishPress = {},
            onSetupLanguagePress = {},
            showCheckmarksOnSwitches = {},
            onStorageUnitsChange = { useBinaryStorageUnits = it },
            customizeColors = {},
            goBack = {},
        )
    }
}

