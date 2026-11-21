package com.katiusu.islandexample.compose.navigation

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import com.katiusu.islandexample.compose.theme.squircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import com.katiusu.islandexample.BuildConfig
import com.katiusu.islandexample.R
import com.katiusu.islandexample.compose.component.BarBackdropContent
import com.katiusu.islandexample.compose.component.BarBlurHost
import com.katiusu.islandexample.compose.component.BlurredBar
import com.katiusu.islandexample.compose.component.LocalRootBottomBarPadding
import com.katiusu.islandexample.compose.component.LiquidGlassNavigationBar
import com.katiusu.islandexample.compose.component.LiquidGlassNavigationItem
import com.katiusu.islandexample.compose.component.LAYER_EXIT_DURATION
import com.katiusu.islandexample.compose.component.NoticeDialogHost
import com.katiusu.islandexample.compose.component.PredictiveNavigationBackHandler
import com.katiusu.islandexample.compose.component.PredictiveNavigationBackdrop
import com.katiusu.islandexample.compose.component.PredictiveNavigationLayer
import com.katiusu.islandexample.compose.component.UpdateDialogHost
import com.katiusu.islandexample.compose.component.UpdateDialogState
import com.katiusu.islandexample.compose.component.barBlurBackground
import com.katiusu.islandexample.compose.component.predictiveNavigationBackground
import com.katiusu.islandexample.compose.component.rememberPredictiveNavigationLayerState
import com.katiusu.islandexample.compose.component.requiresBackdropCapture
import com.katiusu.islandexample.compose.data.FlutterPrefsRepository
import com.katiusu.islandexample.compose.data.InstalledApp
import com.katiusu.islandexample.compose.data.channel.BatchChannelTarget
import com.katiusu.islandexample.compose.data.rememberBooleanPreference
import com.katiusu.islandexample.compose.data.rememberLongPreference
import com.katiusu.islandexample.compose.page.AppsPage
import com.katiusu.islandexample.compose.page.AboutPage
import com.katiusu.islandexample.compose.page.ChangelogPage
import com.katiusu.islandexample.compose.page.DonorsPage
import com.katiusu.islandexample.compose.page.apps.NotificationChannelsPage
import com.katiusu.islandexample.compose.page.apps.channel.ChannelEditorPage
import com.katiusu.islandexample.compose.page.apps.channel.BatchChannelSettingsPage
import com.katiusu.islandexample.compose.page.apps.MediaNotificationPage
import com.katiusu.islandexample.compose.page.apps.ToastSettingsPage
import com.katiusu.islandexample.compose.page.apps.toast.BatchToastSettingsPage
import com.katiusu.islandexample.compose.page.home.OverviewPage
import com.katiusu.islandexample.compose.page.home.rememberHomeOverviewState
import com.katiusu.islandexample.compose.page.onboarding.OnboardingPage
import com.katiusu.islandexample.compose.page.SettingsDetail
import com.katiusu.islandexample.compose.page.SettingsPage
import com.katiusu.islandexample.compose.page.settings.HideBehaviorPage
import com.katiusu.islandexample.compose.page.settings.appearance.AppearancePage
import com.katiusu.islandexample.compose.page.settings.appearance.SoftGlassSceneBrightnessPage
import com.katiusu.islandexample.compose.page.settings.IslandMaterialPage
import com.katiusu.islandexample.compose.page.settings.DefaultConfigPage
import com.katiusu.islandexample.compose.page.settings.AiConfigPage
import com.katiusu.islandexample.compose.page.settings.BackupRestorePage
import com.katiusu.islandexample.compose.page.settings.FilterRulesPage
import com.katiusu.islandexample.compose.page.settings.IslandOtherPage
import com.katiusu.islandexample.compose.page.settings.KeepIslandPage
import com.katiusu.islandexample.compose.page.settings.MiscPage
import com.katiusu.islandexample.compose.page.settings.PresetConfigPage
import com.katiusu.islandexample.compose.page.settings.ReferencesPage
import com.katiusu.islandexample.compose.page.settings.ThemeSettingsPage
import com.katiusu.islandexample.compose.page.settings.extensions.homeassistant.HomeAssistantPage
import com.katiusu.islandexample.compose.service.UpdateService
import com.katiusu.islandexample.compose.service.AppNotice
import com.katiusu.islandexample.compose.service.HubClient
import com.katiusu.islandexample.compose.service.NoticeService
import com.katiusu.islandexample.compose.theme.PREF_BLUR_BARS
import com.katiusu.islandexample.compose.theme.PREF_FLOATING_NAVIGATION_BAR
import com.katiusu.islandexample.compose.theme.PREF_LIQUID_GLASS_NAVIGATION_BAR
import com.katiusu.islandexample.compose.theme.DEFAULT_PREDICTIVE_BACK_TRANSLATION_PERCENT
import com.katiusu.islandexample.compose.theme.PREF_PREDICTIVE_BACK_MAX_TRANSLATION
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.FloatingNavigationBar
import top.yukonga.miuix.kmp.basic.FloatingNavigationBarItem
import top.yukonga.miuix.kmp.basic.FloatingToolbarDefaults
import top.yukonga.miuix.kmp.basic.NavigationBar
import top.yukonga.miuix.kmp.basic.NavigationBarItem
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SnackbarHost
import top.yukonga.miuix.kmp.basic.SnackbarHostState
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.GridView
import top.yukonga.miuix.kmp.icon.extended.Home
import top.yukonga.miuix.kmp.icon.extended.Info
import top.yukonga.miuix.kmp.icon.extended.Settings

