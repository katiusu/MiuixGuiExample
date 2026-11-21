package com.katiusu.islandexample.compose.page

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.katiusu.islandexample.AppLocaleController
import com.katiusu.islandexample.R
import com.katiusu.islandexample.compose.component.CollapsingPage
import com.katiusu.islandexample.compose.component.PreferenceDropdown
import com.katiusu.islandexample.compose.component.SectionTitle
import com.katiusu.islandexample.compose.component.SettingsActionWithArrow
import com.katiusu.islandexample.compose.data.FlutterPrefsRepository
import com.katiusu.islandexample.compose.page.settings.PresetEntryCard
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Blocklist
import top.yukonga.miuix.kmp.icon.extended.Hide
import top.yukonga.miuix.kmp.icon.extended.Image
import top.yukonga.miuix.kmp.icon.extended.Community
import top.yukonga.miuix.kmp.icon.extended.More
import top.yukonga.miuix.kmp.icon.extended.Pin
import top.yukonga.miuix.kmp.icon.extended.Settings
import top.yukonga.miuix.kmp.icon.extended.GridView
import top.yukonga.miuix.kmp.icon.extended.Theme
import top.yukonga.miuix.kmp.icon.extended.Translate
import top.yukonga.miuix.kmp.icon.extended.Tune

internal enum class SettingsDetail {
    Appearance,
    Theme,
    HideBehavior,
    DefaultConfig,
    AiConfig,
    Misc,
    Other,
    References,
    BackupRestore,
    FilterRules,
    KeepIsland,
    Onboarding,
    Preset,
    /** 更新日志 & 历史版本（站点 JSON，原生渲染）。 */
    Changelog,
    /** 捐赠名单（站点 JSON，原生渲染）。 */
    Donors,
}

@Composable
internal fun SettingsPage(
    prefs: FlutterPrefsRepository,
    onOpenDetail: (SettingsDetail) -> Unit,
) {
    val context = LocalContext.current
    val localeValues = listOf("", "zh", "en", "ja", "ru", "tr", "ar", "pt-BR")
    val currentLocale = AppLocaleController.currentLanguageTag(context)
    CollapsingPage(
        title = stringResource(R.string.nav_settings),
    ) {
        item {
            PresetEntryCard(
                text = stringResource(R.string.preset_entry_hint),
                onClick = { onOpenDetail(SettingsDetail.Preset) },
            )
        }
        item {
            SectionTitle(stringResource(R.string.island))
            Card(modifier = Modifier.fillMaxWidth()) {
                SettingsActionWithArrow(stringResource(R.string.appearance), MiuixIcons.Image) {
                    onOpenDetail(SettingsDetail.Appearance)
                }
                SettingsActionWithArrow(stringResource(R.string.ai_summary), MiuixIcons.Community) {
                    onOpenDetail(SettingsDetail.AiConfig)
                }
                SettingsActionWithArrow(stringResource(R.string.filter_rules), MiuixIcons.Blocklist) {
                    onOpenDetail(SettingsDetail.FilterRules)
                }
                SettingsActionWithArrow(stringResource(R.string.default_config), MiuixIcons.Tune) {
                    onOpenDetail(SettingsDetail.DefaultConfig)
                }
                SettingsActionWithArrow(stringResource(R.string.hide_behavior), MiuixIcons.Hide) {
                    onOpenDetail(SettingsDetail.HideBehavior)
                }
                SettingsActionWithArrow(stringResource(R.string.always_on_island), MiuixIcons.Pin) {
                    onOpenDetail(SettingsDetail.KeepIsland)
                }
                SettingsActionWithArrow(stringResource(R.string.other), MiuixIcons.More) {
                    onOpenDetail(SettingsDetail.Other)
                }
            }
        }
        item {
            SectionTitle(stringResource(R.string.misc))
            Card(modifier = Modifier.fillMaxWidth()) {
                SettingsActionWithArrow(stringResource(R.string.misc), MiuixIcons.Settings) {
                    onOpenDetail(SettingsDetail.Misc)
                }
            }
        }
        item {
            SectionTitle(stringResource(R.string.appearance))
            Card(modifier = Modifier.fillMaxWidth()) {
                SettingsActionWithArrow(stringResource(R.string.theme), MiuixIcons.Theme) {
                    onOpenDetail(SettingsDetail.Theme)
                }
                PreferenceDropdown(
                    title = stringResource(R.string.language),
                    summary = null,
                    icon = MiuixIcons.Translate,
                    items = listOf(
                        stringResource(R.string.follow_system),
                        stringResource(R.string.chinese),
                        stringResource(R.string.english),
                        stringResource(R.string.japanese),
                        stringResource(R.string.russian),
                        stringResource(R.string.turkish),
                        stringResource(R.string.arabic),
                        stringResource(R.string.brazilian_portuguese),
                    ),
                    selectedIndex = localeValues.indexOf(currentLocale).coerceAtLeast(0),
                ) { index ->
                    val selectedLocale = localeValues[index]
                    if (selectedLocale.isBlank()) {
                        prefs.remove(KEY_LOCALE)
                    } else {
                        prefs.putString(KEY_LOCALE, selectedLocale)
                    }
                    AppLocaleController.apply(context, selectedLocale)
                }
            }
        }
    }
}
private const val KEY_LOCALE = "pref_locale"
