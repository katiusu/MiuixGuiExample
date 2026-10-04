package com.katiusu.miuixgui.example.ui.screen.settings

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.katiusu.miuixgui.example.LocaleHelper
import com.katiusu.miuixgui.example.R
import com.katiusu.miuixgui.example.prefs.ConfigBackup
import com.katiusu.miuixgui.example.ui.util.BlurredBar
import com.katiusu.miuixgui.example.ui.util.MiuixExpandSpec
import com.katiusu.miuixgui.example.ui.util.blurSource
import com.katiusu.miuixgui.example.ui.util.pageScrollModifiers
import com.katiusu.miuixgui.example.ui.util.rememberBlurState
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.preference.ArrowPreference
import top.yukonga.miuix.kmp.preference.SwitchPreference
import top.yukonga.miuix.kmp.preference.WindowDropdownPreference
import top.yukonga.miuix.kmp.theme.ColorSchemeMode
import top.yukonga.miuix.kmp.theme.MiuixTheme
import java.io.BufferedReader
import java.io.InputStreamReader

@Composable
fun SettingsPageView(
    currentMode: ColorSchemeMode,
    onModeChange: (ColorSchemeMode) -> Unit,
    isFloatingNavbar: Boolean,
    onFloatingNavbarChange: (Boolean) -> Unit,
    isLiquidGlass: Boolean,
    onLiquidGlassChange: (Boolean) -> Unit,
    isBlurEnabled: Boolean,
    onBlurEnabledChange: (Boolean) -> Unit,
    checkUpdateOnLaunch: Boolean,
    onCheckUpdateOnLaunchChange: (Boolean) -> Unit,
    onCheckUpdate: () -> Unit,
    isCheckingUpdate: Boolean,
    extraBottomPadding: Dp = 0.dp,
) {
    val context = LocalContext.current
    val resources = LocalResources.current
    val activity = context as? Activity
    val scope = rememberCoroutineScope()
    val scrollBehavior = MiuixScrollBehavior()
    val title = stringResource(R.string.tab_settings)

    val hazeState = rememberBlurState()
    val blurActive = isBlurEnabled && hazeState != null
    val barColor = if (blurActive) Color.Transparent else MiuixTheme.colorScheme.surface

    val appVersion = remember {
        try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "1.0"
        } catch (_: Exception) { "1.0" }
    }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        uri?.let {
            try {
                val json = ConfigBackup.exportJson()
                context.contentResolver.openOutputStream(it)?.use { output ->
                    output.write(json.toByteArray())
                }
                Toast.makeText(context, resources.getString(R.string.export_success), Toast.LENGTH_SHORT).show()
            } catch (_: Exception) {
                Toast.makeText(context, resources.getString(R.string.export_failed), Toast.LENGTH_SHORT).show()
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                val reader = BufferedReader(InputStreamReader(inputStream))
                val json = reader.readText()
                reader.close()
                inputStream?.close()
                if (ConfigBackup.importJson(json)) {
                    Toast.makeText(context, resources.getString(R.string.import_success), Toast.LENGTH_SHORT).show()
                    activity?.recreate()
                } else {
                    Toast.makeText(context, resources.getString(R.string.import_failed), Toast.LENGTH_SHORT).show()
                }
            } catch (_: Exception) {
                Toast.makeText(context, resources.getString(R.string.import_failed), Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        topBar = {
            BlurredBar(hazeState, blurActive, scrollBehavior) {
                TopAppBar(
                    title = title,
                    color = barColor,
                    scrollBehavior = scrollBehavior,
                )
            }
        },
        contentWindowInsets = WindowInsets.systemBars.add(WindowInsets.displayCutout).only(WindowInsetsSides.Horizontal),
    ) { innerPadding ->
        Box {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .blurSource(if (isBlurEnabled) hazeState else null)
                    .pageScrollModifiers(
                        showTopAppBar = true,
                        topAppBarScrollBehavior = scrollBehavior,
                    ),
                contentPadding = PaddingValues(
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding() + extraBottomPadding
                )
            ) {
                item {
                    Column {
                        SmallTitle(text = stringResource(R.string.settings_interface))
                        Card(
                            modifier = Modifier.padding(horizontal = 12.dp).padding(bottom = 12.dp)
                        ) {
                            Column {
                                val modes = listOf(
                                    stringResource(R.string.theme_system),
                                    stringResource(R.string.theme_light),
                                    stringResource(R.string.theme_dark),
                                    stringResource(R.string.theme_monet_system),
                                    stringResource(R.string.theme_monet_light),
                                    stringResource(R.string.theme_monet_dark)
                                )
                                val modesEnum = listOf(
                                    ColorSchemeMode.System,
                                    ColorSchemeMode.Light,
                                    ColorSchemeMode.Dark,
                                    ColorSchemeMode.MonetSystem,
                                    ColorSchemeMode.MonetLight,
                                    ColorSchemeMode.MonetDark
                                )
                                val currentIndex = modesEnum.indexOf(currentMode).takeIf { it >= 0 } ?: 0

                                WindowDropdownPreference(
                                    title = stringResource(R.string.theme_mode),
                                    summary = modes[currentIndex],
                                    items = modes,
                                    selectedIndex = currentIndex,
                                    onSelectedIndexChange = { onModeChange(modesEnum[it]) },
                                    onExpandedChange = { }
                                )

                                SwitchPreference(
                                    title = stringResource(R.string.floating_navbar),
                                    summary = stringResource(R.string.floating_navbar_summary),
                                    checked = isFloatingNavbar,
                                    onCheckedChange = onFloatingNavbarChange
                                )

                                AnimatedVisibility(
                                    visible = isFloatingNavbar,
                                    enter = expandVertically(animationSpec = MiuixExpandSpec),
                                    exit = shrinkVertically(animationSpec = MiuixExpandSpec),
                                ) {
                                    SwitchPreference(
                                        title = stringResource(R.string.liquid_glass),
                                        summary = stringResource(R.string.liquid_glass_summary),
                                        checked = isLiquidGlass,
                                        onCheckedChange = onLiquidGlassChange
                                    )
                                }

                                SwitchPreference(
                                    title = stringResource(R.string.blur_enabled),
                                    summary = stringResource(R.string.blur_enabled_summary),
                                    checked = isBlurEnabled,
                                    onCheckedChange = onBlurEnabledChange
                                )
                            }
                        }

                        SmallTitle(text = stringResource(R.string.settings_language))
                        Card(
                            modifier = Modifier.padding(horizontal = 12.dp).padding(bottom = 12.dp)
                        ) {
                            val languageNames = listOf(
                                stringResource(R.string.language_default),
                                stringResource(R.string.language_zh_cn),
                                stringResource(R.string.language_en)
                            )
                            val languageValues = listOf(
                                LocaleHelper.Language.SYSTEM,
                                LocaleHelper.Language.ZH_CN,
                                LocaleHelper.Language.EN
                            )
                            val savedLanguage = LocaleHelper.getSavedLanguage(context)
                            val langCurrentIndex = languageValues.indexOf(savedLanguage)
                                .takeIf { it >= 0 } ?: 0

                            WindowDropdownPreference(
                                title = stringResource(R.string.settings_language),
                                summary = languageNames[langCurrentIndex],
                                items = languageNames,
                                selectedIndex = langCurrentIndex,
                                onSelectedIndexChange = {
                                    LocaleHelper.setLanguage(context, languageValues[it])
                                    activity?.recreate()
                                },
                                onExpandedChange = { }
                            )
                        }

                        SmallTitle(text = stringResource(R.string.settings_update))
                        Card(
                            modifier = Modifier.padding(horizontal = 12.dp).padding(bottom = 12.dp)
                        ) {
                            Column {
                                SwitchPreference(
                                    title = stringResource(R.string.check_update_on_launch),
                                    summary = stringResource(R.string.check_update_on_launch_summary),
                                    checked = checkUpdateOnLaunch,
                                    onCheckedChange = onCheckUpdateOnLaunchChange,
                                )

                                ArrowPreference(
                                    title = stringResource(R.string.check_update),
                                    summary = if (isCheckingUpdate) {
                                        stringResource(R.string.check_update_checking)
                                    } else {
                                        stringResource(R.string.check_update_summary, appVersion)
                                    },
                                    onClick = onCheckUpdate,
                                )
                            }
                        }

                        SmallTitle(text = stringResource(R.string.settings_data))
                        Card(
                            modifier = Modifier.padding(horizontal = 12.dp).padding(bottom = 12.dp)
                        ) {
                            Column {
                                ArrowPreference(
                                    title = stringResource(R.string.export_settings),
                                    summary = stringResource(R.string.export_settings_summary),
                                    onClick = {
                                        exportLauncher.launch("MiuixGuiTemplate_settings.json")
                                    }
                                )

                                ArrowPreference(
                                    title = stringResource(R.string.import_settings),
                                    summary = stringResource(R.string.import_settings_summary),
                                    onClick = { importLauncher.launch("application/json") }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
