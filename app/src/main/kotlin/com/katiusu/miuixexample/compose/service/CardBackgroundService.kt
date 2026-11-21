package com.katiusu.miuixexample.compose.service

import android.content.Context
import android.net.Uri
import java.io.File

internal enum class CardBackgroundType(
    val preferenceKey: String,
    val fileBaseName: String,
) {
    Small("pref_card_bg_small_path", "miuixexample_bg_small"),
    Big("pref_card_bg_big_path", "miuixexample_bg_big"),
    Expand("pref_card_bg_expand_path", "miuixexample_bg_expand"),
}

internal object CardBackgroundService {
    private val publicDirectory = File("/sdcard/Pictures/MiuixExample")

    fun save(context: Context, source: Uri, type: CardBackgroundType): Result<String> = runCatching {
        val extension = if (context.contentResolver.getType(source) == "image/gif") "gif" else "png"
        val destination = File(publicDirectory, "${type.fileBaseName}.$extension")
        val temporary = File.createTempFile(type.fileBaseName, ".$extension", context.cacheDir)
        try {
            context.contentResolver.openInputStream(source).use { input ->
                requireNotNull(input) { "Cannot open selected image" }
                temporary.outputStream().use(input::copyTo)
            }
            publicDirectory.mkdirs()
            val copiedDirectly = runCatching {
                temporary.copyTo(destination, overwrite = true)
                destination.setReadable(true, false)
                removeSiblingFiles(type, destination)
                true
            }.getOrDefault(false)
            check(copiedDirectly) { "Cannot save selected image" }
            destination.path
        } finally {
            temporary.delete()
        }
    }

    fun delete(type: CardBackgroundType, configuredPath: String): Boolean {
        val targets = buildSet {
            add(File(publicDirectory, "${type.fileBaseName}.png"))
            add(File(publicDirectory, "${type.fileBaseName}.gif"))
            configuredPath.takeIf(String::isNotBlank)?.let { add(File(it)) }
        }.filter(::isAllowedTarget)
        var success = true
        targets.forEach { file ->
            if (file.exists()) success = file.delete() && success
        }
        return success
    }

    private fun removeSiblingFiles(type: CardBackgroundType, destination: File) {
        siblingFile(type, destination).delete()
    }

    private fun siblingFile(type: CardBackgroundType, destination: File): File = File(
        publicDirectory,
        "${type.fileBaseName}.${if (destination.extension == "gif") "png" else "gif"}",
    )

    private fun isAllowedTarget(file: File): Boolean = runCatching {
        file.name.matches(Regex("miuixexample_bg_(small|big|expand)\\.(png|gif)")) &&
            file.parentFile?.canonicalFile == publicDirectory.canonicalFile
    }.getOrDefault(false)

}
