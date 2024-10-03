package org.fossify.thankyou.extensions

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.pm.PackageManager.SIGNATURE_MATCH
import org.fossify.commons.extensions.getSignatures
import org.fossify.commons.helpers.isRPlus
import org.fossify.thankyou.models.FossifyApp
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

fun Context.getFossifyAppsFlow(
    getApps: () -> List<FossifyApp>
) = createBroadcastFlow(
    intentFilter = IntentFilter().apply {
        addAction(Intent.ACTION_PACKAGE_ADDED)
        addAction(Intent.ACTION_PACKAGE_CHANGED)
        addAction(Intent.ACTION_PACKAGE_REPLACED)
        addAction(Intent.ACTION_PACKAGE_REMOVED)
        addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED)
        addDataScheme("package")
    },
    emitOnCollect = true,
    value = { getApps() }
)

fun Context.getAllFossifyApps(): List<FossifyApp> {
    val packageName = packageName
    return with(packageManager) {
        getInstalledPackages(0)
            .filter { checkSignatures(packageName, it.packageName) == SIGNATURE_MATCH }
            .map {
                val `package` = it.packageName
                val installerPackage = getInstallerPackage(`package`)
                FossifyApp(
                    name = getApplicationLabel(it.applicationInfo).toString(),
                    icon = getApplicationIcon(it.applicationInfo),
                    packageName = `package`,
                    versionName = it.versionName,
                    signerName = getSignerName(`package`),
                    installerPackage = installerPackage,
                    installerName = getInstallerLabel(installerPackage),
                    verified = true
                )
            }.sortedBy { it.packageName }
    }
}

fun Context.getFakeFossifyApps(): List<FossifyApp> {
    val packageName = packageName
    return with(packageManager) {
        getInstalledPackages(0)
            .filter { it.packageName.startsWith("org.fossify.") }
            .filter { checkSignatures(packageName, it.packageName) != SIGNATURE_MATCH }
            .map {
                val `package` = it.packageName
                val installerPackage = getInstallerPackage(`package`)
                FossifyApp(
                    name = getApplicationLabel(it.applicationInfo).toString(),
                    icon = getApplicationIcon(it.applicationInfo),
                    packageName = `package`,
                    versionName = it.versionName,
                    signerName = getSignerName(`package`),
                    installerPackage = installerPackage,
                    installerName = getInstallerLabel(installerPackage),
                    verified = false
                )
            }.sortedBy { it.packageName }
    }
}

fun PackageManager.getInstallerLabel(packageName: String?): String? {
    if (packageName == null) return null
    return try {
        val appInfo = getApplicationInfo(packageName, 0)
        getApplicationLabel(appInfo).toString()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun PackageManager.getInstallerPackage(packageName: String): String? {
    return try {
        if (isRPlus()) {
            val sourceInfo = getInstallSourceInfo(packageName)
            sourceInfo.initiatingPackageName ?: sourceInfo.installingPackageName
        } else {
            @Suppress("DEPRECATION")
            getInstallerPackageName(packageName)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}

fun PackageManager.getSignerName(packageName: String?): String? {
    try {
        if (packageName == null) return null
        val signatures = getSignatures(packageName).orEmpty()
        for (signature in signatures) {
            val certificateFactory = CertificateFactory.getInstance("X.509")
            val certificate = certificateFactory.generateCertificate(
                signature.toByteArray().inputStream()
            ) as X509Certificate

            return certificate.subjectX500Principal.name
                .split(",")
                .find { it.trim().startsWith("O=") }
                ?.substringAfter("O=")
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return null
}

