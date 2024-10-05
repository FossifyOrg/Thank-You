package org.fossify.thankyou.helpers

import android.content.Context
import kotlinx.coroutines.flow.Flow
import org.fossify.commons.helpers.BaseConfig

class Config(context: Context) : BaseConfig(context) {
    companion object {
        fun newInstance(context: Context) = Config(context)

        const val SHOW_THANK_YOU_NOTICE = "show_thank_you_notice"
    }

    var showThankYouNotice: Boolean
        get() = prefs.getBoolean(SHOW_THANK_YOU_NOTICE, true)
        set(showThankYouNotice) = prefs.edit().putBoolean(
            SHOW_THANK_YOU_NOTICE, showThankYouNotice
        ).apply()

    val showThankYouNoticeFlow: Flow<Boolean> = ::showThankYouNotice.asFlowNonNull()
}
