package org.fossify.thankyou.activities

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.fossify.commons.compose.extensions.enableEdgeToEdgeSimple
import org.fossify.commons.compose.theme.AppThemeSurface
import org.fossify.commons.extensions.updateGlobalConfig
import org.fossify.commons.helpers.MyContentProvider
import org.fossify.commons.helpers.isTiramisuPlus
import org.fossify.thankyou.extensions.config
import org.fossify.thankyou.extensions.launchChangeAppLanguageIntent
import org.fossify.thankyou.extensions.startCustomizationActivity
import org.fossify.thankyou.ui.screens.SettingsScreen
import java.util.Locale
import kotlin.system.exitProcess

class SettingsActivity : ComponentActivity() {

    private val preferences by lazy { config }

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdgeSimple()
        setContent {
            AppThemeSurface {
                val wasUseEnglishToggledFlow by preferences.wasUseEnglishToggledFlow
                    .collectAsStateWithLifecycle(preferences.wasUseEnglishToggled)
                val useEnglishFlow by preferences.useEnglishFlow
                    .collectAsStateWithLifecycle(preferences.useEnglish)
                val showCheckmarksOnSwitches by preferences.showCheckmarksOnSwitchesFlow
                    .collectAsStateWithLifecycle(preferences.showCheckmarksOnSwitches)
                val displayLanguage = remember { Locale.getDefault().displayLanguage }
                val isUseEnglishEnabled by remember(wasUseEnglishToggledFlow) {
                    derivedStateOf {
                        (wasUseEnglishToggledFlow || Locale.getDefault().language != "en") && !isTiramisuPlus()
                    }
                }

                SettingsScreen(
                    displayLanguage = displayLanguage,
                    isUseEnglishEnabled = isUseEnglishEnabled,
                    isUseEnglishChecked = useEnglishFlow,
                    isShowingCheckmarksOnSwitches = showCheckmarksOnSwitches,
                    onUseEnglishPress = { isChecked ->
                        preferences.useEnglish = isChecked
                        exitProcess(0)
                    },
                    onSetupLanguagePress = ::launchChangeAppLanguageIntent,
                    showCheckmarksOnSwitches = { isChecked ->
                        preferences.showCheckmarksOnSwitches = isChecked
                        updateGlobalConfig(
                            contentValues = ContentValues().apply {
                                put(MyContentProvider.COL_SHOW_CHECKMARKS_ON_SWITCHES, isChecked)
                            }
                        )
                    },
                    customizeColors = ::startCustomizationActivity,
                    goBack = ::finish
                )
            }
        }
    }
}
