package com.katiusu.miuixgui.example.ui.component.pref

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import com.katiusu.miuixgui.example.prefs.ConfigState
import com.katiusu.miuixgui.example.prefs.OptionRegistry
import com.katiusu.miuixgui.example.prefs.OptionSpec
import com.katiusu.miuixgui.example.ui.component.effect.DeviceType

/** 设备形态覆盖存储键。 */
private const val DEVICE_TYPE_KEY = "device_type"

/**
 * 当前生效的设备形态：优先读取设置页的覆盖值，否则按屏宽自动判定。
 *
 * 读取 [ConfigState]，因此更改设备类型时会实时重组刷新，无需重启页面。
 */
@Composable
fun rememberEffectiveDeviceType(): DeviceType {
    val configuration = LocalConfiguration.current
    val override = ConfigState.string(DEVICE_TYPE_KEY, "")
    val widthDp = configuration.screenWidthDp
    return remember(override, widthDp) {
        when (override.trim().lowercase()) {
            "phone" -> DeviceType.PHONE
            "pad", "fold" -> DeviceType.PAD
            else -> if (widthDp >= 600) DeviceType.PAD else DeviceType.PHONE
        }
    }
}

/**
 * 设备形态是否在 [OptionSpec.deviceScope] 白名单内；未声明白名单（null / 空）视为各设备通用。
 *
 * 非白名单设备返回 false，用于把设备独占功能**禁用而不隐藏**。
 */
@Composable
fun rememberDeviceScopeEnabled(spec: OptionSpec): Boolean {
    val scope = spec.deviceScope
    if (scope.isNullOrEmpty()) return true
    return rememberEffectiveDeviceType() in scope
}

/**
 * 解析依赖项：沿 [OptionSpec.dependsOn] 链向上逐级校验，任一级未满足即禁用（传递依赖）。
 */
@Composable
fun rememberDependencyEnabled(spec: OptionSpec): Boolean {
    var current = spec
    val visited = HashSet<String>()
    while (true) {
        val dependencyKey = current.dependsOn ?: return true
        if (!visited.add(dependencyKey)) return true
        val dependencySpec = OptionRegistry.find(dependencyKey) ?: return true
        val dependencyValue = ConfigState.bool(dependencyKey, dependencySpec.defaultBoolean)
        if (current.dependsOnValue != dependencyValue) return false
        current = dependencySpec
    }
}

/**
 * 组件是否可用：同时满足「依赖项」与「设备形态白名单」。
 */
@Composable
fun rememberOptionEnabled(spec: OptionSpec): Boolean =
    rememberDependencyEnabled(spec) && rememberDeviceScopeEnabled(spec)
