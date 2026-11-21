package com.katiusu.miuixgui.example.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.squircle.squircleClip
import top.yukonga.miuix.kmp.theme.MiuixTheme

/** 卡片圆角：与 miuix 组件保持同一套连续曲率圆角。 */
val CardCornerRadius: Dp = 26.dp

/** 分组标题 + 卡片容器（等价于 miuix 设置页的分组写法）。 */
@Composable
fun AppSection(
    title: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        if (title != null) {
            SmallTitle(
                text = title,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 6.dp),
            )
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .squircleClip(CardCornerRadius),
            colors = CardDefaults.defaultColors(),
        ) {
            Column { content() }
        }
    }
}

/** 并列数字小卡（主页顶部）。 */
@Composable
fun RowScope.StatTile(label: String, value: String) {
    Card(
        modifier = Modifier
            .weight(1f)
            .squircleClip(CardCornerRadius),
        colors = CardDefaults.defaultColors(),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = value,
                style = MiuixTheme.textStyles.headline1,
                color = MiuixTheme.colorScheme.onSurface,
            )
            Text(
                text = label,
                style = MiuixTheme.textStyles.footnote2,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            )
        }
    }
}

/** 段落正文：统一走 miuix 文字样式，不手写字号。 */
@Composable
fun BodyText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MiuixTheme.textStyles.body2,
        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
        modifier = modifier.padding(horizontal = 18.dp, vertical = 14.dp),
    )
}

/** 主页顶部横幅：容器用连续曲率圆角，颜色取自主题 surface。 */
@Composable
fun BannerCard(title: String, subtitle: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .squircleClip(32.dp)
            .background(MiuixTheme.colorScheme.surface)
            .padding(horizontal = 20.dp, vertical = 22.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = title,
            style = MiuixTheme.textStyles.headline1,
            color = MiuixTheme.colorScheme.onSurface,
        )
        Text(
            text = subtitle,
            style = MiuixTheme.textStyles.body2,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
        )
    }
}

/** 页面标题（大标题 + 说明），所有页面共用同一种排版。 */
@Composable
fun PageHeader(title: String, subtitle: String) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(start = 4.dp, end = 4.dp, top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = title,
            style = MiuixTheme.textStyles.headline1,
            color = MiuixTheme.colorScheme.onSurface,
        )
        Text(
            text = subtitle,
            style = MiuixTheme.textStyles.footnote2,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
        )
    }
}

/** 两列小卡容器。 */
@Composable
fun TileRow(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        content = content,
    )
}
