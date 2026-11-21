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
import com.katiusu.miuixgui.example.ui.component.AppSection
import com.katiusu.miuixgui.example.ui.component.BodyText
import com.katiusu.miuixgui.example.ui.component.PageHeader
import top.yukonga.miuix.kmp.basic.BasicComponent

@Composable
fun FeaturesScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize().statusBarsPadding(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = ShellBottomSpace),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item { PageHeader(title = "功能", subtitle = "模板内置的界面能力") }

        item {
            AppSection(title = "界面骨架") {
                BasicComponent(title = "玻璃底栏", summary = "miuix GlassNavigationBar，实时采样下层内容做模糊")
                BasicComponent(title = "连续曲率圆角", summary = "统一由 miuix squircleClip 提供，不使用普通圆角")
                BasicComponent(title = "深浅色主题", summary = "主题模式可在设置页切换，系统栏图标自动同步")
                BasicComponent(title = "边缘到边缘", summary = "内容铺到状态栏与导航栏之下，底部为悬浮底栏留空")
            }
        }

        item {
            AppSection(title = "组件来源") {
                BodyText("卡片、分组标题、设置行、开关、底栏全部来自 miuix 官方组件，模板内不包含任何自绘 UI 样式。")
            }
        }
    }
}
