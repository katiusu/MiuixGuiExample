package com.katiusu.islandexample.compose.service

import android.content.Context
import android.util.Log

/**
 * 岛通知预览入口。
 *
 * 本模板不包含任何 hook / Xposed / Root 传输层，因此这里只保留与界面完全一致的调用入口，
 * 通知不会真的投递到系统 UI 岛屿。
 */
internal object TestNotificationService {
    private const val TAG = "IslandExample"

    fun sendWelcome(context: Context) {
        Log.i(TAG, "sendWelcome: island transport unavailable in this template")
    }

    fun sendDefault(context: Context) {
        Log.i(TAG, "sendDefault: island transport unavailable in this template")
    }

    fun sendCustom(
        context: Context,
        title: String,
        content: String,
        clearPrevious: Boolean,
        enableFloat: Boolean,
        expandFirst: Boolean = false,
    ) {
        Log.i(TAG, "sendCustom: title=\$title content=\$content")
    }
}
