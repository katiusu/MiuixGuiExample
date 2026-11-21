package com.katiusu.miuixgui.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.katiusu.miuixgui.example.data.AppSettings
import com.katiusu.miuixgui.example.ui.theme.AppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val settings = AppSettings(applicationContext)
        setContent {
            AppTheme(themeMode = settings.themeMode) {
                AppShell(settings = settings)
            }
        }
    }
}
