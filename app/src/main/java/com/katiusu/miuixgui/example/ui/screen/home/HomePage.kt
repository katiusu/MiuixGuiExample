package com.katiusu.miuixgui.example.ui.screen.home

import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.katiusu.miuixgui.example.R
import com.katiusu.miuixgui.example.prefs.OptionRegistry
import com.katiusu.miuixgui.example.ui.component.AppArrowPreference
import com.katiusu.miuixgui.example.ui.component.AppLazyPage
import com.katiusu.miuixgui.example.ui.component.AppSectionCard
import com.katiusu.miuixgui.example.ui.component.sectionCardPadding
import com.katiusu.miuixgui.example.ui.icons.CheckCircleOutlineIcon
import com.katiusu.miuixgui.example.ui.icons.ErrorOutlineIcon
import com.katiusu.miuixgui.example.ui.icons.RemoveCircleOutlineIcon
import com.katiusu.miuixgui.example.ui.util.isInDarkTheme
import com.katiusu.miuixgui.example.ui.util.shouldShowSplitPane
import com.katiusu.miuixgui.example.util.SystemVersionDetector
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.PressFeedbackType
import top.yukonga.miuix.kmp.basic.Text as MiuixText

/**
 * 模板主页。所有数据均为占位符，接入业务时替换为真实数据即可。
 */
