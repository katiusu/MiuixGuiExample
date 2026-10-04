package com.katiusu.miuixgui.example

import android.app.Application
import com.katiusu.miuixgui.example.bridge.HookStatusStore
import com.katiusu.miuixgui.example.bridge.XposedServiceManager
import com.katiusu.miuixgui.example.prefs.ConfigState
import com.katiusu.miuixgui.example.prefs.OptionRegistry
import com.katiusu.miuixgui.example.prefs.PrefsStore
import com.katiusu.miuixgui.example.ui.screen.features.featureSpecs

class TemplateApp : Application() {

    override fun onCreate() {
        super.onCreate()
        PrefsStore.init(this)
        ConfigState.init(this)
        OptionRegistry.registerAll(featureSpecs())
        HookStatusStore.initialize(this)
        XposedServiceManager.init()
    }
}
