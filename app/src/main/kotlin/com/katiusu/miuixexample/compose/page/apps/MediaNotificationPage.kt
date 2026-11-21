package com.katiusu.miuixexample.compose.page.apps

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.katiusu.miuixexample.R
import com.katiusu.miuixexample.compose.component.DetailPage
import com.katiusu.miuixexample.compose.component.PageDropdownPreference
import com.katiusu.miuixexample.compose.component.ColorPaletteDialog
import com.katiusu.miuixexample.compose.component.SectionTitle
import com.katiusu.miuixexample.compose.component.parseHexColor
import com.katiusu.miuixexample.compose.component.toArgbHex
import com.katiusu.miuixexample.compose.data.FlutterPrefsRepository
import com.katiusu.miuixexample.compose.data.InstalledApp
import com.katiusu.miuixexample.compose.data.MediaNotificationSettings
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Refresh
import top.yukonga.miuix.kmp.preference.ArrowPreference
import top.yukonga.miuix.kmp.preference.SwitchPreference

@Composable
internal fun MediaNotificationPage(
    app: InstalledApp,
    prefs: FlutterPrefsRepository,
    onBack: () -> Unit,
) {
    var draft by remember(app.packageName) {
        mutableStateOf(prefs.mediaNotificationSettings(app.packageName))
    }
    var activeColorTarget by remember { mutableStateOf<ColorTarget?>(null) }
    var selectedColor by remember { mutableStateOf(Color.Red) }
    val defaultOuterGlow = prefs.getString(PREF_DEFAULT_OUTER_GLOW, TRI_STATE_OFF)
    val defaultCardOuterGlow = prefs.getString(PREF_DEFAULT_CARD_OUTER_GLOW, TRI_STATE_OFF)
    val modes = remember { listOf(TRI_STATE_DEFAULT, TRI_STATE_ON, TRI_STATE_OFF, TRI_STATE_FOLLOW_DYNAMIC) }
    val outerGlowLabels = glowModeLabels(defaultOuterGlow)
    val cardOuterGlowLabels = glowModeLabels(defaultCardOuterGlow)

    fun updateSettings(value: MediaNotificationSettings) {
        draft = value
        prefs.setMediaNotificationSettings(app.packageName, value)
    }

    DetailPage(
        title = stringResource(R.string.media_notification),
        onBack = onBack,
        actionIcon = MiuixIcons.Refresh,
        actionDescription = stringResource(R.string.restore_default),
        onAction = { updateSettings(MediaNotificationSettings()) },
    ) {
        item {
            SectionTitle(stringResource(R.string.media_notification))
            Card {
                SwitchPreference(
                    checked = draft.enabled,
                    onCheckedChange = { updateSettings(draft.copy(enabled = it)) },
                    title = stringResource(R.string.media_notification),
                    summary = stringResource(R.string.media_notification_disabled_summary),
                    insideMargin = MEDIA_ITEM_MARGIN,
                )
                SwitchPreference(
                    checked = draft.showCard,
                    onCheckedChange = {
                        updateSettings(
                            draft.copy(showCard = it, normalNotification = if (it) false else draft.normalNotification),
                        )
                    },
                    title = stringResource(R.string.media_show_card),
                    summary = stringResource(R.string.media_show_card_summary),
                    enabled = draft.enabled,
                    insideMargin = MEDIA_ITEM_MARGIN,
                )
                SwitchPreference(
                    checked = draft.normalNotification,
                    onCheckedChange = {
                        updateSettings(
                            draft.copy(normalNotification = it, showCard = if (it) false else draft.showCard),
                        )
                    },
                    title = stringResource(R.string.normal_notification),
                    summary = stringResource(R.string.normal_notification_summary),
                    enabled = draft.enabled,
                    insideMargin = MEDIA_ITEM_MARGIN,
                )
            }
        }
        item {
            SectionTitle(
                stringResource(R.string.glow_section, stringResource(R.string.focus_notification)),
            )
            Card {
                PageDropdownPreference(
                    items = outerGlowLabels,
                    selectedIndex = modes.indexOf(draft.outerGlow).coerceAtLeast(0),
                    title = stringResource(R.string.outer_glow),
                    onSelectedIndexChange = { index -> updateSettings(draft.copy(outerGlow = modes[index])) },
                    enabled = draft.enabled,
                    insideMargin = MEDIA_ITEM_MARGIN,
                )
                AnimatedVisibility(visible = draft.enabled && draft.outerGlow == TRI_STATE_ON) {
                    ArrowPreference(
                        title = stringResource(R.string.out_effect_color),
                        summary = draft.outEffectColor.takeIf(String::isNotEmpty),
                        insideMargin = MEDIA_ITEM_MARGIN,
                        onClick = {
                            selectedColor = parseHexColor(draft.outEffectColor)
                            activeColorTarget = ColorTarget.Focus
                        },
                    )
                }
            }
        }
        item {
            SectionTitle(
                stringResource(R.string.glow_section, stringResource(R.string.enable_card)),
            )
            Card {
                PageDropdownPreference(
                    items = cardOuterGlowLabels,
                    selectedIndex = modes.indexOf(draft.cardOuterGlow).coerceAtLeast(0),
                    title = stringResource(R.string.outer_glow),
                    onSelectedIndexChange = { index ->
                        updateSettings(draft.copy(cardOuterGlow = modes[index]))
                    },
                    enabled = draft.enabled,
                    insideMargin = MEDIA_ITEM_MARGIN,
                )
                AnimatedVisibility(visible = draft.enabled && draft.cardOuterGlow == TRI_STATE_ON) {
                    ArrowPreference(
                        title = stringResource(R.string.out_effect_color),
                        summary = draft.cardOuterGlowColor.takeIf(String::isNotEmpty),
                        insideMargin = MEDIA_ITEM_MARGIN,
                        onClick = {
                            selectedColor = parseHexColor(draft.cardOuterGlowColor)
                            activeColorTarget = ColorTarget.Card
                        },
                    )
                }
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
                ColorTarget.Focus -> updateSettings(draft.copy(outEffectColor = ""))
                ColorTarget.Card -> updateSettings(draft.copy(cardOuterGlowColor = ""))
                null -> Unit
            }
            activeColorTarget = null
        },
        onSave = { color ->
            when (activeColorTarget) {
                ColorTarget.Focus -> updateSettings(draft.copy(outEffectColor = color.toArgbHex()))
                ColorTarget.Card -> updateSettings(draft.copy(cardOuterGlowColor = color.toArgbHex()))
                null -> Unit
            }
            activeColorTarget = null
        },
    )
}

@Composable
private fun glowModeLabels(defaultMode: String): List<String> = listOf(
    stringResource(
        if (defaultMode == TRI_STATE_ON) R.string.default_on else R.string.default_off,
    ),
    stringResource(R.string.enabled_option),
    stringResource(R.string.disabled_option),
    stringResource(R.string.follow_dynamic_color),
)

private val MEDIA_ITEM_MARGIN = PaddingValues(horizontal = 18.dp, vertical = 14.dp)
private const val PREF_DEFAULT_OUTER_GLOW = "pref_default_outer_glow"
private const val PREF_DEFAULT_CARD_OUTER_GLOW = "pref_default_card_outer_glow"
private const val TRI_STATE_DEFAULT = "default"
private const val TRI_STATE_ON = "on"
private const val TRI_STATE_OFF = "off"
private const val TRI_STATE_FOLLOW_DYNAMIC = "follow_dynamic"

private enum class ColorTarget { Focus, Card }
