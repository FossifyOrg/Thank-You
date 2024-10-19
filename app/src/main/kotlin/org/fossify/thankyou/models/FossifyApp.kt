package org.fossify.thankyou.models

import android.graphics.drawable.Drawable
import androidx.compose.runtime.Immutable

@Immutable
data class FossifyApp(
    val name: String,
    val icon: Drawable?,
    val packageName: String,
    val versionName: String?,
    val signerName: String?,
    val installerPackage: String?,
    val installerName: String?,
    val verified: Boolean
)
