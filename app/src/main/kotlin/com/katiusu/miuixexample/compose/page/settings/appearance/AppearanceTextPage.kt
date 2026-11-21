package com.katiusu.miuixexample.compose.page.settings.appearance

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.katiusu.miuixexample.R
import com.katiusu.miuixexample.compose.component.PreferenceDropdown
import com.katiusu.miuixexample.compose.component.SectionTitle
import com.katiusu.miuixexample.compose.data.FlutterPrefsRepository
import com.katiusu.miuixexample.compose.data.rememberStringPreference
import top.yukonga.miuix.kmp.basic.Card

@Composable
internal fun AppearanceTextPage(prefs: FlutterPrefsRepository, onBack: () -> Unit) {
    AppearanceDetailPage(title = stringResource(R.string.appearance_text), onBack = onBack) {
        item { SectionTitle(stringResource(R.string.card_text_section)) }
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                LongPreferenceSlider(prefs, KEY_TEXT_SCALE, R.string.card_text_size, 10, 200, 100, unit = SliderUnit.Percent)
                LongPreferenceSlider(prefs, KEY_TEXT_AREA_HEIGHT, R.string.card_text_area_height, 0, 100, 0)
                TextColorPreference(prefs, KEY_TEXT_COLOR, R.string.card_text_color, includeBackground = true)
                TextColorPreference(prefs, KEY_FOCUS_TEXT_COLOR, R.string.focus_text_color, includeBackground = false)
                TextColorPreference(prefs, KEY_MEDIA_TEXT_COLOR, R.string.media_text_color, includeBackground = false)
            }
        }
        item { SectionTitle(stringResource(R.string.card_font_section)) }
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                LongPreferenceSlider(
                    prefs, KEY_TEXT_WEIGHT_PERCENT, R.string.card_text_weight,
                    minimum = 50, maximum = 200, default = 100, unit = SliderUnit.Percent,
                )
                LongPreferenceSlider(
                    prefs, KEY_TEXT_WIDTH_PERCENT, R.string.card_text_width,
                    minimum = 30, maximum = 100, default = 100, unit = SliderUnit.Percent,
                )
                LongPreferenceSlider(
                    prefs, KEY_TIME_WIDTH_PERCENT, R.string.card_time_width,
                    minimum = 30, maximum = 100, default = 100, unit = SliderUnit.Percent,
                )
            }
        }
        item { SectionTitle(stringResource(R.string.card_text_gravity_section)) }
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                TextGravityPreference(
                    prefs, KEY_LEFT_TEXT_GRAVITY, R.string.card_left_text_gravity,
                )
                TextGravityPreference(
                    prefs, KEY_RIGHT_TEXT_GRAVITY, R.string.card_right_text_gravity,
                )
            }
        }
    }
}

@Composable
private fun TextGravityPreference(
    prefs: FlutterPrefsRepository,
    key: String,
    titleRes: Int,
) {
    val values = listOf("default", "center", "left", "right")
    val labels = listOf(
        stringResource(R.string.default_option),
        stringResource(R.string.text_align_center),
        stringResource(R.string.text_align_left),
        stringResource(R.string.text_align_right),
    )
    val state = rememberStringPreference(prefs, key, "default")
    PreferenceDropdown(
        title = stringResource(titleRes),
        summary = null,
        icon = null,
        items = labels,
        selectedIndex = values.indexOf(state.value).coerceAtLeast(0),
    ) { index ->
        val value = values[index]
        state.value = value
        if (value == "default") prefs.remove(key) else prefs.putString(key, value)
    }
}
