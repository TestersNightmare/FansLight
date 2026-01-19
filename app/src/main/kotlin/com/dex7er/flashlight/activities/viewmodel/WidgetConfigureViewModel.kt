package com.dex7er.flashlight.activities.viewmodel

import android.app.Application
import android.content.res.Configuration
import android.graphics.Color
import androidx.lifecycle.AndroidViewModel
import com.dex7er.flashlight.R
import com.dex7er.flashlight.extensions.config
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class WidgetConfigureViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val _widgetAlpha = MutableStateFlow(0f)
    val widgetAlpha = _widgetAlpha.asStateFlow()

    private val _widgetId = MutableStateFlow(0)
    val widgetId = _widgetId.asStateFlow()

    private val _widgetColor = MutableStateFlow(0)
    val widgetColor = _widgetColor.asStateFlow()

    fun changeAlpha(newAlpha: Float) {
        _widgetAlpha.value = newAlpha
    }

    fun updateColor(newColor: Int) {
        _widgetColor.value = newColor
    }

    fun setWidgetId(widgetId: Int) {
        _widgetId.value = widgetId
    }

    init {
        val config = application.config
        val resources = application.resources

        // 1. 获取已保存的背景色
        var initialColor = config.widgetBgColor

        // 2. 替代 isUsingSystemTheme 逻辑
        // 如果当前是默认颜色，我们可以尝试根据系统深色模式调整它
        val isDarkTheme = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

        val defaultColor = resources.getColor(R.color.default_widget_bg_color, null)

        if (initialColor == defaultColor) {
            // 如果你支持 Material You (Android 12+), 这里可以获取系统动态色
            // 否则，我们直接使用你项目中定义的 color_primary
            initialColor = resources.getColor(R.color.color_primary.let { R.color.color_primary }, application.theme)
        }

        _widgetColor.value = initialColor

        // 3. 计算初始透明度
        // Color.alpha 返回 0-255，我们需要将其转换为 0.0-1.0 的 Float
        _widgetAlpha.value = Color.alpha(initialColor) / 255f
    }
}
