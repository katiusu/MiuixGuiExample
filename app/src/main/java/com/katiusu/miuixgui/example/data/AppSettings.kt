package com.katiusu.miuixgui.example.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import top.yukonga.miuix.kmp.theme.ColorSchemeMode

/** 应用设置：SharedPreferences 持久化 + Compose 可观察状态。 */
class AppSettings(context: Context) {

    private val prefs = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    var themeMode: ColorSchemeMode by mutableStateOf(readThemeMode())
        private set

    var backdropBlurEnabled: Boolean by mutableStateOf(prefs.getBoolean(KEY_BLUR, true))
        private set

    fun updateThemeMode(mode: ColorSchemeMode) {
        themeMode = mode
        prefs.edit().putString(KEY_THEME, mode.name).apply()
    }

    fun updateBackdropBlur(enabled: Boolean) {
        backdropBlurEnabled = enabled
        prefs.edit().putBoolean(KEY_BLUR, enabled).apply()
    }

    private fun readThemeMode(): ColorSchemeMode {
        val stored = prefs.getString(KEY_THEME, null) ?: return ColorSchemeMode.System
        return runCatching { ColorSchemeMode.valueOf(stored) }.getOrDefault(ColorSchemeMode.System)
    }

    companion object {
        private const val FILE_NAME = "miuix_gui_settings"
        private const val KEY_THEME = "theme_mode"
        private const val KEY_BLUR = "backdrop_blur"

        /** 可选项：Monet 取色模式在部分机型会自动回退，模板里不暴露。 */
        val selectableModes: List<ColorSchemeMode> = listOf(
            ColorSchemeMode.System,
            ColorSchemeMode.Light,
            ColorSchemeMode.Dark,
        )

        fun labelOf(mode: ColorSchemeMode): String = when (mode) {
            ColorSchemeMode.Light -> "浅色"
            ColorSchemeMode.Dark -> "深色"
            ColorSchemeMode.MonetSystem -> "跟随系统（取色）"
            ColorSchemeMode.MonetLight -> "浅色（取色）"
            ColorSchemeMode.MonetDark -> "深色（取色）"
            ColorSchemeMode.System -> "跟随系统"
        }
    }
}
