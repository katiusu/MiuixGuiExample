package com.katiusu.miuixgui.example.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.katiusu.miuixgui.example.ShellBottomSpace
import com.katiusu.miuixgui.example.data.AppSettings
import com.katiusu.miuixgui.example.ui.component.AppSection
import com.katiusu.miuixgui.example.ui.component.BodyText
import com.katiusu.miuixgui.example.ui.component.PageHeader
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.preference.SwitchPreference
import top.yukonga.miuix.kmp.theme.ColorSchemeMode

@Composable
fun SettingsScreen(settings: AppSettings) {
    val modes = AppSettings.selectableModes
    val currentMode = settings.themeMode

    LazyColumn(
        modifier = Modifier.fillMaxSize().statusBarsPadding(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = ShellBottomSpace),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item { PageHeader(title = "设置", subtitle = "外观与交互") }

        item {
            AppSection(title = "外观") {
                SwitchPreference(
                    title = "背景模糊",
                    summary = "玻璃底栏对下层内容做实时模糊，关闭后底栏不再采样背景",
                    checked = settings.backdropBlurEnabled,
                    onCheckedChange = { settings.updateBackdropBlur(it) },
                )
                BasicComponent(
                    title = "主题模式",
                    summary = AppSettings.labelOf(currentMode),
                    onClick = {
                        val next = modes[(modes.indexOf(currentMode) + 1) % modes.size]
                        settings.updateThemeMode(next)
                    },
                )
                BasicComponent(
                    title = "重置外观",
                    summary = "恢复跟随系统主题与背景模糊",
                    onClick = {
                        settings.updateThemeMode(ColorSchemeMode.System)
                        settings.updateBackdropBlur(true)
                    },
                )
            }
        }

        item {
            AppSection(title = "说明") {
                BodyText("以上选项只影响界面外观，模板本身不读取、不上传任何设备信息。")
            }
        }
    }
}
