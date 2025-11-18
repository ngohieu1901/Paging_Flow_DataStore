package com.hieunt.base.constants

import android.Manifest
import android.content.res.Resources
import android.os.Build

object Constants {
    const val PRIVACY_POLICY =
        "https://amazic.net/Privacy-Policy-270pdf.html"

    const val BASE_URL = ""

    object IntentKeys {
        const val SCREEN = "SCREEN"
        const val SPLASH_ACTIVITY = "SplashActivity"
        const val FILES_MODEL = "FilesModel"
    }

    object Screen {
        val width: Int
            get() = Resources.getSystem().displayMetrics.widthPixels

        val height: Int
            get() = Resources.getSystem().displayMetrics.heightPixels
    }

    val STORAGE_PERMISSION_API_SMALLER_30 = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
    )

    val STORAGE_PERMISSION = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) arrayOf(
        Manifest.permission.READ_MEDIA_AUDIO
    ) else arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    val NOTIFICATION_PERMISSION = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        Manifest.permission.POST_NOTIFICATIONS
    else ""

}