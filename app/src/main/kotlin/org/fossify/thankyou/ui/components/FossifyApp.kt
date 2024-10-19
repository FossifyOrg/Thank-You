package org.fossify.thankyou.ui.components

import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import org.fossify.commons.compose.extensions.BooleanPreviewParameterProvider
import org.fossify.commons.compose.theme.Shapes
import org.fossify.commons.compose.theme.SimpleTheme
import org.fossify.thankyou.R
import org.fossify.thankyou.models.FossifyApp

@Composable
internal fun FossifyApp(
    app: FossifyApp,
    modifier: Modifier = Modifier,
    launchApp: (packageName: String) -> Unit,
    uninstallApp: (packageName: String) -> Unit
) {
    val name = app.name
    val verified = app.verified
    val packageName = app.packageName

    ListItem(
        modifier = Modifier
            .clickable(
                enabled = verified,
                onClick = { launchApp(packageName) }
            )
            .then(modifier),
        headlineContent = {
            AppName(name = name, verified = verified)
        },
        leadingContent = {
            AppIcon(icon = app.icon, verified = verified)
        },
        supportingContent = {
            AppInfo(
                versionName = app.versionName,
                packageName = packageName,
                signerName = app.signerName,
                installerName = app.installerName
            )
        },
        trailingContent = {
            TrailingContent(
                verified = verified,
                packageName = packageName,
                launchApp = launchApp,
                uninstallApp = uninstallApp
            )
        }
    )
}

@Composable
private fun AppName(
    name: String,
    verified: Boolean
) {
    Text(
        text = name,
        color = when {
            verified -> SimpleTheme.colorScheme.onSurface
            else -> SimpleTheme.colorScheme.error
        },
        fontWeight = FontWeight.Medium,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun AppIcon(
    icon: Drawable?,
    verified: Boolean
) {
    Column {
        Spacer(Modifier.height(4.dp))
        BadgedBox(
            badge = {
                if (!verified) {
                    Icon(
                        imageVector = Icons.Rounded.Error,
                        contentDescription = null,
                        tint = SimpleTheme.colorScheme.error,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(SimpleTheme.colorScheme.errorContainer)
                    )
                }
            },
            content = {
                Column {
                    Image(
                        modifier = Modifier.size(48.dp),
                        painter = rememberDrawablePainter(icon),
                        contentDescription = null,
                    )
                }
            }
        )
    }
}

@Composable
private fun AppInfo(
    versionName: String?,
    packageName: String,
    signerName: String?,
    installerName: String?
) {
    Column {
        Text(
            text = stringResource(
                R.string.version,
                versionName ?: stringResource(org.fossify.commons.R.string.unknown)
            )
        )
        Text(
            text = stringResource(R.string.package_id, packageName),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = stringResource(
                R.string.signed_by,
                signerName ?: stringResource(org.fossify.commons.R.string.unknown)
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = stringResource(
                R.string.installed_by,
                installerName ?: stringResource(org.fossify.commons.R.string.unknown)
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun TrailingContent(
    verified: Boolean,
    packageName: String,
    launchApp: (packageName: String) -> Unit,
    uninstallApp: (packageName: String) -> Unit
) {
    Icon(
        imageVector = if (verified) Icons.AutoMirrored.Rounded.OpenInNew else Icons.Rounded.Close,
        contentDescription = null,
        tint = SimpleTheme.colorScheme.primary,
        modifier = Modifier
            .clip(Shapes.extraLarge)
            .clickable(onClick = {
                if (verified) {
                    launchApp(packageName)
                } else {
                    uninstallApp(packageName)
                }
            })
            .padding(12.dp),
    )
}

@Preview
@Composable
private fun PreviewFossifyApp(
    @PreviewParameter(BooleanPreviewParameterProvider::class) verified: Boolean
) {
    FossifyApp(
        app = FossifyApp(
            name = "Fossify Thank You",
            icon = AppCompatResources.getDrawable(LocalContext.current, R.mipmap.ic_launcher),
            packageName = "org.fossify.thankyou.debug",
            versionName = "1.2.1",
            signerName = if (verified) "Fossify" else null,
            installerName = "Fossify Store",
            installerPackage = "org.fossify.store",
            verified = verified
        ),
        launchApp = {},
        uninstallApp = {}
    )
}