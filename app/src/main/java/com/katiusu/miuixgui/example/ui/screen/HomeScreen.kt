package com.katiusu.miuixgui.example.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.katiusu.miuixgui.example.ShellBottomSpace
import com.katiusu.miuixgui.example.data.AppSettings
import com.katiusu.miuixgui.example.data.DeviceInfo
import com.katiusu.miuixgui.example.ui.component.AppSection
import com.katiusu.miuixgui.example.ui.component.BannerCard
import com.katiusu.miuixgui.example.ui.component.PageHeader
import com.katiusu.miuixgui.example.ui.component.StatTile
import com.katiusu.miuixgui.example.ui.component.TileRow
import top.yukonga.miuix.kmp.basic.BasicComponent

@Composable
fun HomeScreen(settings: AppSettings) {
    val context = LocalContext.current
    val device = remember { DeviceInfo.read(context) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().statusBarsPadding(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = ShellBottomSpace),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item { PageHeader(title = "主页", subtitle = "界面骨架与运行环境") }

        item {
            BannerCard(
                title = "Miuix GUI 模板",
                subtitle = "Compose + miuix 组件库：玻璃底栏、连续曲率圆角、深浅色自动跟随。",
            )
        }

        item {
            TileRow {
                StatTile(label = "页面", value = "4")
                StatTile(label = "主题", value = AppSettings.labelOf(settings.themeMode))
            }
        }

        item {
            AppSection(title = "设备信息") {
                BasicComponent(title = "设备代号", summary = device.deviceName)
                BasicComponent(title = "机型", summary = device.model)
                BasicComponent(title = "系统版本", summary = device.systemVersion)
                BasicComponent(title = "Android 版本", summary = device.androidVersion)
                BasicComponent(title = "CPU 架构", summary = device.cpuAbi)
                BasicComponent(title = "应用版本", summary = device.appVersion)
            }
        }
    }
}
