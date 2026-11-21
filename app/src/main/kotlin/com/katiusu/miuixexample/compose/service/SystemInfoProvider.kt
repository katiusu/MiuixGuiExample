package com.katiusu.miuixexample.compose.service

import android.content.Context
import android.os.Build
import android.provider.Settings
import com.katiusu.miuixexample.BuildConfig
import com.katiusu.miuixexample.utils.SystemPropertyReader

internal data class HomeSystemInfo(
    val systemVersion: String,
    val appVersion: String,
    val appVersionCode: Int,
    val deviceModel: String,
    val focusProtocolVersion: Int,
    val androidSdkVersion: Int,
)

internal object SystemInfoProvider {
    fun load(context: Context): HomeSystemInfo = HomeSystemInfo(
        systemVersion = SystemPropertyReader.get("ro.build.version.incremental")
            .ifBlank { Build.VERSION.INCREMENTAL.orEmpty() },
        appVersion = BuildConfig.VERSION_NAME,
        appVersionCode = BuildConfig.VERSION_CODE,
        deviceModel = SystemPropertyReader.get("ro.product.marketname")
            .ifBlank { Build.MODEL.orEmpty() },
        focusProtocolVersion = Settings.System.getInt(
            context.contentResolver,
            "notification_focus_protocol",
            0,
        ),
        androidSdkVersion = Build.VERSION.SDK_INT,
    )
}
