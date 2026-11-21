package com.katiusu.miuixexample.compose.data

import org.json.JSONObject

internal enum class CardMaterialType(val value: String) {
    Default("default"),
    Gaussian("gaussian"),
    HighlightGlass("highlight_glass"),
    LiquidGlass("liquid_glass"),
    SoftGlass("soft_glass");

    /** 默认类型即「不使用自定义材质」，没有可记忆的参数。 */
    val isCustom: Boolean get() = this != Default

    companion object {
        fun fromValue(value: String?): CardMaterialType = entries.firstOrNull { it.value == value } ?: Default
    }
}

internal enum class CardMaterialState { Big, Small, Expand }

internal data class CardMaterialConfig(
    val type: CardMaterialType = CardMaterialType.Default,
    val blur: Int = 35,
    val softLight: Double = 0.0,
    val saturation: Double = 0.0,
    val brightness: Double = 0.0,
    val softDarker: Double = 0.0,
    val transparency: Double = -0.57,
    val burn: Double = 0.0,
    val softRefraction: Double = 0.0,
    val softEdgeThickness: Double = 0.8,
    val softShapeEdgeWidth: Double = 0.0,
    val softShapeEdgePower: Double = 0.0,
    val softReflectionLighten: Double = 0.0,
    val softReflection: Double = 0.0,
    val directionalLightIntensity: Double = 1.0,
    val backgroundSaturation: Double = 0.0,
    val backgroundBrightness: Double = 0.04,
    val refraction: Int = 16,
    val edgeThickness: Int = 16,
    val reflectionStrength: Int = 42,
    val darker: Int = 14,
    val lightDirection: Int = 243,
    val dispersion: Int = 18,
    val blendColor: String = "#FFFFFF",
    val blendOpacity: Int = 0,
    val highlight: Boolean = true,
) {
    val isCustom: Boolean get() = type.isCustom

    fun toJson(): JSONObject = JSONObject().apply {
        put("type", type.value)
        put("blur", blur)
        put("softSchema", 3)
        put("softLight", softLight)
        put("saturation", saturation)
        put("brightness", brightness)
        put("softDarker", softDarker)
        put("transparency", transparency)
        put("burn", burn)
        put("softRefraction", softRefraction)
        put("softEdgeThickness", softEdgeThickness)
        put("softShapeEdgeWidth", softShapeEdgeWidth)
        put("softShapeEdgePower", softShapeEdgePower)
        put("softReflectionLighten", softReflectionLighten)
        put("softReflection", softReflection)
        put("directionalLightIntensity", directionalLightIntensity)
        put("backgroundSaturation", backgroundSaturation)
        put("backgroundBrightness", backgroundBrightness)
        put("refraction", refraction)
        put("edgeThickness", edgeThickness)
        put("reflectionStrength", reflectionStrength)
        put("darker", darker)
        put("lightDirection", lightDirection)
        put("dispersion", dispersion)
        put("blendColor", blendColor)
        put("blendOpacity", blendOpacity)
        put("highlight", highlight)
    }

    companion object {
        fun fromJson(json: JSONObject): CardMaterialConfig {
            fun integer(key: String, fallback: Int, range: IntRange): Int =
                json.optInt(key, fallback).coerceIn(range)
            fun decimal(key: String, fallback: Double): Double =
                json.optDouble(key, fallback).coerceIn(-50.0, 50.0)
            val softSchema = json.optInt("softSchema", 0)
            val type = CardMaterialType.fromValue(json.optString("type", "default"))
            fun soft(key: String, fallback: Double): Double =
                if (softSchema >= 2) decimal(key, fallback) else fallback
            return CardMaterialConfig(
                type = type,
                blur = integer("blur", 35, 0..100),
                softLight = soft("softLight", 0.0),
                saturation = if (softSchema >= 3) soft("saturation", 0.0) else 0.0,
                brightness = if (softSchema >= 3) soft("brightness", 0.0) else 0.0,
                softDarker = if (softSchema >= 3) soft("softDarker", 0.0) else 0.0,
                transparency = soft("transparency", -0.57),
                burn = soft("burn", 0.0),
                softRefraction = soft("softRefraction", 0.0),
                softEdgeThickness = soft("softEdgeThickness", 0.8),
                softShapeEdgeWidth = soft("softShapeEdgeWidth", 0.0),
                softShapeEdgePower = soft("softShapeEdgePower", 0.0),
                softReflectionLighten = soft("softReflectionLighten", 0.0),
                softReflection = soft("softReflection", 0.0),
                directionalLightIntensity = soft("directionalLightIntensity", 1.0),
                backgroundSaturation = soft("backgroundSaturation", 0.0),
                backgroundBrightness = soft("backgroundBrightness", 0.04),
                refraction = integer("refraction", 16, 0..40),
                edgeThickness = integer("edgeThickness", 16, 4..40),
                reflectionStrength = integer("reflectionStrength", 42, 0..100),
                darker = integer("darker", 14, 0..100),
                lightDirection = integer("lightDirection", 243, 0..359),
                dispersion = integer("dispersion", 18, 0..100),
                blendColor = json.optString("blendColor", "#FFFFFF").trim(),
                blendOpacity = if (softSchema < 2 && type == CardMaterialType.SoftGlass) 0
                    else integer("blendOpacity", 0, 0..100),
                highlight = json.optBoolean("highlight", true),
            )
        }

        fun decode(raw: String): CardMaterialConfig = runCatching {
            fromJson(JSONObject(raw))
        }.getOrDefault(CardMaterialConfig())

        /**
         * 某个类型**首次**被选中时的起始参数，沿用历史切换行为：
         * 柔光给纯默认；高斯 / 高光 / 液体给一组立刻可见的模糊底色；默认类型不带自定义参数。
         *
         * 已有记忆槽时不走这里（见 [MaterialDraftStore]），只有从没调过该材质才用种子值。
         */
        fun seed(type: CardMaterialType): CardMaterialConfig = when (type) {
            CardMaterialType.Default, CardMaterialType.SoftGlass -> CardMaterialConfig(type = type)
            else -> CardMaterialConfig(type = type, blur = 80, blendColor = "#FFFFFF", blendOpacity = 13)
        }
    }
}

internal data class CardMaterialSettings(
    val big: CardMaterialConfig,
    val small: CardMaterialConfig,
    val expand: CardMaterialConfig,
    val smallFollowBig: Boolean,
    val expandFollowBig: Boolean,
) {
    fun config(state: CardMaterialState): CardMaterialConfig = when (state) {
        CardMaterialState.Big -> big
        CardMaterialState.Small -> small
        CardMaterialState.Expand -> expand
    }

    fun effective(state: CardMaterialState): CardMaterialConfig = when (state) {
        CardMaterialState.Big -> big
        CardMaterialState.Small -> if (smallFollowBig) big else small
        CardMaterialState.Expand -> if (expandFollowBig) big else expand
    }

    fun followsBig(state: CardMaterialState): Boolean = when (state) {
        CardMaterialState.Big -> false
        CardMaterialState.Small -> smallFollowBig
        CardMaterialState.Expand -> expandFollowBig
    }
}
