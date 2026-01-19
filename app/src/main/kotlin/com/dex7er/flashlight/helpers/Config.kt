package com.dex7er.flashlight.helpers

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color

class Config(context: Context) {
    // 使用原生的 SharedPreferences
    private val prefs: SharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    companion object {
        fun newInstance(context: Context) = Config(context)

        const val DEFAULT_BRIGHTNESS_LEVEL = 100

        // --- 统一在这里定义所有的 Key 常量，避免重复声明冲突 ---
        private const val BRIGHT_DISPLAY = "bright_display"
        private const val STROBOSCOPE = "stroboscope"
        private const val SOS = "sos"
        private const val TURN_FLASHLIGHT_ON = "turn_flashlight_on"
        private const val STROBOSCOPE_PROGRESS = "stroboscope_progress"
        private const val STROBOSCOPE_FREQUENCY = "stroboscope_frequency"
        private const val BRIGHT_DISPLAY_COLOR = "bright_display_color"
        private const val FORCE_PORTRAIT_MODE = "force_portrait_mode"
        private const val BRIGHTNESS_LEVEL = "brightness_level"
        private const val LAST_SLEEP_TIMER_SECONDS = "last_sleep_timer_seconds"
        private const val SLEEP_IN_TS = "sleep_in_ts"
        private const val USE_ENGLISH = "use_english"
        private const val WAS_USE_ENGLISH_TOGGLED = "was_use_english_toggled"
        private const val WIDGET_BG_COLOR = "widget_bg_color"
    }

    // --- 属性定义 ---

    var widgetBgColor: Int
        get() = prefs.getInt(WIDGET_BG_COLOR, Color.BLACK)
        set(value) = prefs.edit().putInt(WIDGET_BG_COLOR, value).apply()

    var useEnglish: Boolean
        get() = prefs.getBoolean(USE_ENGLISH, false)
        set(value) = prefs.edit().putBoolean(USE_ENGLISH, value).apply()

    var wasUseEnglishToggled: Boolean
        get() = prefs.getBoolean(WAS_USE_ENGLISH_TOGGLED, false)
        set(value) = prefs.edit().putBoolean(WAS_USE_ENGLISH_TOGGLED, value).apply()

    var brightDisplay: Boolean
        get() = prefs.getBoolean(BRIGHT_DISPLAY, true)
        set(value) = prefs.edit().putBoolean(BRIGHT_DISPLAY, value).apply()

    var stroboscope: Boolean
        get() = prefs.getBoolean(STROBOSCOPE, true)
        set(value) = prefs.edit().putBoolean(STROBOSCOPE, value).apply()

    var sos: Boolean
        get() = prefs.getBoolean(SOS, true)
        set(value) = prefs.edit().putBoolean(SOS, value).apply()

    var turnFlashlightOn: Boolean
        get() = prefs.getBoolean(TURN_FLASHLIGHT_ON, false)
        set(value) = prefs.edit().putBoolean(TURN_FLASHLIGHT_ON, value).apply()

    var stroboscopeProgress: Int
        get() = prefs.getInt(STROBOSCOPE_PROGRESS, 1000)
        set(value) = prefs.edit().putInt(STROBOSCOPE_PROGRESS, value).apply()

    var stroboscopeFrequency: Long
        get() = prefs.getLong(STROBOSCOPE_FREQUENCY, 1000L)
        set(value) = prefs.edit().putLong(STROBOSCOPE_FREQUENCY, value).apply()

    var brightDisplayColor: Int
        get() = prefs.getInt(BRIGHT_DISPLAY_COLOR, Color.WHITE)
        set(value) = prefs.edit().putInt(BRIGHT_DISPLAY_COLOR, value).apply()

    var forcePortraitMode: Boolean
        get() = prefs.getBoolean(FORCE_PORTRAIT_MODE, true)
        set(value) = prefs.edit().putBoolean(FORCE_PORTRAIT_MODE, value).apply()

    var brightnessLevel: Int
        get() = prefs.getInt(BRIGHTNESS_LEVEL, DEFAULT_BRIGHTNESS_LEVEL)
        set(value) = prefs.edit().putInt(BRIGHTNESS_LEVEL, value).apply()

    var lastSleepTimerSeconds: Int
        get() = prefs.getInt(LAST_SLEEP_TIMER_SECONDS, 30 * 60)
        set(value) = prefs.edit().putInt(LAST_SLEEP_TIMER_SECONDS, value).apply()

    var sleepInTS: Long
        get() = prefs.getLong(SLEEP_IN_TS, 0)
        set(value) = prefs.edit().putLong(SLEEP_IN_TS, value).apply()
}
