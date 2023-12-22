package org.fossify.thankyou.helpers

import android.content.Context
import kotlinx.coroutines.flow.Flow
import org.fossify.commons.helpers.BaseConfig

class Config(context: Context) : BaseConfig(context) {
    companion object {
        const val HIDE_LAUNCHER_ICON = "hide_launcher_icon"
        fun newInstance(context: Context) = Config(context)
    }

    var hideLauncherIcon: Boolean
        get() = prefs.getBoolean(HIDE_LAUNCHER_ICON, false)
        set(hideLauncherIcon) = prefs.edit().putBoolean(HIDE_LAUNCHER_ICON, hideLauncherIcon).apply()

    val hideLauncherIconFlow: Flow<Boolean> = ::hideLauncherIcon.asFlowNonNull()
}