private data class RootDestination(@StringRes val title: Int, val icon: ImageVector)

@Composable
internal fun IslandExampleApp(prefs: FlutterPrefsRepository) {
    val context = LocalContext.current
    val destinations = remember {
        listOf(
            RootDestination(R.string.nav_home, MiuixIcons.Home),
            RootDestination(R.string.nav_apps, MiuixIcons.GridView),
            RootDestination(R.string.nav_settings, MiuixIcons.Settings),
            RootDestination(R.string.about, MiuixIcons.Info),
        )
    }
    val pagerState = rememberPagerState(pageCount = { destinations.size })
    var appsSelectedMode by remember { mutableIntStateOf(0) }
    val homeOverviewState = rememberHomeOverviewState(prefs)
    val scope = rememberCoroutineScope()
    val rootSnackbarState = remember { SnackbarHostState() }
    val alreadyLatestMessage = stringResource(R.string.already_latest)
    var updateDialogState by remember { mutableStateOf<UpdateDialogState?>(null) }
    var notice by remember { mutableStateOf<AppNotice?>(null) }
    var isCheckingUpdate by remember { mutableStateOf(false) }
    val floatingNavigationBar = rememberBooleanPreference(prefs, PREF_FLOATING_NAVIGATION_BAR, false)
    val liquidGlassNavigationBar = rememberBooleanPreference(
        prefs,
        PREF_LIQUID_GLASS_NAVIGATION_BAR,
        false,
    )
    val blurBars = rememberBooleanPreference(prefs, PREF_BLUR_BARS, false)
    val predictiveBackMaxTranslation = rememberLongPreference(
        prefs,
        PREF_PREDICTIVE_BACK_MAX_TRANSLATION,
        DEFAULT_PREDICTIVE_BACK_TRANSLATION_PERCENT,
    )
    var visibleDetail by remember { mutableStateOf<SettingsDetail?>(null) }
    var visibleChannelApp by remember { mutableStateOf<InstalledApp?>(null) }
    var visibleToastApp by remember { mutableStateOf<InstalledApp?>(null) }
    var detailShown by remember { mutableStateOf(false) }
    var mediaShown by remember { mutableStateOf(false) }
    var visibleChannelEditor by remember { mutableStateOf<com.katiusu.islandexample.compose.data.NotificationChannelInfo?>(null) }
    var batchChannelTarget by remember { mutableStateOf<BatchChannelTarget?>(null) }
    var batchToastPackages by remember { mutableStateOf<Set<String>?>(null) }
    var materialShown by remember { mutableStateOf(false) }
    var sceneBrightnessShown by remember { mutableStateOf(false) }
    val nestedDetailShown = mediaShown || materialShown || sceneBrightnessShown || visibleChannelEditor != null ||
        (visibleChannelApp != null && batchChannelTarget != null)
    val extensionSubDetailShown = false
    val detailNavigationState = rememberPredictiveNavigationLayerState()
    val nestedNavigationState = rememberPredictiveNavigationLayerState()
    val extensionNavigationState = rememberPredictiveNavigationLayerState()
    val bottomBarProgress = remember { Animatable(0f) }
    var bottomBarHeightPx by remember { mutableIntStateOf(0) }
    var bottomBarComposed by remember { mutableStateOf(true) }
    val density = LocalDensity.current

    @Composable
    fun RootBottomBar(enabled: Boolean = true) {
        if (floatingNavigationBar.value) {
            if (liquidGlassNavigationBar.value) {
                LiquidGlassNavigationBar(
                    selectedTabIndex = { pagerState.currentPage },
                    onTabSelected = { index ->
                        if (enabled && pagerState.currentPage != index) {
                            scope.launch { pagerState.animateScrollToPage(index) }
                        }
                    },
                    items = destinations.map { destination ->
                        LiquidGlassNavigationItem(
                            icon = destination.icon,
                            label = stringResource(destination.title),
                        )
                    },
                )
            } else {
                FloatingNavigationBar(
                    modifier = Modifier.barBlurBackground(
                        squircleShape(FloatingToolbarDefaults.CornerRadius),
                    ),
                    color = Color.Transparent,
                ) {
                    destinations.forEachIndexed { index, destination ->
                        FloatingNavigationBarItem(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                if (enabled) scope.launch { pagerState.animateScrollToPage(index) }
                            },
                            icon = destination.icon,
                            label = stringResource(destination.title),
                        )
                    }
                }
            }
        } else {
            BlurredBar {
                NavigationBar(color = Color.Transparent) {
                    destinations.forEachIndexed { index, destination ->
                        NavigationBarItem(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                if (enabled) scope.launch { pagerState.animateScrollToPage(index) }
                            },
                            icon = destination.icon,
                            label = stringResource(destination.title),
                        )
                    }
                }
            }
        }
    }

    fun requestUpdateCheck(showUpToDate: Boolean) {
        if (isCheckingUpdate) return
        isCheckingUpdate = true
        scope.launch {
            var showAlreadyLatest = false
            try {
                val update = UpdateService.fetchIfNewer(BuildConfig.VERSION_NAME)
                if (update != null) {
                    updateDialogState = UpdateDialogState.Available(
                        currentVersion = BuildConfig.VERSION_NAME,
                        update = update,
                    )
                } else if (showUpToDate) {
                    showAlreadyLatest = true
                }
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                updateDialogState = UpdateDialogState.Failure
            } finally {
                isCheckingUpdate = false
            }
            if (showAlreadyLatest) {
                rootSnackbarState.showSnackbar(alreadyLatestMessage)
            }
        }
    }

    LaunchedEffect(prefs) {
        if (prefs.getBoolean(PREF_CHECK_UPDATE_ON_LAUNCH, true)) {
            requestUpdateCheck(showUpToDate = false)
        }
    }

    // 公告：每次冷启动拉一次。版本范围与系统版本由服务端筛（见 Hub 的 _lib/notice.js），
    // 客户端只比对哈希 —— 关过的就不再弹，服务端改了正文（哈希变了）就再弹一次。
    // 失败一律静默：公告是锦上添花，网络不通时不该在启动路径上留下任何痕迹。
    LaunchedEffect(prefs) {
        val fetched = runCatching {
            val hyperOs = withContext(Dispatchers.IO) { NoticeService.hyperOsVersionName() }
            HubClient.notice(BuildConfig.VERSION_NAME, hyperOs)
        }.getOrNull()
        if (fetched != null && !NoticeService.isDismissed(context, fetched.hash)) {
            notice = fetched
        }
    }

    fun closeDetail() {
        detailShown = false
        batchChannelTarget = null
        batchToastPackages = null
    }

    LaunchedEffect(detailShown, detailNavigationState.isBackActive) {
        if (detailNavigationState.isBackActive) {
            bottomBarComposed = true
        } else {
            if (!detailShown) bottomBarComposed = true
            bottomBarProgress.animateTo(
                if (detailShown) 1f else 0f,
                tween(
                    if (detailShown) BOTTOM_BAR_ENTER_DURATION else LAYER_EXIT_DURATION,
                    easing = FastOutSlowInEasing,
                ),
            )
            if (detailShown) bottomBarComposed = false
        }
    }

    PredictiveNavigationBackHandler(
        visible = detailShown,
        enabled = detailShown && !nestedDetailShown,
        state = detailNavigationState,
        maxTranslationPercent = predictiveBackMaxTranslation.value,
        onDismiss = {
            detailShown = false
            batchChannelTarget = null
            batchToastPackages = null
        },
        additionalCommitAnimation = { _, _ ->
            bottomBarProgress.animateTo(
                0f,
                tween(LAYER_EXIT_DURATION, easing = FastOutSlowInEasing),
            )
        },
    )

    PredictiveNavigationBackHandler(
        visible = nestedDetailShown,
        enabled = nestedDetailShown && !extensionSubDetailShown,
        state = nestedNavigationState,
        maxTranslationPercent = predictiveBackMaxTranslation.value,
        onDismiss = {
            mediaShown = false
            materialShown = false
            sceneBrightnessShown = false
            visibleChannelEditor = null
            if (visibleChannelApp != null) batchChannelTarget = null
            extensionDetail = null
            extensionSubDetail = null
        },
    )

    PredictiveNavigationBackHandler(
        visible = extensionSubDetailShown,
        enabled = extensionSubDetailShown,
        state = extensionNavigationState,
        maxTranslationPercent = predictiveBackMaxTranslation.value,
        onDismiss = { extensionSubDetail = null },
    )

    BarBlurHost(
        enabled = blurBars.value,
        liquidGlassEnabled = floatingNavigationBar.value && liquidGlassNavigationBar.value,
        captureForEffects = detailNavigationState.requiresBackdropCapture(detailShown),
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(rootSnackbarState) },
            bottomBar = {
                // Reserve space without composing a transparent, clickable second navigation bar.
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(with(density) { bottomBarHeightPx.toDp() }),
                )
            },
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .predictiveNavigationBackground(detailNavigationState),
                ) {
                    BarBackdropContent(modifier = Modifier.fillMaxSize()) {
                        CompositionLocalProvider(
                            LocalRootBottomBarPadding provides padding.calculateBottomPadding(),
                        ) {
                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier.fillMaxSize(),
                                beyondViewportPageCount = 1,
                            ) { page ->
                            when (page) {
                                0 -> OverviewPage(
                                    state = homeOverviewState,
                                    isActive = pagerState.settledPage == page,
                                    onOpenApps = {
                                        appsSelectedMode = 0
                                        scope.launch { pagerState.animateScrollToPage(1) }
                                    },
                                    onOpenToastApps = {
                                        appsSelectedMode = 1
                                        scope.launch { pagerState.animateScrollToPage(1) }
                                    },
                                    onOpenDonors = {
                                        batchChannelTarget = null
                                        batchToastPackages = null
                                        visibleChannelApp = null
                                        visibleToastApp = null
                                        extensionDetail = null
                                        extensionSubDetail = null
                                        visibleDetail = SettingsDetail.Donors
                                        detailShown = true
                                    },
                                )
                                1 -> AppsPage(
                                    prefs = prefs,
                                    selectedMode = appsSelectedMode,
                                    onSelectedModeChange = { appsSelectedMode = it },
                                    onOpenChannels = { app ->
                                        batchChannelTarget = null
                                        batchToastPackages = null
                                        visibleDetail = null
                                        visibleToastApp = null
                                        visibleChannelApp = app
                                        detailShown = true
                                    },
                                    onOpenToastSettings = { app ->
                                        batchChannelTarget = null
                                        batchToastPackages = null
                                        visibleDetail = null
                                        visibleChannelApp = null
                                        visibleToastApp = app
                                        detailShown = true
                                    },
                                    onOpenBatchChannelSettings = { packages ->
                                        batchToastPackages = null
                                        visibleDetail = null
                                        visibleChannelApp = null
                                        visibleToastApp = null
                                        batchChannelTarget = BatchChannelTarget.Apps(packages)
                                        detailShown = true
                                    },
                                    onOpenBatchToastSettings = { packages ->
                                        batchChannelTarget = null
                                        visibleDetail = null
                                        visibleChannelApp = null
                                        visibleToastApp = null
                                        batchToastPackages = packages
                                        detailShown = true
                                    },
                                )
                                2 -> SettingsPage(
                                    prefs = prefs,
                                    onOpenDetail = {
                                        batchChannelTarget = null
                                        batchToastPackages = null
                                        visibleChannelApp = null
                                        visibleToastApp = null
                                        extensionDetail = null
                                        extensionSubDetail = null
                                        visibleDetail = it
                                        detailShown = true
                                    },
                                )
                                else -> AboutPage(
                                    isActive = pagerState.currentPage == page,
                                    isCheckingUpdate = isCheckingUpdate,
                                    onCheckUpdate = { requestUpdateCheck(showUpToDate = true) },
                                    onOpenBackupRestore = {
                                        batchChannelTarget = null
                                        batchToastPackages = null
                                        visibleChannelApp = null
                                        visibleToastApp = null
                                        visibleDetail = SettingsDetail.BackupRestore
                                        detailShown = true
                                    },
                                    onOpenReferences = {
                                        batchChannelTarget = null
                                        batchToastPackages = null
                                        visibleChannelApp = null
                                        visibleToastApp = null
                                        visibleDetail = SettingsDetail.References
                                        detailShown = true
                                    },
                                    onOpenDebugTools = {
                                        batchChannelTarget = null
                                        batchToastPackages = null
                                        visibleChannelApp = null
                                        visibleToastApp = null
                                        visibleDetail = SettingsDetail.DebugTools
                                        detailShown = true
                                    },
                                    onOpenChangelog = {
                                        batchChannelTarget = null
                                        batchToastPackages = null
                                        visibleChannelApp = null
                                        visibleToastApp = null
                                        visibleDetail = SettingsDetail.Changelog
                                        detailShown = true
                                    },
                                )
                            }
                            }
                        }
                    }
                }

                PredictiveNavigationBackdrop(
                    state = detailNavigationState,
                    modifier = Modifier.fillMaxSize(),
                )

                BarBlurHost(
                enabled = blurBars.value,
                captureForEffects = nestedNavigationState.requiresBackdropCapture(
                    nestedDetailShown,
                ),
                ) {
                BarBackdropContent(modifier = Modifier.fillMaxSize()) {
                    PredictiveNavigationLayer(
                        visible = detailShown,
                        state = detailNavigationState,
                        backgroundState = nestedNavigationState,
                        maxTranslationPercent = predictiveBackMaxTranslation.value,
                    ) {
                        val channelApp = visibleChannelApp
                        if (channelApp != null) {
                            NotificationChannelsPage(
                                app = channelApp,
                                prefs = prefs,
                                onBack = ::closeDetail,
                                onOpenMediaSettings = {
                                    materialShown = false
                                    visibleChannelEditor = null
                                    mediaShown = true
                                },
                                onOpenChannelSettings = { channel ->
                                    mediaShown = false
                                    materialShown = false
                                    visibleChannelEditor = channel
                                },
                                onOpenBatchChannelSettings = { channelIds ->
                                    mediaShown = false
                                    materialShown = false
                                    visibleChannelEditor = null
                                    batchChannelTarget = BatchChannelTarget.Channels(
                                        channelApp.packageName,
                                        channelIds,
                                    )
                                },
                            )
                        } else if (visibleToastApp != null) {
                            ToastSettingsPage(
                                app = visibleToastApp!!,
                                prefs = prefs,
                                onBack = ::closeDetail,
                            )
                        } else if (batchChannelTarget != null) {
                            BatchChannelSettingsPage(
                                target = batchChannelTarget!!,
                                prefs = prefs,
                                onBack = ::closeDetail,
                            )
                        } else if (batchToastPackages != null) {
                            BatchToastSettingsPage(
                                packageNames = batchToastPackages!!,
                                prefs = prefs,
                                onBack = ::closeDetail,
                            )
                        } else {
                            when (visibleDetail) {
                                SettingsDetail.Appearance -> AppearancePage(
                                    prefs = prefs,
                                    materialVisible = materialShown || sceneBrightnessShown,
                                    onOpenMaterial = {
                                        mediaShown = false
                                        sceneBrightnessShown = false
                                        materialShown = true
                                    },
                                    onOpenSceneBrightness = {
                                        mediaShown = false
                                        materialShown = false
                                        sceneBrightnessShown = true
                                    },
                                    onBack = ::closeDetail,
                                )
                                SettingsDetail.Theme -> ThemeSettingsPage(prefs, ::closeDetail)
                                SettingsDetail.HideBehavior -> HideBehaviorPage(prefs, ::closeDetail)
                                SettingsDetail.DefaultConfig -> DefaultConfigPage(prefs, ::closeDetail)
                                SettingsDetail.Preset -> PresetConfigPage(prefs = prefs, onBack = ::closeDetail)
                                SettingsDetail.AiConfig -> AiConfigPage(prefs, ::closeDetail)
                                SettingsDetail.Misc -> MiscPage(
                                    prefs = prefs,
                                    onOpenOnboarding = {
                                        visibleDetail = SettingsDetail.Onboarding
                                    },
                                    onBack = ::closeDetail,
                                )
                                SettingsDetail.Other -> IslandOtherPage(prefs, ::closeDetail)
                                SettingsDetail.References -> ReferencesPage(::closeDetail)
                                SettingsDetail.Changelog -> ChangelogPage(::closeDetail)
                                SettingsDetail.Donors -> DonorsPage(::closeDetail)
                                SettingsDetail.BackupRestore -> BackupRestorePage(::closeDetail)
                                SettingsDetail.FilterRules -> FilterRulesPage(prefs, ::closeDetail)
                                SettingsDetail.KeepIsland -> KeepIslandPage(prefs, ::closeDetail)
                                SettingsDetail.Onboarding -> OnboardingPage(
                                    prefs = prefs,
                                    showCloseButton = true,
                                    onFinished = ::closeDetail,
                                )
                                null -> Unit
                            }
                        }
                    }
                }

                PredictiveNavigationBackdrop(
                    state = nestedNavigationState,
                    modifier = Modifier.fillMaxSize(),
                )

                BarBlurHost(
                    enabled = blurBars.value,
                    captureForEffects = extensionNavigationState.requiresBackdropCapture(
                        extensionSubDetailShown,
                    ),
                ) {
                    BarBackdropContent(modifier = Modifier.fillMaxSize()) {
                        PredictiveNavigationLayer(
                            visible = nestedDetailShown,
                            state = nestedNavigationState,
                            backgroundState = extensionNavigationState,
                            maxTranslationPercent = predictiveBackMaxTranslation.value,
                        ) {
                            run {
                                if (batchChannelTarget != null && visibleChannelApp != null) {
                                    BatchChannelSettingsPage(
                                        target = batchChannelTarget!!,
                                        prefs = prefs,
                                        onBack = { batchChannelTarget = null },
                                    )
                                } else if (visibleChannelEditor != null && visibleChannelApp != null) {
                                    ChannelEditorPage(
                                        appPackage = visibleChannelApp!!.packageName,
                                        channel = visibleChannelEditor!!,
                                        prefs = prefs,
                                        onBack = { visibleChannelEditor = null },
                                    )
                                } else if (sceneBrightnessShown) {
                                    SoftGlassSceneBrightnessPage(
                                        prefs = prefs,
                                        onBack = { sceneBrightnessShown = false },
                                    )
                                } else if (materialShown) {
                                    IslandMaterialPage(
                                        prefs = prefs,
                                        onBack = { materialShown = false },
                                    )
                                } else {
                                    visibleChannelApp?.let { app ->
                                        MediaNotificationPage(
                                            app = app,
                                            prefs = prefs,
                                            onBack = { mediaShown = false },
                                        )
                                    }
                                }
                            }
                        }
                    }

                }
                }

                if (bottomBarComposed) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .onSizeChanged { bottomBarHeightPx = it.height }
                            .graphicsLayer {
                            translationY = size.height * bottomBarProgress.value
                            alpha = 1f - bottomBarProgress.value
                        },
                    ) {
                        RootBottomBar(enabled = !detailShown)
                    }
                }
            }
        }
    }

    UpdateDialogHost(
        state = updateDialogState,
        onDismiss = { updateDialogState = null },
        onViewUpdate = { releaseUrl ->
            updateDialogState = null
            runCatching {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(releaseUrl)))
            }.onFailure {
                updateDialogState = UpdateDialogState.Failure
            }
        },
    )

    // 更新弹窗在场时先让位：两个 WindowDialog 同时弹会互相抢焦点，
    // 而「有新版本」比公告更该被先看到。它关掉之后公告再出现，倒计时从头开始。
    NoticeDialogHost(
        notice = notice.takeIf { updateDialogState == null },
        onConfirm = { shown ->
            NoticeService.markDismissed(context, shown.hash)
            notice = null
        },
    )
}

private const val PREF_CHECK_UPDATE_ON_LAUNCH = "pref_check_update_on_launch"
private const val BOTTOM_BAR_ENTER_DURATION = 300
