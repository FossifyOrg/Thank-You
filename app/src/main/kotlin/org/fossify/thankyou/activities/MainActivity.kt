package org.fossify.thankyou.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.collections.immutable.toImmutableList
import org.fossify.commons.compose.alert_dialog.AlertDialogState
import org.fossify.commons.compose.alert_dialog.rememberAlertDialogState
import org.fossify.commons.compose.extensions.*
import org.fossify.commons.compose.theme.AppThemeSurface
import org.fossify.commons.dialogs.DonateAlertDialog
import org.fossify.commons.dialogs.RateStarsAlertDialog
import org.fossify.commons.dialogs.WhatsNewAlertDialog
import org.fossify.commons.extensions.hideKeyboard
import org.fossify.commons.extensions.launchMoreAppsFromUsIntent
import org.fossify.commons.models.FAQItem
import org.fossify.commons.models.Release
import org.fossify.thankyou.BuildConfig
import org.fossify.thankyou.R
import org.fossify.thankyou.extensions.startAboutActivity
import org.fossify.thankyou.screens.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdgeSimple()
        setContent {
            AppThemeSurface {
                val releasesList = remember { mutableStateListOf<Release>() }
                val checkWhatsNewAlertDialogState = getCheckWhatsNewAlertDialogState(releasesList)
                val linkColor = linkColor()
                val showMoreApps = onEventValue { !resources.getBoolean(R.bool.hide_google_relations) }
                MainScreen(
                    linkColor = linkColor,
                    showMoreApps = showMoreApps,
                    openSettings = ::launchSettings,
                    openAbout = ::launchAbout,
                    moreAppsFromUs = ::launchMoreAppsFromUsIntent
                )
                AppLaunched()
                CheckWhatsNew(releasesList, checkWhatsNewAlertDialogState)
            }
        }
    }

    @Composable
    private fun AppLaunched(
        donateAlertDialogState: AlertDialogState = getDonateAlertDialogState(),
        rateStarsAlertDialogState: AlertDialogState = getRateStarsAlertDialogState(),
    ) {
        LaunchedEffect(Unit) {
            appLaunchedCompose(
                appId = BuildConfig.APPLICATION_ID,
                showDonateDialog = donateAlertDialogState::show,
                showRateUsDialog = rateStarsAlertDialogState::show,
                showUpgradeDialog = {}
            )
        }
    }

    @Composable
    private fun CheckWhatsNew(
        releasesList: SnapshotStateList<Release>,
        checkWhatsNewAlertDialogState: AlertDialogState
    ) {
        DisposableEffect(Unit) {
            checkWhatsNewCompose(
                releases = listOf(),
                currVersion = BuildConfig.VERSION_CODE,
                showWhatsNewDialog = { releases ->
                    releasesList.addAll(releases)
                    checkWhatsNewAlertDialogState.show()
                }
            )
            onDispose {
                releasesList.clear()
            }
        }
    }

    @Composable
    private fun getCheckWhatsNewAlertDialogState(releasesList: SnapshotStateList<Release>) = rememberAlertDialogState().apply {
        DialogMember {
            WhatsNewAlertDialog(alertDialogState = this, releases = releasesList.toImmutableList())
        }
    }

    @Composable
    private fun getDonateAlertDialogState() =
        rememberAlertDialogState().apply {
            DialogMember {
                DonateAlertDialog(alertDialogState = this)
            }
        }

    @Composable
    private fun getRateStarsAlertDialogState() = rememberAlertDialogState().apply {
        DialogMember {
            RateStarsAlertDialog(alertDialogState = this, onRating = ::rateStarsRedirectAndThankYou)
        }
    }

    private fun launchSettings() {
        hideKeyboard()
        startActivity(Intent(this, SettingsActivity::class.java))
    }

    private fun launchAbout() {
        val faqItems = ArrayList<FAQItem>()

        if (!resources.getBoolean(R.bool.hide_google_relations)) {
            faqItems.add(FAQItem(R.string.faq_2_title_commons, R.string.faq_2_text_commons))
            faqItems.add(FAQItem(R.string.faq_6_title_commons, R.string.faq_6_text_commons))
        }

        startAboutActivity(R.string.app_name, 0, BuildConfig.VERSION_NAME, faqItems, false)
    }
}