@Composable
fun HomePageView(
    isBlurEnabled: Boolean = true,
    refreshKey: Int = 0,
    extraBottomPadding: Dp = 0.dp,
    onOpenExamples: () -> Unit = {},
) {
    val title = stringResource(R.string.tab_home)

    AppLazyPage(
        title = title,
        isBlurEnabled = isBlurEnabled,
        extraBottomPadding = extraBottomPadding,
    ) {
        item {
            val isWideScreen = shouldShowSplitPane()
            val darkTheme = isInDarkTheme()
            val dynamicColor = MiuixTheme.isDynamicColor
            val activated = true
            val rootMissing = false
            val featureCount = OptionRegistry.all().size

            val statusColor = when {
                !activated -> when {
                    dynamicColor -> MiuixTheme.colorScheme.errorContainer
                    darkTheme -> Color(0xFF3D1C1C)
                    else -> Color(0xFFFDE8E8)
                }
                rootMissing -> when {
                    dynamicColor -> MiuixTheme.colorScheme.secondaryContainer
                    darkTheme -> Color(0xFF3D3520)
                    else -> Color(0xFFFDF6E3)
                }
                else -> when {
                    dynamicColor -> MiuixTheme.colorScheme.secondaryContainer
                    darkTheme -> Color(0xFF1A3825)
                    else -> Color(0xFFDFFAE4)
                }
            }
            val iconTint = when {
                !activated -> if (dynamicColor) MiuixTheme.colorScheme.error.copy(alpha = 0.8f) else Color(0xFFDC3545)
                rootMissing -> if (dynamicColor) MiuixTheme.colorScheme.primary.copy(alpha = 0.8f) else Color(0xFFE0A800)
                else -> if (dynamicColor) MiuixTheme.colorScheme.primary.copy(alpha = 0.8f) else Color(0xFF36D167)
            }
            val statusSummary = stringResource(
                when {
                    !activated -> R.string.module_status_summary_inactive
                    rootMissing -> R.string.module_status_summary_no_root
                    else -> R.string.module_status_summary_activated
                }
            )
            val statusDetail = if (rootMissing) {
                stringResource(R.string.module_status_root_hint)
            } else {
                ""
            }
            // 未激活 = 叉号；已激活但无 Root = 圈中横线；正常 = 对号。
            val statusIcon: ImageVector = when {
                !activated -> ErrorOutlineIcon
                rootMissing -> RemoveCircleOutlineIcon
                else -> CheckCircleOutlineIcon
            }

            val cardsModifier = Modifier
                .fillMaxWidth()
                // 首页首组卡片：上方补 12.dp 与顶栏/大标题拉开距离，其余外边距走统一规范。
                .sectionCardPadding(top = 12.dp)
                .height(IntrinsicSize.Min)

            if (isWideScreen) {
                // 横屏：状态卡 / 作用域 / 功能总数 三卡片横向三等分。
                Row(
                    modifier = cardsModifier,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusCard(
                        titleText = stringResource(R.string.module_status),
                        versionText = statusSummary,
                        statusText = statusDetail,
                        imageVector = statusIcon,
                        iconTint = iconTint,
                        statusColor = statusColor,
                        onClick = {},
                        compact = true,
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                    )
                    CountCard(
                        title = stringResource(R.string.home_feature_count),
                        count = featureCount.toString(),
                        onClick = onOpenExamples,
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                    )
                }
            } else {
                Row(
                    modifier = cardsModifier,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusCard(
                        titleText = stringResource(R.string.module_status),
                        versionText = statusSummary,
                        statusText = statusDetail,
                        imageVector = statusIcon,
                        iconTint = iconTint,
                        statusColor = statusColor,
                        onClick = {},
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        CountCard(
                            title = stringResource(R.string.home_feature_count),
                            count = featureCount.toString(),
                            onClick = onOpenExamples,
                            modifier = Modifier.fillMaxWidth().weight(1f),
                        )
                    }
                }
            }
        }

        item {
            val context = LocalContext.current
            val deviceModel = remember {
                SystemVersionDetector.getMarketName().ifEmpty { "Unknown" }
            }
            val deviceName = remember {
                try {
                    Settings.Global.getString(
                        context.contentResolver,
                        Settings.Global.DEVICE_NAME
                    )?.takeIf { it.isNotBlank() } ?: deviceModel
                } catch (_: Exception) {
                    deviceModel
                }
            }
            val hyperOsVersion = remember { SystemVersionDetector.getHyperOsVersion() }
            val androidVersion = remember { SystemVersionDetector.getAndroidVersion() }
            val appVersion = remember {
                try {
                    context.packageManager
                        .getPackageInfo(context.packageName, 0).versionName ?: "1.0"
                } catch (_: Exception) {
                    "1.0"
                }
            }

            AppSectionCard(title = stringResource(R.string.home_device_info)) {
                BasicComponent(
                    title = stringResource(R.string.home_device_name),
                    summary = deviceName,
                )
                BasicComponent(
                    title = stringResource(R.string.home_device_model),
                    summary = deviceModel,
                )
                BasicComponent(
                    title = stringResource(R.string.home_hyperos_version),
                    summary = hyperOsVersion,
                )
                BasicComponent(
                    title = stringResource(R.string.home_android_version),
                    summary = androidVersion,
                )
                BasicComponent(
                    title = stringResource(R.string.home_app_version),
                    summary = appVersion
                )
            }
        }

        item {
            val uriHandler = LocalUriHandler.current
            val sourceCodeUrl = stringResource(R.string.about_source_code_summary)
            val telegramUrl = stringResource(R.string.about_telegram_summary)
            AppSectionCard(title = stringResource(R.string.home_shortcuts)) {
                AppArrowPreference(
                    title = stringResource(R.string.home_component_demo),
                    summary = stringResource(R.string.home_component_demo_summary),
                    onClick = onOpenExamples,
                )
                AppArrowPreference(
                    title = stringResource(R.string.about_source_code),
                    summary = sourceCodeUrl,
                    onClick = { runCatching { uriHandler.openUri(sourceCodeUrl) } },
                )
                AppArrowPreference(
                    title = stringResource(R.string.about_telegram),
                    summary = telegramUrl,
                    onClick = { runCatching { uriHandler.openUri(telegramUrl) } },
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun StatusCard(
    titleText: String,
    versionText: String,
    statusText: String,
    imageVector: ImageVector,
    iconTint: Color,
    statusColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    val iconSize = if (compact) 100.dp else 170.dp
    val iconOffsetX = if (compact) 22.dp else 38.dp
    val iconOffsetY = if (compact) 26.dp else 45.dp
    Card(
        modifier = modifier,
        colors = CardDefaults.defaultColors(color = statusColor),
        onClick = onClick,
        showIndication = true,
        pressFeedbackType = PressFeedbackType.Tilt
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(iconOffsetX, iconOffsetY),
                contentAlignment = Alignment.BottomEnd
            ) {
                Icon(
                    modifier = Modifier.size(iconSize),
                    imageVector = imageVector,
                    tint = iconTint,
                    contentDescription = null
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(all = 16.dp)
            ) {
                MiuixText(
                    modifier = Modifier.fillMaxWidth(),
                    text = titleText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(4.dp))
                MiuixText(
                    modifier = Modifier.fillMaxWidth(),
                    text = versionText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(2.dp))
                MiuixText(
                    modifier = Modifier.fillMaxWidth(),
                    text = statusText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun CountCard(
    title: String,
    count: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    Card(
        modifier = modifier,
        insideMargin = PaddingValues(16.dp),
        showIndication = true,
        onClick = onClick,
        pressFeedbackType = PressFeedbackType.Tilt
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            MiuixText(
                modifier = Modifier.fillMaxWidth(),
                text = title,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary
            )
            MiuixText(
                modifier = Modifier.fillMaxWidth(),
                text = count,
                fontSize = 26.sp,
                fontWeight = FontWeight.SemiBold,
                color = MiuixTheme.colorScheme.onSurface
            )
        }
    }
}
