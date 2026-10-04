package com.katiusu.miuixgui.example.ui.screen.features

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.katiusu.miuixgui.example.R
import com.katiusu.miuixgui.example.prefs.OptionSpec
import com.katiusu.miuixgui.example.prefs.OptionType
import com.katiusu.miuixgui.example.ui.component.pref.HookOptionsPage
import com.katiusu.miuixgui.example.ui.component.pref.HookSection
import com.katiusu.miuixgui.example.ui.component.pref.HookSubPage

/**
 * 功能页：模块全部功能入口（模板以各组件类型作为示例功能）。
 *
 * 页面布局由通用组件 [HookOptionsPage] 提供；子页面内的功能通过 [HookSubPage]
 * 并入本页搜索，命中后打开对应子页面。
 *
 * > 实际开发时应把本页替换为真实功能，小标题使用单语言（默认中文），不要再传
 * > `HookSection.titleEn`——该参数仅用于模板示例展示 API 英文组件名。
 */
@Composable
fun FeaturesPageView(
    isBlurEnabled: Boolean = true,
    extraBottomPadding: Dp = 0.dp,
) {
    val context = LocalContext.current
    val specs = remember { featureSpecs() }
    val sections = remember(specs) { featureSections(specs) }
    val subPages = remember(specs) {
        listOf(
            HookSubPage(
                titleRes = R.string.example_arrow_sub_title,
                specs = listOf(specByKey(specs, "example_sub_text")),
                onOpen = { context.startActivity(Intent(context, FeatureSubPageActivity::class.java)) },
            ),
        )
    }

    HookOptionsPage(
        title = stringResource(R.string.tab_features),
        sections = sections,
        subPages = subPages,
        isBlurEnabled = isBlurEnabled,
        extraBottomPadding = extraBottomPadding,
        onArrowClick = {
            context.startActivity(Intent(context, FeatureSubPageActivity::class.java))
        },
    )
}

private fun specByKey(specs: List<OptionSpec>, key: String): OptionSpec =
    specs.first { it.key == key }

private fun featureSections(specs: List<OptionSpec>): List<HookSection> = listOf(
    HookSection(
        titleRes = R.string.example_section_switch,
        specs = listOf(specByKey(specs, "example_switch")),
    ),
    HookSection(
        titleRes = R.string.example_section_checkbox,
        specs = listOf(specByKey(specs, "example_checkbox")),
    ),
    HookSection(
        titleRes = R.string.example_section_arrow,
        specs = listOf(specByKey(specs, "example_arrow")),
    ),
    HookSection(
        titleRes = R.string.example_section_dropdown,
        specs = listOf(specByKey(specs, "example_dropdown")),
    ),
    HookSection(
        titleRes = R.string.example_section_radio,
        specs = listOf(specByKey(specs, "example_radio")),
    ),
    HookSection(
        titleRes = R.string.example_section_slider,
        specs = listOf(specByKey(specs, "example_slider")),
    ),
    HookSection(
        titleRes = R.string.example_section_text,
        specs = listOf(specByKey(specs, "example_text")),
    ),
    HookSection(
        titleRes = R.string.example_section_package_list,
        specs = listOf(specByKey(specs, "example_package_list")),
    ),
)

/** 功能页的全部配置项（App 启动时注册，供全局搜索与作用域申请使用）。 */
internal fun featureSpecs(): List<OptionSpec> = listOf(
    OptionSpec(
        key = "example_switch",
        type = OptionType.SWITCH,
        titleRes = R.string.example_switch_title,
        summaryRes = R.string.example_switch_summary,
        defaultBoolean = false,
        targetPackages = listOf("com.example.target"),
        // 演示 Hook 生效状态：目标进程安装同名 key 的 hook 并回报后显示「已生效」。
        showStatus = true,
    ),
    OptionSpec(
        key = "example_checkbox",
        type = OptionType.CHECKBOX,
        titleRes = R.string.example_checkbox_title,
        summaryRes = R.string.example_checkbox_summary,
        defaultBoolean = false,
        dependsOn = "example_switch",
        targetPackages = listOf("com.example.target"),
    ),
    OptionSpec(
        key = "example_arrow",
        type = OptionType.ARROW,
        titleRes = R.string.example_arrow_title,
        summaryRes = R.string.example_arrow_summary,
    ),
    OptionSpec(
        key = "example_sub_text",
        type = OptionType.TEXT,
        titleRes = R.string.example_sub_text_title,
        summaryRes = R.string.example_sub_text_summary,
        defaultString = "",
        targetPackages = listOf("com.example.target"),
    ),
    OptionSpec(
        key = "example_dropdown",
        type = OptionType.DROPDOWN,
        titleRes = R.string.example_dropdown_title,
        summaryRes = R.string.example_dropdown_summary,
        defaultString = "default",
        entryResIds = listOf(
            R.string.example_dropdown_default,
            R.string.example_dropdown_slow,
            R.string.example_dropdown_fast,
        ),
        entryValues = listOf("default", "slow", "fast"),
        targetPackages = listOf("com.example.target"),
    ),
    OptionSpec(
        key = "example_radio",
        type = OptionType.RADIO,
        titleRes = R.string.example_radio_title,
        summaryRes = R.string.example_radio_summary,
        defaultString = "home",
        entryResIds = listOf(
            R.string.example_radio_home,
            R.string.example_radio_discover,
            R.string.example_radio_mine,
        ),
        entryValues = listOf("home", "discover", "mine"),
        targetPackages = listOf("com.example.target"),
    ),
    OptionSpec(
        key = "example_slider",
        type = OptionType.SLIDER,
        titleRes = R.string.example_slider_title,
        summaryRes = R.string.example_slider_summary,
        defaultFloat = 100f,
        masterKey = "example_slider_enable",
        sliderMin = 50f,
        sliderMax = 200f,
        sliderStep = 1f,
        sliderDecimals = 0,
        sliderUnitRes = R.string.example_slider_unit,
        sliderValueLabelRes = R.string.example_slider_value_label,
        targetPackages = listOf("com.example.target"),
    ),
    OptionSpec(
        key = "example_slider_enable",
        type = OptionType.SWITCH,
        titleRes = R.string.example_slider_title,
        summaryRes = R.string.example_slider_summary,
        defaultBoolean = true,
        targetPackages = listOf("com.example.target"),
    ),
    OptionSpec(
        key = "example_text",
        type = OptionType.TEXT,
        titleRes = R.string.example_text_title,
        summaryRes = R.string.example_text_summary,
        defaultString = "",
        targetPackages = listOf("com.example.target"),
    ),
    OptionSpec(
        key = "example_package_list",
        type = OptionType.PACKAGE_LIST,
        titleRes = R.string.example_package_list_title,
        summaryRes = R.string.example_package_list_summary,
        defaultString = "",
    ),
)
