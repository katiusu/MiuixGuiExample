package com.katiusu.miuixexample.compose.service

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.io.File

internal object KeepAliveService {
    private val publicDirectory = File("/sdcard/Pictures/MiuixExample")

    fun refresh(context: Context) {
        context.sendBroadcast(
            Intent(ACTION_REFRESH_KEEP_CARD).setPackage(SYSTEM_UI_PACKAGE),
            SEND_CARD_PERMISSION,
        )
    }

    fun saveIcon(context: Context, source: Uri, oldPath: String): Result<String> = runCatching {
        val destination = File(publicDirectory, "miuixexample_keep_icon_${System.currentTimeMillis()}.png")
        val temporary = File.createTempFile("miuixexample_keep_icon_", ".png", context.cacheDir)
        try {
            context.contentResolver.openInputStream(source).use { input ->
                requireNotNull(input) { "Cannot open selected image" }
                temporary.outputStream().use(input::copyTo)
            }
            publicDirectory.mkdirs()
            val copiedDirectly = runCatching {
                temporary.copyTo(destination, overwrite = true)
                destination.setReadable(true, false)
                true
            }.getOrDefault(false)
            check(copiedDirectly) { "Cannot save selected image" }
            if (oldPath.isNotBlank() && oldPath != destination.path) deleteIcon(oldPath)
            destination.path
        } finally {
            temporary.delete()
        }
    }

    fun deleteIcon(configuredPath: String): Boolean {
        val file = File(configuredPath)
        if (!isAllowedTarget(file)) return false
        if (!file.exists()) return true
        return file.delete()
    }

    private fun isAllowedTarget(file: File): Boolean = runCatching {
        file.name.matches(Regex("miuixexample_keep_icon_[0-9]+\\.png")) &&
            file.parentFile?.canonicalFile == publicDirectory.canonicalFile
    }.getOrDefault(false)

    private const val ACTION_REFRESH_KEEP_CARD = "com.katiusu.miuixexample.action.REFRESH_KEEP_CARD"
    private const val SYSTEM_UI_PACKAGE = "com.android.systemui"
    private const val SEND_CARD_PERMISSION = "com.katiusu.miuixexample.SEND_CARD"
}
