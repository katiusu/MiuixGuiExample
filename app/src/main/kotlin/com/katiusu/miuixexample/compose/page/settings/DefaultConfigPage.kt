package com.katiusu.miuixexample.compose.page.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.katiusu.miuixexample.R
import com.katiusu.miuixexample.compose.component.ColorPaletteDialog
import com.katiusu.miuixexample.compose.component.PageDropdownPreference
import com.katiusu.miuixexample.compose.component.DetailPage
import com.katiusu.miuixexample.compose.component.SectionTitle
import com.katiusu.miuixexample.compose.component.parseHexColor
import com.katiusu.miuixexample.compose.component.toArgbHex
import com.katiusu.miuixexample.compose.data.DefaultConfigSettings
import com.katiusu.miuixexample.compose.data.FlutterPrefsRepository
import com.katiusu.miuixexample.compose.data.channel.STATUS_BAR_ICON_HIDE
import com.katiusu.miuixexample.compose.data.channel.STATUS_BAR_ICON_SHOW
import com.katiusu.miuixexample.compose.data.channel.STATUS_BAR_ICON_SYSTEM
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.preference.ArrowPreference
import top.yukonga.miuix.kmp.preference.SwitchPreference
import top.yukonga.miuix.kmp.window.WindowDialog

@Composable
internal fun DefaultConfigPage(
    prefs: FlutterPrefsRepository,
    onBack: () -> Unit,
) {
    var settings by remember { mutableStateOf(prefs.defaultConfigSettings()) }
    var timeoutText by remember { mutableStateOf(settings.timeout.toString()) }
    var showTimeoutDialog by remember { mutableStateOf(false) }
    var activeColorTarget by remember { mutableStateOf<DefaultColorTarget?>(null) }
    var selectedColor by remember { mutableStateOf(Color.Red) }
    val glowModes = remember { listOf(GLOW_ON, GLOW_OFF, GLOW_FOLLOW_DYNAMIC) }
    val glowModeLabels = listOf(
        stringResource(R.string.enabled_option),
        stringResource(R.string.disabled_option),
        stringResource(R.string.follow_dynamic_color),
    )
    val marqueeAutoHideValues = remember { listOf("off", "1", "2", "1_override", "2_override") }
    val marqueeAutoHideLabels = listOf(
        stringResource(R.string.disabled_option),
        stringResource(R.string.marquee_once),
        stringResource(R.string.marquee_twice),
        stringResource(R.string.marquee_once_override),
        stringResource(R.string.marquee_twice_override),
    )
    val statusBarIconValues = remember {
        listOf(STATUS_BAR_ICON_SHOW, STATUS_BAR_ICON_HIDE, STATUS_BAR_ICON_SYSTEM)
    }
    val statusBarIconLabels = listOf(
        stringResource(R.string.enabled_option),
        stringResource(R.string.disabled_option),
        stringResource(R.string.status_bar_icon_system),
    )
    val statusBarIconSummary = stringResource(
        when (settings.statusBarIcon) {
            STATUS_BAR_ICON_SHOW -> R.string.status_bar_icon_subtitle_show
            STATUS_BAR_ICON_HIDE -> R.string.status_bar_icon_subtitle_hide
            else -> R.string.status_bar_icon_subtitle_system
        },
    )

    fun update(value: DefaultConfigSettings) {
        settings = value
        prefs.setDefaultConfigSettings(value)
    }

    DetailPage(
        title = stringResource(R.string.default_config),
        onBack = onBack,
    ) {
        item {
            SectionTitle(stringResource(R.string.behavior))
            Card {
                SwitchPreference(
                    checked = settings.firstFloat,
                    onCheckedChange = { update(settings.copy(firstFloat = it)) },
                    title = stringResource(R.string.first_float),
                    summary = stringResource(R.string.first_float_summary),
                    insideMargin = DEFAULT_ITEM_MARGIN,
                )
                SwitchPreference(
                    checked = settings.aodText,
                    onCheckedChange = { update(settings.copy(aodText = it)) },
                    title = stringResource(R.string.aod_text),
                    summary = stringResource(R.string.aod_text_summary),
                    insideMargin = DEFAULT_ITEM_MARGIN,
                )
                SwitchPreference(
                    checked = settings.enableFloat,
                    onCheckedChange = { update(settings.copy(enableFloat = it)) },
                    title = stringResource(R.string.update_float),
                    summary = stringResource(R.string.update_float_summary),
                    insideMargin = DEFAULT_ITEM_MARGIN,
                )
                SwitchPreference(
                    checked = settings.focusNotification,
                    onCheckedChange = { update(settings.copy(focusNotification = it)) },
                    title = stringResource(R.string.focus_notification),
                    summary = stringResource(R.string.focus_notification_summary),
                    insideMargin = DEFAULT_ITEM_MARGIN,
                )
                SwitchPreference(
                    checked = settings.suppressHeadsUp,
                    onCheckedChange = { update(settings.copy(suppressHeadsUp = it)) },
                    title = stringResource(R.string.suppress_heads_up_notification),
                    summary = stringResource(R.string.suppress_heads_up_notification_summary),
                    insideMargin = DEFAULT_ITEM_MARGIN,
                )
                SwitchPreference(
                    checked = settings.restoreLockscreen,
                    onCheckedChange = { update(settings.copy(restoreLockscreen = it)) },
                    title = stringResource(R.string.restore_lockscreen),
                    summary = stringResource(R.string.restore_lockscreen_summary),
                    insideMargin = DEFAULT_ITEM_MARGIN,
                )
                ArrowPreference(
                    title = stringResource(R.string.auto_disappear),
                    summary = stringResource(R.string.timeout_seconds_value, settings.timeout),
                    insideMargin = DEFAULT_ITEM_MARGIN,
                    onClick = {
                        timeoutText = settings.timeout.toString()
                        showTimeoutDialog = true
                    },
                )
            }
        }
        item {
            SectionTitle(stringResource(R.string.appearance))
            Card {
                SwitchPreference(
                    checked = settings.marquee,
                    onCheckedChange = { update(settings.copy(marquee = it)) },
                    title = stringResource(R.string.marquee_channel),
                    summary = stringResource(R.string.marquee_channel_summary),
                    insideMargin = DEFAULT_ITEM_MARGIN,
                )
                PageDropdownPreference(
                    items = marqueeAutoHideLabels,
                    selectedIndex = marqueeAutoHideValues.indexOf(settings.marqueeAutoHide).coerceAtLeast(0),
                    title = stringResource(R.string.marquee_auto_hide),
                    summary = stringResource(R.string.marquee_auto_hide_summary),
                    enabled = settings.marquee,
                    insideMargin = DEFAULT_ITEM_MARGIN,
                    onSelectedIndexChange = { index ->
                        update(settings.copy(marqueeAutoHide = marqueeAutoHideValues[index]))
                    },
                )
                SwitchPreference(
                    checked = settings.dynamicHighlightColor,
                    onCheckedChange = { update(settings.copy(dynamicHighlightColor = it)) },
                    title = stringResource(R.string.dynamic_highlight_color),
                    summary = stringResource(R.string.dynamic_highlight_color_summary),
                    insideMargin = DEFAULT_ITEM_MARGIN,
                )
                SwitchPreference(
                    checked = settings.showCardIcon,
                    onCheckedChange = { update(settings.copy(showCardIcon = it)) },
                    title = stringResource(R.string.card_icon),
                    summary = stringResource(R.string.card_icon_summary),
                    insideMargin = DEFAULT_ITEM_MARGIN,
                )
                PageDropdownPreference(
                    items = statusBarIconLabels,
                    selectedIndex = statusBarIconValues.indexOf(settings.statusBarIcon).coerceAtLeast(0),
                    title = stringResource(R.string.preserve_small_icon),
                    summary = statusBarIconSummary,
                    insideMargin = DEFAULT_ITEM_MARGIN,
                    onSelectedIndexChange = { index ->
                        update(settings.copy(statusBarIcon = statusBarIconValues[index]))
                    },
                )
            }
        }
        item {
            SectionTitle(stringResource(R.string.glow))
            Card {
                PageDropdownPreference(
                    items = glowModeLabels,
                    selectedIndex = glowModes.indexOf(settings.outerGlow).coerceAtLeast(0),
                    title = stringResource(R.string.focus_outer_glow),
                    insideMargin = DEFAULT_ITEM_MARGIN,
                    onSelectedIndexChange = { index ->
                        val mode = glowModes[index]
                        update(
                            settings.copy(
                                outerGlow = mode,
                                forceOuterGlow = settings.forceOuterGlow && mode != GLOW_OFF,
                            ),
                        )
                    },
                )
                AnimatedVisibility(visible = settings.outerGlow != GLOW_OFF) {
                    SwitchPreference(
                        checked = settings.forceOuterGlow,
                        onCheckedChange = { update(settings.copy(forceOuterGlow = it)) },
                        title = stringResource(R.string.force_outer_glow),
                        summary = stringResource(R.string.force_focus_outer_glow_summary),
                        insideMargin = DEFAULT_ITEM_MARGIN,
                    )
                }
                AnimatedVisibility(visible = settings.outerGlow == GLOW_ON) {
                    ArrowPreference(
                        title = stringResource(R.string.out_effect_color),
                        summary = settings.outEffectColor.takeIf(String::isNotEmpty),
                        insideMargin = DEFAULT_ITEM_MARGIN,
                        onClick = {
                            selectedColor = parseHexColor(settings.outEffectColor)
                            activeColorTarget = DefaultColorTarget.Focus
                        },
                    )
                }
                PageDropdownPreference(
                    items = glowModeLabels,
                    selectedIndex = glowModes.indexOf(settings.cardOuterGlow).coerceAtLeast(0),
                    title = stringResource(R.string.card_outer_glow),
                    insideMargin = DEFAULT_ITEM_MARGIN,
                    onSelectedIndexChange = { index ->
                        val mode = glowModes[index]
                        update(
                            settings.copy(
                                cardOuterGlow = mode,
                                forceCardOuterGlow = settings.forceCardOuterGlow && mode != GLOW_OFF,
                            ),
                        )
                    },
                )
                AnimatedVisibility(visible = settings.cardOuterGlow != GLOW_OFF) {
                    SwitchPreference(
                        checked = settings.forceCardOuterGlow,
                        onCheckedChange = { update(settings.copy(forceCardOuterGlow = it)) },
                        title = stringResource(R.string.force_outer_glow),
                        summary = stringResource(R.string.force_card_outer_glow_summary),
                        insideMargin = DEFAULT_ITEM_MARGIN,
                    )
                }
                AnimatedVisibility(visible = settings.cardOuterGlow == GLOW_ON) {
                    ArrowPreference(
                        title = stringResource(R.string.out_effect_color),
                        summary = settings.cardOuterGlowColor.takeIf(String::isNotEmpty),
                        insideMargin = DEFAULT_ITEM_MARGIN,
                        onClick = {
                            selectedColor = parseHexColor(settings.cardOuterGlowColor)
                            activeColorTarget = DefaultColorTarget.Card
                        },
                    )
                }
            }
        }
    }

    WindowDialog(
        show = showTimeoutDialog,
        title = stringResource(R.string.auto_disappear),
        onDismissRequest = { showTimeoutDialog = false },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            TextField(
                value = timeoutText,
                onValueChange = { raw -> timeoutText = raw.filter(Char::isDigit).take(9) },
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(R.string.seconds),
                useLabelAsPlaceholder = true,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                TextButton(
                    text = stringResource(R.string.cancel),
                    onClick = { showTimeoutDialog = false },
                    modifier = Modifier.weight(1f),
                )
                Button(
                    onClick = {
                        val timeout = timeoutText.toIntOrNull()?.takeIf { it >= 1 }
                            ?: settings.timeout
                        timeoutText = timeout.toString()
                        update(settings.copy(timeout = timeout))
                        showTimeoutDialog = false
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColorsPrimary(),
                ) { Text(stringResource(R.string.save)) }
            }
        }
    }

    ColorPaletteDialog(
        show = activeColorTarget != null,
        title = stringResource(R.string.out_effect_color),
        initialColor = selectedColor,
        onDismiss = { activeColorTarget = null },
        onDelete = {
            when (activeColorTarget) {
                DefaultColorTarget.Focus -> update(settings.copy(outEffectColor = ""))
                DefaultColorTarget.Card -> update(settings.copy(cardOuterGlowColor = ""))
                null -> Unit
            }
            activeColorTarget = null
        },
        onSave = { color ->
            when (activeColorTarget) {
                DefaultColorTarget.Focus -> update(settings.copy(outEffectColor = color.toArgbHex()))
                DefaultColorTarget.Card -> update(settings.copy(cardOuterGlowColor = color.toArgbHex()))
                null -> Unit
            }
            activeColorTarget = null
        },
    )
}

private enum class DefaultColorTarget { Focus, Card }

private val DEFAULT_ITEM_MARGIN = PaddingValues(horizontal = 18.dp, vertical = 14.dp)
private const val GLOW_ON = "on"
private const val GLOW_OFF = "off"
private const val GLOW_FOLLOW_DYNAMIC = "follow_dynamic"
