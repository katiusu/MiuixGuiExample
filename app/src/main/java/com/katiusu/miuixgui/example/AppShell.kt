package com.katiusu.miuixgui.example

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.katiusu.miuixgui.example.data.AppSettings
import com.katiusu.miuixgui.example.ui.screen.AboutScreen
import com.katiusu.miuixgui.example.ui.screen.FeaturesScreen
import com.katiusu.miuixgui.example.ui.screen.HomeScreen
import com.katiusu.miuixgui.example.ui.screen.SettingsScreen
import top.yukonga.miuix.kmp.blur.layerBackdrop
import top.yukonga.miuix.kmp.blur.rememberLayerBackdrop
import top.yukonga.miuix.kmp.glass.GlassNavigationBar
import top.yukonga.miuix.kmp.glass.GlassNavigationItem
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.glass.HomeOutline
import top.yukonga.miuix.kmp.icon.glass.Info
import top.yukonga.miuix.kmp.icon.glass.ListView
import top.yukonga.miuix.kmp.icon.glass.SettingsOutline
import top.yukonga.miuix.kmp.theme.MiuixTheme

/** 标签页：图标与文字全部取自 miuix 官方图标集，不自造图标。 */
private enum class ShellTab(val label: String, val icon: ImageVector) {
    Home("主页", MiuixIcons.Glass.HomeOutline),
    Features("功能", MiuixIcons.Glass.ListView),
    Settings("设置", MiuixIcons.Glass.SettingsOutline),
    About("关于", MiuixIcons.Glass.Info),
}

/** 内容区为悬浮玻璃底栏预留的底部空间（含系统导航栏）。 */
internal val ShellBottomSpace = 132.dp

@Composable
fun AppShell(settings: AppSettings) {
    val tabs = ShellTab.entries
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }

    // 背景采样层：玻璃底栏从这一层取色做实时模糊/折射。
    val backdrop = rememberLayerBackdrop {
        drawRect(MiuixTheme.colorScheme.surface)
        drawContent()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize().layerBackdrop(backdrop)) {
            when (tabs[selectedIndex]) {
                ShellTab.Home -> HomeScreen(settings = settings)
                ShellTab.Features -> FeaturesScreen()
                ShellTab.Settings -> SettingsScreen(settings = settings)
                ShellTab.About -> AboutScreen()
            }
        }

        GlassNavigationBar(
            items = tabs.map { GlassNavigationItem(icon = it.icon, label = it.label) },
            selectedIndex = selectedIndex,
            onSelect = { selectedIndex = it },
            backdrop = if (settings.backdropBlurEnabled) backdrop else null,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp),
        )
    }
}
