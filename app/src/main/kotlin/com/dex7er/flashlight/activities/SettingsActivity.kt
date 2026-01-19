package com.dex7er.flashlight.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.dex7er.flashlight.extensions.config
import com.dex7er.flashlight.extensions.startAboutActivity
import com.dex7er.flashlight.screens.GeneralSettingsSection
import com.dex7er.flashlight.screens.SettingsScreen
import java.util.Locale
import kotlin.system.exitProcess

class SettingsActivity : ComponentActivity() {
    private val preferences by lazy { config }

    companion object {
        const val RESULT_OPEN_ABOUT = 1002
        const val IS_CUSTOMIZING_COLORS = "is_customizing_colors"
    }

    // SettingsActivity.kt 内部

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                // 确保 SettingsScreen 只接收它现在拥有的参数
                SettingsScreen(
                    generalSection = {
                        GeneralSettingsSection(
                            turnFlashlightOnStartupChecked = config.turnFlashlightOn,
                            forcePortraitModeChecked = config.forcePortraitMode,
                            showBrightDisplayButtonChecked = config.brightDisplay,
                            showSosButtonChecked = config.sos,
                            showStroboscopeButtonChecked = config.stroboscope,
                            onTurnFlashlightOnStartupPress = {
                                config.turnFlashlightOn = it
                                // 重新触发 UI 刷新（如果需要）
                            },
                            onForcePortraitModePress = { config.forcePortraitMode = it },
                            onShowBrightDisplayButtonPress = { config.brightDisplay = it },
                            onShowSosButtonPress = { config.sos = it },
                            onShowStroboscopeButtonPress = { config.stroboscope = it },
                            onAboutPress = {
                                // 调用我们之前在 Activity 扩展里修复的 startAboutActivity
                                startAboutActivity()
                            }
                        )
                    },
                    goBack = { finish() }
                )
            }
        }
    }
}
