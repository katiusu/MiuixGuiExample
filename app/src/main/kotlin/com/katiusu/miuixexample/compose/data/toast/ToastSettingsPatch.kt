package com.katiusu.miuixexample.compose.data.toast

import com.katiusu.miuixexample.compose.data.ToastAppSettings

/** null 表示批量编辑时不修改该字段；单应用表单使用 [ToastAppSettings.toFullPatch]。 */
internal data class ToastSettingsPatch(
    val forwardEnabled: Boolean? = null,
    val blockOriginal: Boolean? = null,
    val showNotification: Boolean? = null,
    val showCardIcon: Boolean? = null,
    val firstFloat: String? = null,
    val enableFloat: String? = null,
    val preserveSmallIcon: String? = null,
    val marquee: String? = null,
    val marqueeAutoHide: String? = null,
    val timeout: String? = null,
    val highlightColor: String? = null,
    val dynamicHighlightColor: String? = null,
    val showLeftHighlight: String? = null,
    val showRightHighlight: String? = null,
    val outerGlow: String? = null,
    val outEffectColor: String? = null,
    val cardOuterGlow: String? = null,
    val cardOuterGlowColor: String? = null,
    val filterMode: String? = null,
    val whitelistKeywords: List<String>? = null,
    val blacklistKeywords: List<String>? = null,
) {
    val hasChanges: Boolean
        get() = this != ToastSettingsPatch()
}

internal fun ToastAppSettings.toFullPatch(): ToastSettingsPatch = ToastSettingsPatch(
    forwardEnabled = forwardEnabled,
    blockOriginal = blockOriginal,
    showNotification = showNotification,
    showCardIcon = showCardIcon,
    firstFloat = firstFloat,
    enableFloat = enableFloat,
    preserveSmallIcon = preserveSmallIcon,
    marquee = marquee,
    marqueeAutoHide = marqueeAutoHide,
    timeout = timeout,
    highlightColor = highlightColor,
    dynamicHighlightColor = dynamicHighlightColor,
    showLeftHighlight = showLeftHighlight,
    showRightHighlight = showRightHighlight,
    outerGlow = outerGlow,
    outEffectColor = outEffectColor,
    cardOuterGlow = cardOuterGlow,
    cardOuterGlowColor = cardOuterGlowColor,
    filterMode = filterMode,
    whitelistKeywords = whitelistKeywords,
    blacklistKeywords = blacklistKeywords,
)

internal fun ToastAppSettings.withPatch(patch: ToastSettingsPatch): ToastAppSettings = copy(
    forwardEnabled = patch.forwardEnabled ?: forwardEnabled,
    blockOriginal = patch.blockOriginal ?: blockOriginal,
    showNotification = patch.showNotification ?: showNotification,
    showCardIcon = patch.showCardIcon ?: showCardIcon,
    firstFloat = patch.firstFloat ?: firstFloat,
    enableFloat = patch.enableFloat ?: enableFloat,
    preserveSmallIcon = patch.preserveSmallIcon ?: preserveSmallIcon,
    marquee = patch.marquee ?: marquee,
    marqueeAutoHide = patch.marqueeAutoHide ?: marqueeAutoHide,
    timeout = patch.timeout ?: timeout,
    highlightColor = patch.highlightColor ?: highlightColor,
    dynamicHighlightColor = patch.dynamicHighlightColor ?: dynamicHighlightColor,
    showLeftHighlight = patch.showLeftHighlight ?: showLeftHighlight,
    showRightHighlight = patch.showRightHighlight ?: showRightHighlight,
    outerGlow = patch.outerGlow ?: outerGlow,
    outEffectColor = patch.outEffectColor ?: outEffectColor,
    cardOuterGlow = patch.cardOuterGlow ?: cardOuterGlow,
    cardOuterGlowColor = patch.cardOuterGlowColor ?: cardOuterGlowColor,
    filterMode = patch.filterMode ?: filterMode,
    whitelistKeywords = patch.whitelistKeywords ?: whitelistKeywords,
    blacklistKeywords = patch.blacklistKeywords ?: blacklistKeywords,
)
