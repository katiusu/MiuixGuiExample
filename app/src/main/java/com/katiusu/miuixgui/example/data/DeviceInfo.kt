package com.katiusu.miuixgui.example.data

import android.content.Context
import android.os.Build

/** 只读设备信息，供主页展示。 */
data class DeviceInfo(
    val deviceName: String,
    val model: String,
    val systemVersion: String,
    val androidVersion: String,
    val cpuAbi: String,
    val appVersion: String,
) {
    companion object {
        fun read(context: Context): DeviceInfo {
            val packageInfo = runCatching {
                context.packageManager.getPackageInfo(context.packageName, 0)
            }.getOrNull()
            val versionName = packageInfo?.versionName ?: "-"
            return DeviceInfo(
                deviceName = Build.DEVICE,
                model = Build.MODEL,
                systemVersion = Build.DISPLAY,
                androidVersion = "Android " + Build.VERSION.RELEASE + "（API " + Build.VERSION.SDK_INT + "）",
                cpuAbi = Build.SUPPORTED_ABIS.firstOrNull() ?: "-",
                appVersion = versionName,
            )
        }
    }
}
