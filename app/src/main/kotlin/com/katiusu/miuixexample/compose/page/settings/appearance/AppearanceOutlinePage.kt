package com.katiusu.miuixexample.compose.page.settings.appearance

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.katiusu.miuixexample.R
import com.katiusu.miuixexample.compose.component.ColorPaletteDialog
import com.katiusu.miuixexample.compose.component.PreferenceSwitch
import com.katiusu.miuixexample.compose.component.PreferenceDropdown
import com.katiusu.miuixexample.compose.component.SectionTitle
import com.katiusu.miuixexample.compose.component.SettingsAction
import com.katiusu.miuixexample.compose.component.parseHexColor
import com.katiusu.miuixexample.compose.component.toArgbHex
import com.katiusu.miuixexample.compose.data.FlutterPrefsRepository
import com.katiusu.miuixexample.compose.data.rememberBooleanPreference
import com.katiusu.miuixexample.compose.data.rememberStringPreference
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.basic.ArrowRight

@Composable
internal fun AppearanceOutlinePage(prefs: FlutterPrefsRepository, onBack: () -> Unit) {
    val outlineValues = listOf("system", "show", "hide")
    val outlineLabels = listOf(stringResource(R.string.outline_follow_system),
        stringResource(R.string.outline_force_show), stringResource(R.string.outline_force_hide))
    val cardOutline = rememberStringPreference(prefs, KEY_CARD_OUTLINE_MODE, "system")
    val focusOutline = rememberStringPreference(prefs, KEY_FOCUS_OUTLINE_MODE, "system")
    val bigMaterial = rememberStringPreference(prefs, KEY_MATERIAL_BIG, "")
    val expandMaterial = rememberStringPreference(prefs, KEY_MATERIAL_EXPAND, "")
    val expandFollowBig = rememberBooleanPreference(prefs, KEY_MATERIAL_EXPAND_FOLLOW, true)
    val cardOutlineEnabled = !usesSoftGlass(bigMaterial.value)
    val focusOutlineEnabled = !usesSoftGlass(
        if (expandFollowBig.value) bigMaterial.value else expandMaterial.value,
    )
    val glowSingleColor = rememberBooleanPreference(prefs, KEY_GLOW_SINGLE_COLOR, false)
    val glowBaseColor = rememberStringPreference(prefs, KEY_GLOW_BASE_COLOR, "")
    var colorDialog by remember { mutableStateOf(false) }

    AppearanceDetailPage(title = stringResource(R.string.appearance_outline), onBack = onBack) {
        item {
            SectionTitle(stringResource(R.string.outline_control))
            Card(modifier = Modifier.fillMaxWidth()) {
                PreferenceDropdown(stringResource(R.string.card_outline), null, null,
                    outlineLabels, outlineValues.indexOf(cardOutline.value).coerceAtLeast(0),
                    enabled = cardOutlineEnabled) {
                    cardOutline.value = outlineValues[it]
                    prefs.putString(KEY_CARD_OUTLINE_MODE, outlineValues[it])
                }
                PreferenceDropdown(stringResource(R.string.focus_outline), null, null,
                    outlineLabels, outlineValues.indexOf(focusOutline.value).coerceAtLeast(0),
                    enabled = focusOutlineEnabled) {
                    focusOutline.value = outlineValues[it]
                    prefs.putString(KEY_FOCUS_OUTLINE_MODE, outlineValues[it])
                }
            }
        }
        item {
            SectionTitle(stringResource(R.string.outer_glow))
            Card(modifier = Modifier.fillMaxWidth()) {
                LongPreferenceSlider(prefs, KEY_GLOW_RANGE, R.string.glow_range, 0, 100, 0, unit = SliderUnit.Percent)
                PreferenceSwitch(stringResource(R.string.single_color_glow), null, null, glowSingleColor.value) {
                    glowSingleColor.value = it; prefs.putBoolean(KEY_GLOW_SINGLE_COLOR, it)
                }
                AnimatedVisibility(visible = !glowSingleColor.value) {
                    SettingsAction(
                        title = stringResource(R.string.glow_base_color),
                        summary = glowBaseColor.value.ifBlank { stringResource(R.string.default_option) },
                        endIcon = MiuixIcons.Basic.ArrowRight,
                    ) { colorDialog = true }
                }
            }
        }
    }

    ColorPaletteDialog(
        show = colorDialog,
        title = stringResource(R.string.glow_base_color),
        initialColor = parseHexColor(glowBaseColor.value, Color(0xFF0096FF)),
        onDismiss = { colorDialog = false },
        onDelete = { glowBaseColor.value = ""; prefs.remove(KEY_GLOW_BASE_COLOR); colorDialog = false },
    ) { color ->
        val value = color.toArgbHex()
        glowBaseColor.value = value
        prefs.putString(KEY_GLOW_BASE_COLOR, value)
        colorDialog = false
    }
}
