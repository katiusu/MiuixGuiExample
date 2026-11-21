package com.katiusu.miuixgui.example.ui.component.search

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.katiusu.miuixgui.example.R
import com.katiusu.miuixgui.example.ui.component.appTopBarSafeInsets
import com.katiusu.miuixgui.example.prefs.OptionRegistry
import com.katiusu.miuixgui.example.prefs.OptionSpec
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.glass.GlassSearchOverlayDefaults
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.basic.Text as MiuixText

/**
 * 搜索结果条目的位置索引：配置键 → 标题路径（本页分区 / 子页面路径）。
 *
 * 索引由组件页面（`HookOptionsPage`）在组合时登记，搜索界面只读它来渲染结果摘要，
 * 这样搜索可以跨页面复用，同时摘要仍能显示「功能所在分区 / 子页面」。
 */
object AppSearchIndex {

    private val labels = mutableStateMapOf<String, List<Int>>()

    /** 批量登记（幂等）：同一个键重复登记会覆盖为最新路径。 */
    fun putAll(mapping: Map<String, List<Int>>) {
        if (mapping.isNotEmpty()) labels.putAll(mapping)
    }

    /** 返回该配置项的标题路径（字符串资源 id 列表），未登记时返回 null。 */
    fun labelFor(key: String): List<Int>? = labels[key]
}

/**
 * 全局搜索结果列表。
 *
 * 结果来自 [OptionRegistry]（功能页在启动时注册了全部配置项，子页面内的功能同样在内），
 * 摘要显示该项所在的分区 / 子页面路径（见 [AppSearchIndex]）。
 *
 * @param query 搜索关键词。
 * @param onPick 点击结果时回调，参数为配置键。
 */
@Composable
fun AppSearchResults(
    query: String,
    onPick: (String) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    searchTopPadding: () -> Dp = { 0.dp },
) {
    val context = LocalContext.current
    val results = remember(query) { OptionRegistry.search(context, query) }
    if (query.isBlank()) return
    val resultsTop = appTopBarSafeInsets().asPaddingValues().calculateTopPadding() +
        GlassSearchOverlayDefaults.BarHeight + GlassSearchOverlayDefaults.VerticalPadding * 2
    val hasResults = results.isNotEmpty()
    val padding = remember(resultsTop, searchTopPadding, hasResults) {
        val horizontal = if (hasResults) 12.dp else 28.dp
        val gap = if (hasResults) 4.dp else 12.dp
        val base = PaddingValues(horizontal = horizontal, vertical = 24.dp)
        object : PaddingValues by base {
            override fun calculateTopPadding() = resultsTop + gap + searchTopPadding()
        }
    }
    if (!hasResults) {
        MiuixText(
            text = stringResource(R.string.search_empty),
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            style = MiuixTheme.textStyles.footnote2,
            modifier = modifier.padding(padding),
        )
        return
    }
    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxWidth(),
        contentPadding = padding,
    ) {
        item(key = "results") {
            Card {
                results.forEach { spec ->
                    SearchResultRow(spec = spec, onPick = onPick)
                }
            }
        }
    }
}

@Composable
private fun SearchResultRow(spec: OptionSpec, onPick: (String) -> Unit) {
    val summary = AppSearchIndex.labelFor(spec.key)
        ?.map { stringResource(it) }
        ?.joinToString(" / ")
        ?: spec.summaryRes.takeIf { it != 0 }?.let { stringResource(it) }
    BasicComponent(
        title = stringResource(spec.titleRes),
        summary = summary,
        modifier = Modifier.fillMaxWidth(),
        onClick = { onPick(spec.key) },
    )
}
