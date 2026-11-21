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
import com.katiusu.miuixgui.example.data.DeviceInfo
import com.katiusu.miuixgui.example.ui.component.AppSection
import com.katiusu.miuixgui.example.ui.component.BodyText
import com.katiusu.miuixgui.example.ui.component.PageHeader
import top.yukonga.miuix.kmp.basic.BasicComponent

@Composable
fun AboutScreen() {
    val context = LocalContext.current
    val device = remember { DeviceInfo.read(context) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().statusBarsPadding(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = ShellBottomSpace),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item { PageHeader(title = "关于", subtitle = "Miuix GUI 模板") }

        item {
            AppSection(title = "应用") {
                BasicComponent(title = "版本", summary = device.appVersion)
                BasicComponent(title = "界面组件库", summary = "miuix（top.yukonga.miuix.kmp）")
                BasicComponent(title = "许可", summary = "MIT License")
            }
        }

        item {
            AppSection(title = "许可声明") {
                BodyText("本项目以 MIT 许可发布，可自由使用、修改与再分发；界面组件来自 miuix，按其自身许可（Apache-2.0）使用。")
            }
        }

        item {
            AppSection(title = "致谢") {
                BodyText("感谢 miuix 项目提供 HyperOS 风格组件，本模板的排版与交互规则全部沿用其官方实现。")
            }
        }
    }
}
