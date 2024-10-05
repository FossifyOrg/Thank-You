package org.fossify.thankyou.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.collections.immutable.toImmutableList
import org.fossify.commons.activities.BaseComposeActivity
import org.fossify.commons.compose.alert_dialog.AlertDialogState
import org.fossify.commons.compose.alert_dialog.rememberAlertDialogState
import org.fossify.commons.compose.extensions.appLaunchedCompose
import org.fossify.commons.compose.extensions.checkWhatsNewCompose
import org.fossify.commons.compose.extensions.enableEdgeToEdgeSimple
import org.fossify.commons.compose.extensions.linkColor
import org.fossify.commons.compose.extensions.onEventValue
import org.fossify.commons.compose.extensions.rateStarsRedirectAndThankYou
import org.fossify.commons.compose.theme.AppThemeSurface
import org.fossify.commons.dialogs.DonateAlertDialog
import org.fossify.commons.dialogs.RateStarsAlertDialog
import org.fossify.commons.dialogs.WhatsNewAlertDialog
import org.fossify.commons.extensions.hideKeyboard
import org.fossify.commons.extensions.launchMoreAppsFromUsIntent
import org.fossify.commons.extensions.toast
import org.fossify.commons.models.FAQItem
import org.fossify.commons.models.Release
import org.fossify.thankyou.BuildConfig
import org.fossify.thankyou.R
import org.fossify.thankyou.extensions.config
import org.fossify.thankyou.extensions.getAllFossifyApps
import org.fossify.thankyou.extensions.getFakeFossifyApps
import org.fossify.thankyou.extensions.getFossifyAppsFlow
import org.fossify.thankyou.extensions.startAboutActivity
import org.fossify.thankyou.ui.screens.MainScreen

class MainActivity : BaseComposeActivity() {

    private val preferences by lazy { config }
    private val allAppsFlow by lazy { getFossifyAppsFlow(::getAllFossifyApps) }
    private val fakeAppsFlow by lazy { getFossifyAppsFlow(::getFakeFossifyApps) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdgeSimple()
        setContent {
            AppThemeSurface {
                val releasesList = remember { mutableStateListOf<Release>() }
                val checkWhatsNewAlertDialogState = getCheckWhatsNewAlertDialogState(releasesList)
                val linkColor = linkColor()
                val showMoreApps =
                    onEventValue { !resources.getBoolean(org.fossify.commons.R.bool.hide_google_relations) }

                val showThankYouNotice by preferences.showThankYouNoticeFlow
                    .collectAsStateWithLifecycle(preferences.showThankYouNotice)

                val allApps by allAppsFlow.collectAsStateWithLifecycle(listOf())
                val fakeApps by fakeAppsFlow.collectAsStateWithLifecycle(listOf())

                MainScreen(
                    allApps = allApps,
                    fakeApps = fakeApps,
                    linkColor = linkColor,
                    showMoreApps = showMoreApps,
                    showThankYouNotice = showThankYouNotice,
                    openSettings = ::launchSettings,
                    openAbout = ::launchAbout,
                    moreAppsFromUs = ::launchMoreAppsFromUsIntent,
                    launchApp = ::launchApp,
                    uninstallApp = ::uninstallApp,
                    hideThankYouNotice = {
                        preferences.showThankYouNotice = false
                    }
                )

                AppLaunched()
                CheckWhatsNew(
                    releasesList = releasesList,
                    checkWhatsNewAlertDialogState = checkWhatsNewAlertDialogState
                )
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
    private fun getCheckWhatsNewAlertDialogState(releasesList: SnapshotStateList<Release>) =
        rememberAlertDialogState().apply {
            DialogMember {
                WhatsNewAlertDialog(
                    alertDialogState = this,
                    releases = releasesList.toImmutableList()
                )
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
        if (!resources.getBoolean(org.fossify.commons.R.bool.hide_google_relations)) {
            faqItems.add(
                FAQItem(
                    title = org.fossify.commons.R.string.faq_2_title_commons,
                    text = org.fossify.commons.R.string.faq_2_text_commons
                )
            )
            faqItems.add(
                FAQItem(
                    title = org.fossify.commons.R.string.faq_6_title_commons,
                    text = org.fossify.commons.R.string.faq_6_text_commons
                )
            )
        }

        startAboutActivity(
            appNameId = R.string.app_name,
            licenseMask = 0,
            versionName = BuildConfig.VERSION_NAME,
            packageName = packageName,
            faqItems = faqItems,
            showFAQBeforeMail = false
        )
    }

    private fun launchApp(packageName: String) {
        if (packageName == this.packageName) {
            toast(org.fossify.commons.R.string.hello)
        } else {
            startActivity(
                packageManager.getLaunchIntentForPackage(packageName)
            )
        }
    }

    private fun uninstallApp(packageName: String) {
        Intent(Intent.ACTION_DELETE).apply {
            data = Uri.fromParts("package", packageName, null)
            @Suppress("DEPRECATION")
            startActivityForResult(this, UNINSTALL_APP_REQUEST_CODE)
        }
    }

    companion object {
        private const val UNINSTALL_APP_REQUEST_CODE = 50
    }
}
