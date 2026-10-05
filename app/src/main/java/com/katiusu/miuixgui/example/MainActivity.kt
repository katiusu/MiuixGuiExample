package com.katiusu.miuixgui.example

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.katiusu.miuixgui.example.ui.component.UpdateDialog
import com.katiusu.miuixgui.example.ui.component.liquid.IosLiquidGlassNavigationBar
import com.katiusu.miuixgui.example.ui.screen.about.AboutPageContent
import com.katiusu.miuixgui.example.ui.screen.features.FeaturesPageView
import com.katiusu.miuixgui.example.ui.screen.home.HomePageView
import com.katiusu.miuixgui.example.ui.screen.settings.SettingsPageView
import com.katiusu.miuixgui.example.ui.theme.AppTheme
import com.katiusu.miuixgui.example.ui.util.applyWindowBackground
import com.katiusu.miuixgui.example.ui.util.isInDarkTheme
import com.katiusu.miuixgui.example.ui.util.shouldExpandNavigationRail
import com.katiusu.miuixgui.example.ui.util.shouldShowSplitPane
import com.katiusu.miuixgui.example.bridge.XposedServiceManager
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.FloatingNavigationBar
import top.yukonga.miuix.kmp.basic.FloatingNavigationBarItem
import top.yukonga.miuix.kmp.basic.FloatingToolbarDefaults
import top.yukonga.miuix.kmp.basic.NavigationBar
import top.yukonga.miuix.kmp.basic.NavigationBarItem
import top.yukonga.miuix.kmp.basic.NavigationItem
import top.yukonga.miuix.kmp.basic.NavigationRail
import top.yukonga.miuix.kmp.basic.NavigationRailItem
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.rememberNavigationRailState
import top.yukonga.miuix.kmp.blur.BlendColorEntry
import top.yukonga.miuix.kmp.blur.BlurDefaults
import top.yukonga.miuix.kmp.blur.LayerBackdrop
import top.yukonga.miuix.kmp.blur.highlight.Highlight
import top.yukonga.miuix.kmp.blur.layerBackdrop
import top.yukonga.miuix.kmp.blur.rememberLayerBackdrop
import top.yukonga.miuix.kmp.blur.textureBlur
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Home
import top.yukonga.miuix.kmp.icon.extended.Info
import top.yukonga.miuix.kmp.icon.extended.ListView
import top.yukonga.miuix.kmp.icon.extended.Settings
import top.yukonga.miuix.kmp.theme.ColorSchemeMode
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.PagerGestureNestedScrollConnection
import top.yukonga.miuix.kmp.utils.PagerInterceptionMode
import top.yukonga.miuix.kmp.utils.pagerGestureOverride
import top.yukonga.miuix.kmp.utils.springAnimateToPage

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        val language = LocaleHelper.getSavedLanguage(newBase)
        super.attachBaseContext(LocaleHelper.wrapContext(newBase, language))
    }

    override fun onResume() {
        super.onResume()
        // 回到前台时重新检测 Root（用户可能刚刚授予权限）。
        XposedServiceManager.checkRoot()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.isNavigationBarContrastEnforced = false

        val savedSettings = AppSettings.load(this)

        // 覆盖 XML 主题的窗口背景，兼容「系统浅色但应用内手动强制深色」的情况，避免启动白屏闪烁。
        applyWindowBackground(savedSettings.themeMode)

        setContent {
            var themeMode by remember {
                mutableStateOf(
                    try { ColorSchemeMode.valueOf(savedSettings.themeMode) }
                    catch (_: Exception) { ColorSchemeMode.System }
                )
            }
            var isFloatingNavbar by remember { mutableStateOf(savedSettings.isFloatingNavbar) }
            var isLiquidGlass by remember { mutableStateOf(savedSettings.isLiquidGlass) }
            var isBlurEnabled by remember { mutableStateOf(savedSettings.isBlurEnabled) }
            var checkUpdateOnLaunch by remember { mutableStateOf(savedSettings.checkUpdateOnLaunch) }

            var updateInfo by remember { mutableStateOf<UpdateChecker.UpdateInfo?>(null) }
            var isCheckingUpdate by remember { mutableStateOf(false) }
            val uriHandler = LocalUriHandler.current
            val scope = rememberCoroutineScope()

            LaunchedEffect(Unit) {
                if (checkUpdateOnLaunch) {
                    UpdateChecker.checkForUpdate(this@MainActivity)
                        .onSuccess { info -> if (info.hasUpdate) updateInfo = info }
                }
            }

            fun checkUpdate() {
                if (isCheckingUpdate) return
                isCheckingUpdate = true
                scope.launch {
                    val result = UpdateChecker.checkForUpdate(this@MainActivity)
                    isCheckingUpdate = false
                    result.fold(
                        onSuccess = { info ->
                            if (info.hasUpdate) {
                                updateInfo = info
                            } else {
                                Toast.makeText(
                                    this@MainActivity,
                                    getString(R.string.update_latest),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        onFailure = {
                            Toast.makeText(
                                this@MainActivity,
                                getString(R.string.update_check_failed),
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                    )
                }
            }

            fun persistState() {
                AppSettings.save(
                    this@MainActivity,
                    AppSettings(
                        themeMode = themeMode.name,
                        isFloatingNavbar = isFloatingNavbar,
                        isLiquidGlass = isLiquidGlass,
                        isBlurEnabled = isBlurEnabled,
                        checkUpdateOnLaunch = checkUpdateOnLaunch,
                    )
                )
            }

            AppTheme(themeMode = themeMode) {
                MainScreen(
                    themeMode = themeMode,
                    isFloatingNavbar = isFloatingNavbar,
                    isLiquidGlass = isLiquidGlass,
                    isBlurEnabled = isBlurEnabled,
                    checkUpdateOnLaunch = checkUpdateOnLaunch,
                    isCheckingUpdate = isCheckingUpdate,
                    onThemeModeChange = { themeMode = it; persistState() },
                    onFloatingNavbarChange = { isFloatingNavbar = it; persistState() },
                    onLiquidGlassChange = { isLiquidGlass = it; persistState() },
                    onBlurEnabledChange = { isBlurEnabled = it; persistState() },
                    onCheckUpdateOnLaunchChange = { checkUpdateOnLaunch = it; persistState() },
                    onCheckUpdate = { checkUpdate() },
                )

                updateInfo?.let { info ->
                    UpdateDialog(
                        info = info,
                        onDismissRequest = { updateInfo = null },
                        onConfirm = {
                            updateInfo = null
                            runCatching { uriHandler.openUri(info.releaseUrl) }
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun MainScreen(
    themeMode: ColorSchemeMode,
    isFloatingNavbar: Boolean,
    isLiquidGlass: Boolean,
    isBlurEnabled: Boolean,
    checkUpdateOnLaunch: Boolean,
    isCheckingUpdate: Boolean,
    onThemeModeChange: (ColorSchemeMode) -> Unit,
    onFloatingNavbarChange: (Boolean) -> Unit,
    onLiquidGlassChange: (Boolean) -> Unit,
    onBlurEnabledChange: (Boolean) -> Unit,
    onCheckUpdateOnLaunchChange: (Boolean) -> Unit,
    onCheckUpdate: () -> Unit,
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val pagerState = rememberPagerState(pageCount = { 4 })
    var selectedIndex by remember { mutableIntStateOf(0) }
    var isNavigating by remember { mutableStateOf(false) }
    var navJob by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }
    var homeRefreshKey by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()
    val items = listOf(
        stringResource(R.string.tab_home),
        stringResource(R.string.tab_features),
        stringResource(R.string.tab_settings),
        stringResource(R.string.tab_about)
    )
    val icons = listOf(
        MiuixIcons.Home,
        MiuixIcons.ListView,
        MiuixIcons.Settings,
        MiuixIcons.Info
    )

    LaunchedEffect(pagerState.currentPage) {
        if (!isNavigating && selectedIndex != pagerState.currentPage) {
            selectedIndex = pagerState.currentPage
            homeRefreshKey++
        }
    }

    val surfaceColor = MiuixTheme.colorScheme.surface
    val backdrop = rememberLayerBackdrop {
        drawRect(surfaceColor)
        drawContent()
    }
    val blurActive = isBlurEnabled
    // 切页动画期间挂起底栏 / 侧栏的纹理模糊与整页 backdrop 录制：
    // 动画期间页面内容逐帧位移，模糊来源（整页 backdrop）会被逐帧重录、
    // 模糊逐帧重算，这是切页掉帧的主要来源之一。动画结束后立即恢复，
    // 底栏背后本身就是页面表面色，视觉上几乎无差异。
    val navBlurActive = blurActive && !isNavigating

    val navBarMode = if (!isFloatingNavbar) 0 else if (!isLiquidGlass) 1 else 2
    val isWideScreen = shouldShowSplitPane()
    // 横屏时仅普通底栏改为左侧 NavigationRail；悬浮栏与液态玻璃保留底部。
    val useNavigationRail = isWideScreen && navBarMode == 0

    val railState = rememberNavigationRailState()
    val expandRail = shouldExpandNavigationRail()
    LaunchedEffect(expandRail) {
        if (expandRail) railState.expand() else railState.collapse()
    }

    val onItemSelected: (Int) -> Unit = select@{ index ->
        if (index == selectedIndex) return@select
        homeRefreshKey++
        navJob?.cancel()
        selectedIndex = index
        isNavigating = true
        navJob = scope.launch {
            val myJob = coroutineContext.job
            try {
                pagerState.springAnimateToPage(index)
            } finally {
                if (navJob == myJob) {
                    isNavigating = false
                    if (pagerState.currentPage != index) {
                        selectedIndex = pagerState.currentPage
                    }
                }
            }
        }
    }

    val pagerContent: @Composable (Dp, PaddingValues) -> Unit = { navBarHeight, pagerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(if (navBlurActive) Modifier.layerBackdrop(backdrop) else Modifier)
                .background(surfaceColor)
        ) {
            HorizontalPager(
                state = pagerState,
                // 预组合全部 4 页：否则跨页跳转时目标页会在动画进行中才首次组合
                // （关于页的首次组合最贵：图标解码 + 多张模糊卡片 + 动效 painter）。
                beyondViewportPageCount = 3,
                contentPadding = pagerPadding,
                modifier = Modifier
                    .fillMaxSize()
                    .pagerGestureOverride(
                        pagerState = pagerState,
                        mode = PagerInterceptionMode.CrossAxisInterceptor,
                    ),
                userScrollEnabled = false,
                pageNestedScrollConnection = PagerGestureNestedScrollConnection,
            ) { page ->
                when (page) {
                    0 -> HomePageView(
                        isBlurEnabled = isBlurEnabled,
                        refreshKey = homeRefreshKey,
                        extraBottomPadding = navBarHeight,
                        onOpenExamples = { onItemSelected(1) },
                    )
                    1 -> FeaturesPageView(
                        isBlurEnabled = isBlurEnabled,
                        extraBottomPadding = navBarHeight,
                    )
                    2 -> SettingsPageView(
                        currentMode = themeMode,
                        onModeChange = onThemeModeChange,
                        isFloatingNavbar = isFloatingNavbar,
                        onFloatingNavbarChange = onFloatingNavbarChange,
                        isLiquidGlass = isLiquidGlass,
                        onLiquidGlassChange = onLiquidGlassChange,
                        isBlurEnabled = isBlurEnabled,
                        onBlurEnabledChange = onBlurEnabledChange,
                        checkUpdateOnLaunch = checkUpdateOnLaunch,
                        onCheckUpdateOnLaunchChange = onCheckUpdateOnLaunchChange,
                        onCheckUpdate = onCheckUpdate,
                        isCheckingUpdate = isCheckingUpdate,
                        extraBottomPadding = navBarHeight,
                    )
                    3 -> AboutPageContent(
                        openLicensePage = {
                            context.startActivity(Intent(context, LicenseActivity::class.java))
                        },
                        isBlurEnabled = isBlurEnabled,
                        // 只有关于页可见且不处于切页动画时才播放动态背景：
                        // 它的帧循环每帧重绘，是整屏 backdrop 与模糊逐帧重算的根源。
                        animateBackground = selectedIndex == 3 && !isNavigating,
                    )
                }
            }
        }
    }

    if (useNavigationRail) {
        val navigationBarBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        val density = LocalDensity.current
        var railWidthPx by remember { mutableIntStateOf(0) }
        val railWidth = with(density) { railWidthPx.toDp() }
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .consumeWindowInsets(
                        WindowInsets.systemBars
                            .add(WindowInsets.displayCutout)
                            .only(WindowInsetsSides.Start)
                    )
            ) {
                pagerContent(navigationBarBottom, PaddingValues(start = railWidth))
            }
            NavigationRail(
                modifier = Modifier
                    .onSizeChanged { railWidthPx = it.width }
                    .then(
                        if (navBlurActive) {
                            Modifier.textureBlur(
                                backdrop = backdrop,
                                shape = RectangleShape,
                                blurRadius = 25f,
                                colors = BlurDefaults.blurColors(
                                    blendColors = listOf(
                                        BlendColorEntry(color = MiuixTheme.colorScheme.surface.copy(0.5f)),
                                    ),
                                ),
                            )
                        } else {
                            Modifier
                        }
                    ),
                color = if (navBlurActive) Color.Transparent else MiuixTheme.colorScheme.surface,
                state = railState,
            ) {
                items.forEachIndexed { index, label ->
                    NavigationRailItem(
                        selected = selectedIndex == index,
                        onClick = { onItemSelected(index) },
                        icon = icons[index],
                        label = label,
                    )
                }
            }
        }
    } else {
        Scaffold(
            popupHost = { },
            bottomBar = {
                BottomNavigationBar(
                    mode = navBarMode,
                    items = items,
                    icons = icons,
                    selectedIndex = selectedIndex,
                    backdrop = backdrop,
                    blurActive = navBlurActive,
                    onItemSelected = onItemSelected,
                )
            }
        ) { globalPadding ->
            pagerContent(globalPadding.calculateBottomPadding(), PaddingValues(0.dp))
        }
    }
}

@Composable
private fun BottomNavigationBar(
    mode: Int,
    items: List<String>,
    icons: List<androidx.compose.ui.graphics.vector.ImageVector>,
    selectedIndex: Int,
    backdrop: LayerBackdrop?,
    blurActive: Boolean,
    onItemSelected: (Int) -> Unit,
) {
    when (mode) {
        2 -> {
            val navigationItems = remember(items, icons) {
                List(items.size) { i -> NavigationItem(items[i], icons[i]) }
            }
            // 始终限制液态玻璃底栏宽度并居中，保持与手机竖屏一致的小尺寸。
            val liquidModifier = Modifier
                .padding(horizontal = 12.dp)
                .widthIn(max = 440.dp)
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                IosLiquidGlassNavigationBar(
                    modifier = liquidModifier,
                    items = navigationItems,
                    selectedIndex = selectedIndex,
                    onItemClick = { index ->
                        onItemSelected(index)
                    },
                    backdrop = backdrop,
                    isBlurActive = blurActive,
                )
            }
        }
        1 -> {
            val floatingBarColor = if (blurActive) Color.Transparent else MiuixTheme.colorScheme.surfaceContainer
            val floatingBarShape = RoundedCornerShape(FloatingToolbarDefaults.CornerRadius)
            val isDark = isInDarkTheme()
            val floatingHighlight = remember(isDark) {
                if (isDark) Highlight.GlassStrokeMiddleDark else Highlight.GlassStrokeMiddleLight
            }
            FloatingNavigationBar(
                modifier = if (blurActive) {
                    Modifier.textureBlur(
                        backdrop = backdrop!!,
                        shape = floatingBarShape,
                        blurRadius = 25f,
                        colors = BlurDefaults.blurColors(
                            blendColors = listOf(
                                BlendColorEntry(color = MiuixTheme.colorScheme.surfaceContainer.copy(0.4f)),
                            ),
                        ),
                        highlight = floatingHighlight,
                    )
                } else {
                    Modifier
                },
                color = floatingBarColor,
            ) {
                items.forEachIndexed { index, label ->
                    FloatingNavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = { onItemSelected(index) },
                        icon = icons[index],
                        label = label,
                        enabled = true
                    )
                }
            }
        }
        else -> {
            val barColor = if (blurActive) Color.Transparent else MiuixTheme.colorScheme.surface
            Box(
                modifier = Modifier
                    .then(
                        if (blurActive) {
                            Modifier.textureBlur(
                                backdrop = backdrop!!,
                                shape = RectangleShape,
                                blurRadius = 25f,
                                colors = BlurDefaults.blurColors(
                                    blendColors = listOf(
                                        BlendColorEntry(color = MiuixTheme.colorScheme.surface.copy(0.5f)),
                                    ),
                                ),
                            )
                        } else {
                            Modifier
                        }
                    )
                    .background(barColor)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {},
                    )
            ) {
                NavigationBar(
                    color = barColor,
                ) {
                    items.forEachIndexed { index, label ->
                        NavigationBarItem(
                            selected = selectedIndex == index,
                            onClick = { onItemSelected(index) },
                            icon = icons[index],
                            label = label,
                            enabled = true
                        )
                    }
                }
            }
        }
    }
}
