package com.katiusu.miuixgui.example.ui.component.pref

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.katiusu.miuixgui.example.R
import com.katiusu.miuixgui.example.prefs.ConfigState
import com.katiusu.miuixgui.example.prefs.OptionSpec
import androidx.compose.ui.state.ToggleableState
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Checkbox
import top.yukonga.miuix.kmp.basic.Switch
import top.yukonga.miuix.kmp.basic.Text as MiuixText
import com.katiusu.miuixgui.example.ui.component.AppArrowPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * 副标题：基础文案。
 *
 */
@Composable
private fun optionSummary(spec: OptionSpec): String? =
    spec.summaryRes.takeIf { it != 0 }?.let { stringResource(it) }

/**
 * 大标题 +（可选）四态状态标签。
 *
 * 标题字号 / 字重与 Miuix `BasicComponent` 默认标题一致；标签紧跟在标题文字后，
 * 标题过长时由标签保持自身宽度、标题自行换行。
 *
 * 开关 / 复选框卡片已改用 Miuix `SwitchPreference` / `CheckboxPreference`，
 * 该标题行仅供下拉、单选、滑块等仍在自绘的卡片复用。
 */
@Composable
private fun HookTitleWithStatus(
    title: String,
    enabled: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MiuixText(
            text = title,
            modifier = Modifier.weight(1f, fill = false),
            fontSize = MiuixTheme.textStyles.headline1.fontSize,
            fontWeight = FontWeight.Medium,
            color = if (enabled) {
                MiuixTheme.colorScheme.onBackground
            } else {
                MiuixTheme.colorScheme.disabledOnSecondaryVariant
            },
        )
    }
}

@Composable
private fun HookSummary(text: String?, enabled: Boolean) {
    if (text == null) return
    MiuixText(
        text = text,
        modifier = Modifier.padding(top = 2.dp),
        fontSize = MiuixTheme.textStyles.body2.fontSize,
        color = if (enabled) {
            MiuixTheme.colorScheme.onSurfaceVariantSummary
        } else {
            MiuixTheme.colorScheme.disabledOnSecondaryVariant
        },
    )
}

/** 带 switch 的卡片：关时不 hook，开时 hook。 */
@Composable
fun HookSwitchCard(
    spec: OptionSpec,
    modifier: Modifier = Modifier,
) {
    val enabled = rememberOptionEnabled(spec)
    val checked = ConfigState.bool(spec.key, spec.defaultBoolean)

    fun update(value: Boolean) {
        ConfigState.set(spec.key, value)
    }

    BasicComponent(
        modifier = modifier,
        enabled = enabled,
        onClick = { update(!checked) },
        endActions = {
            Switch(
                checked = checked,
                onCheckedChange = { update(it) },
                enabled = enabled,
            )
        },
    ) {
        HookTitleWithStatus(
            title = stringResource(spec.titleRes),
            enabled = enabled,
        )
        HookSummary(text = optionSummary(spec), enabled = enabled)
    }
}

/** 带 checkbox 的卡片。 */
@Composable
fun HookCheckboxCard(
    spec: OptionSpec,
    modifier: Modifier = Modifier,
) {
    val enabled = rememberOptionEnabled(spec)
    val checked = ConfigState.bool(spec.key, spec.defaultBoolean)

    fun update(value: Boolean) {
        ConfigState.set(spec.key, value)
    }

    BasicComponent(
        modifier = modifier,
        enabled = enabled,
        onClick = { update(!checked) },
        endActions = {
            Checkbox(
                state = ToggleableState(checked),
                onClick = { update(!checked) },
                enabled = enabled,
            )
        },
    ) {
        HookTitleWithStatus(
            title = stringResource(spec.titleRes),
            enabled = enabled,
        )
        HookSummary(text = optionSummary(spec), enabled = enabled)
    }
}

/** 带 arrow 的卡片：点击进入下级页面。 */
@Composable
fun HookArrowCard(
    spec: OptionSpec,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val enabled = rememberOptionEnabled(spec)
    AppArrowPreference(
        title = stringResource(spec.titleRes),
        summary = spec.summaryRes.takeIf { it != 0 }?.let { stringResource(it) },
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
    )
}
