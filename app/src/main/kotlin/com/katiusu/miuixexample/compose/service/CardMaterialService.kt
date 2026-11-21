package com.katiusu.miuixexample.compose.service

import android.content.Context
import com.katiusu.miuixexample.compose.data.FlutterPrefsRepository
import com.katiusu.miuixexample.compose.data.CardMaterialConfig
import com.katiusu.miuixexample.compose.data.CardMaterialSettings
import com.katiusu.miuixexample.compose.data.CardMaterialState
import com.katiusu.miuixexample.compose.data.CardMaterialType
import com.katiusu.miuixexample.compose.data.MaterialDraftStore
import org.json.JSONObject

internal class CardMaterialService(
    private val prefs: FlutterPrefsRepository,
    context: Context,
) {
    private val drafts = MaterialDraftStore(context)

    fun load(): CardMaterialSettings = CardMaterialSettings(
        big = CardMaterialConfig.decode(prefs.getString(KEY_BIG)),
        small = CardMaterialConfig.decode(prefs.getString(KEY_SMALL)),
        expand = CardMaterialConfig.decode(prefs.getString(KEY_EXPAND)),
        smallFollowBig = prefs.getBoolean(KEY_SMALL_FOLLOW, true),
        expandFollowBig = prefs.getBoolean(KEY_EXPAND_FOLLOW, true),
    )

    fun save(settings: CardMaterialSettings, state: CardMaterialState, config: CardMaterialConfig): CardMaterialSettings {
        if (config.isCustom) clearBackgroundConflict(settings, state)
        val updated = when (state) {
            CardMaterialState.Big -> settings.copy(big = config)
            CardMaterialState.Small -> settings.copy(small = config)
            CardMaterialState.Expand -> settings.copy(expand = config)
        }
        prefs.putString(configKey(state), config.toJson().toString())
        syncLegacy(state, config)
        if (state == CardMaterialState.Big) {
            if (updated.smallFollowBig) syncLegacy(CardMaterialState.Small, config)
            if (updated.expandFollowBig) syncLegacy(CardMaterialState.Expand, config)
        }
        return updated
    }

    /**
     * 切换某个形态的材质类型，并让每种材质各自记住自己的参数。
     *
     * 真源始终只有一份（`pref_card_material_<state>_config`），语义完全不变，
     * Hook 端与同步链路无需任何改动。切走时把当前参数写进本地草稿槽，
     * 切回时优先读槽，只有从没调过该材质才用 [CardMaterialConfig.seed]。
     */
    fun switchType(
        settings: CardMaterialSettings,
        state: CardMaterialState,
        type: CardMaterialType,
    ): CardMaterialSettings {
        val current = settings.config(state)
        if (current.type == type) return settings
        drafts.save(state, current)
        val restored = drafts.load(state, type) ?: CardMaterialConfig.seed(type)
        return save(settings, state, restored)
    }

    fun setFollow(settings: CardMaterialSettings, state: CardMaterialState, follow: Boolean): CardMaterialSettings {
        require(state != CardMaterialState.Big)
        val updated = when (state) {
            CardMaterialState.Small -> settings.copy(smallFollowBig = follow)
            CardMaterialState.Expand -> settings.copy(expandFollowBig = follow)
            CardMaterialState.Big -> settings
        }
        prefs.putBoolean(if (state == CardMaterialState.Small) KEY_SMALL_FOLLOW else KEY_EXPAND_FOLLOW, follow)
        syncLegacy(state, if (follow) updated.big else updated.config(state))
        if (follow && updated.big.isCustom) clearBackgroundConflict(updated, state)
        return updated
    }

    fun reset(): CardMaterialSettings {
        var settings = load()
        CardMaterialState.entries.forEach { state -> settings = save(settings, state, CardMaterialConfig()) }
        settings = setFollow(settings, CardMaterialState.Small, true)
        settings = setFollow(settings, CardMaterialState.Expand, true)
        // 恢复默认后旧草稿必须一起作废，否则下次切材质又会把自定义参数翻出来。
        drafts.clear()
        return settings
    }

    fun export(settings: CardMaterialSettings): String = JSONObject().apply {
        put("type", CLIPBOARD_TYPE)
        put("version", CLIPBOARD_VERSION)
        put("big", settings.big.toJson())
        put("small", settings.small.toJson())
        put("expand", settings.expand.toJson())
        put("smallFollowBig", settings.smallFollowBig)
        put("expandFollowBig", settings.expandFollowBig)
    }.toString(2)

    fun import(raw: String, allowSoftGlass: Boolean): CardMaterialSettings {
        val root = JSONObject(raw)
        require(root.optString("type") == CLIPBOARD_TYPE && root.optInt("version") == CLIPBOARD_VERSION)
        val big = CardMaterialConfig.fromJson(root.getJSONObject("big"))
        val small = CardMaterialConfig.fromJson(root.getJSONObject("small"))
        val expand = CardMaterialConfig.fromJson(root.getJSONObject("expand"))
        require(allowSoftGlass || listOf(big, small, expand).none { it.type == CardMaterialType.SoftGlass }) {
            SOFT_GLASS_UNSUPPORTED
        }
        var settings = load()
        settings = save(settings, CardMaterialState.Big, big)
        settings = save(settings, CardMaterialState.Small, small)
        settings = save(settings, CardMaterialState.Expand, expand)
        settings = setFollow(settings, CardMaterialState.Small, root.getBoolean("smallFollowBig"))
        settings = setFollow(settings, CardMaterialState.Expand, root.getBoolean("expandFollowBig"))
        // 导入是一整套材质参数，本地记忆槽跟着重来，否则切换材质会覆盖刚导入的值。
        drafts.clear()
        return settings
    }

    fun clearUnsupportedSoftGlass(settings: CardMaterialSettings): CardMaterialSettings {
        var updated = settings
        CardMaterialState.entries.forEach { state ->
            if (updated.config(state).type == CardMaterialType.SoftGlass) {
                updated = save(updated, state, CardMaterialConfig())
            }
        }
        // 降级到 HyperOS 3 后柔光已不可选，残留的柔光槽没有意义。
        drafts.clearType(CardMaterialType.SoftGlass)
        return updated
    }

    fun hasBackgroundConflict(settings: CardMaterialSettings, state: CardMaterialState): Boolean = when (state) {
        CardMaterialState.Big -> prefs.getString(KEY_BG_BIG).isNotBlank() ||
            (settings.smallFollowBig && prefs.getString(KEY_BG_SMALL).isNotBlank()) ||
            (settings.expandFollowBig && prefs.getString(KEY_BG_EXPAND).isNotBlank())
        CardMaterialState.Small -> prefs.getString(KEY_BG_SMALL).isNotBlank()
        CardMaterialState.Expand -> prefs.getString(KEY_BG_EXPAND).isNotBlank()
    }

    private fun clearBackgroundConflict(settings: CardMaterialSettings, state: CardMaterialState) {
        fun clear(key: String) { if (prefs.getString(key).isNotBlank()) prefs.remove(key) }
        when (state) {
            CardMaterialState.Big -> {
                clear(KEY_BG_BIG)
                if (settings.smallFollowBig) clear(KEY_BG_SMALL)
                if (settings.expandFollowBig) clear(KEY_BG_EXPAND)
            }
            CardMaterialState.Small -> clear(KEY_BG_SMALL)
            CardMaterialState.Expand -> clear(KEY_BG_EXPAND)
        }
    }

    private fun syncLegacy(state: CardMaterialState, config: CardMaterialConfig) {
        val moduleBlur = config.type in setOf(
            CardMaterialType.Gaussian,
            CardMaterialType.HighlightGlass,
            CardMaterialType.LiquidGlass,
        )
        prefs.putBoolean(legacyKey("blur", state, "enabled"), moduleBlur)
        prefs.putLong(legacyKey("blur", state, "radius"), config.blur.toLong())
        prefs.putString(legacyKey("blur", state, "color"), config.blendColor)
        prefs.putBoolean(
            legacyKey("glass", state, "enabled"),
            config.type == CardMaterialType.HighlightGlass || config.type == CardMaterialType.LiquidGlass,
        )
        prefs.putBoolean(
            "pref_card_refraction_${state.valueName}_enabled",
            config.type == CardMaterialType.LiquidGlass,
        )
    }

    private fun legacyKey(group: String, state: CardMaterialState, field: String): String =
        "pref_card_${group}_${state.valueName}_$field"

    private fun configKey(state: CardMaterialState): String = when (state) {
        CardMaterialState.Big -> KEY_BIG
        CardMaterialState.Small -> KEY_SMALL
        CardMaterialState.Expand -> KEY_EXPAND
    }

    private val CardMaterialState.valueName: String get() = name.lowercase()

    companion object {
        const val SOFT_GLASS_UNSUPPORTED = "soft_glass_unsupported"
        private const val CLIPBOARD_TYPE = "miuixexample_material_config"
        private const val CLIPBOARD_VERSION = 1
        private const val KEY_BIG = "pref_card_material_big_config"
        private const val KEY_SMALL = "pref_card_material_small_config"
        private const val KEY_EXPAND = "pref_card_material_expand_config"
        private const val KEY_SMALL_FOLLOW = "pref_card_material_small_follow_big"
        private const val KEY_EXPAND_FOLLOW = "pref_card_material_expand_follow_big"
        private const val KEY_BG_SMALL = "pref_card_bg_small_path"
        private const val KEY_BG_BIG = "pref_card_bg_big_path"
        private const val KEY_BG_EXPAND = "pref_card_bg_expand_path"
    }
}
