package com.katiusu.miuixgui.example.ui.theme

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import top.yukonga.miuix.kmp.theme.ColorSchemeMode
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.ThemeController

@Composable
fun AppTheme(
    themeMode: ColorSchemeMode = ColorSchemeMode.System,
    content: @Composable () -> Unit,
) {
    val controller = remember(themeMode) { ThemeController(themeMode) }
    MiuixTheme(
        controller = controller,
        content = {
            // enableEdgeToEdge 的 auto 只跟随系统深浅模式，应用内切换主题后
            // 需要按实际 surface 亮度重新设置系统栏图标颜色。
            val isDark = MiuixTheme.colorScheme.surface.luminance() < 0.5f
            val view = LocalView.current
            if (!view.isInEditMode) {
                SideEffect {
                    val window = (view.context as? Activity)?.window ?: return@SideEffect
                    val controller = WindowCompat.getInsetsController(window, view)
                    controller.isAppearanceLightStatusBars = !isDark
                    controller.isAppearanceLightNavigationBars = !isDark
                }
            }
            content()
        },
    )
}
