package org.fossify.thankyou.helpers

import android.content.Context
import kotlinx.coroutines.flow.Flow
import org.fossify.commons.helpers.BaseConfig

class Config(context: Context) : BaseConfig(context) {
    companion object {
        fun newInstance(context: Context) = Config(context)

        const val SHOW_THANK_YOU_NOTICE = "show_thank_you_notice"
        const val HIDE_LAUNCHER_ICON = "hide_launcher_icon"
    }

    var showThankYouNotice: Boolean
        get() = prefs.getBoolean(SHOW_THANK_YOU_NOTICE, true)
        set(showThankYouNotice) = prefs.edit().putBoolean(
            SHOW_THANK_YOU_NOTICE, showThankYouNotice
        ).apply()

    var hideLauncherIcon: Boolean
        get() = prefs.getBoolean(HIDE_LAUNCHER_ICON, false)
        set(hideLauncherIcon) = prefs.edit().putBoolean(
            HIDE_LAUNCHER_ICON, hideLauncherIcon
        ).apply()

    val hideLauncherIconFlow: Flow<Boolean> = ::hideLauncherIcon.asFlowNonNull()
    val showThankYouNoticeFlow: Flow<Boolean> = ::showThankYouNotice.asFlowNonNull()
}
