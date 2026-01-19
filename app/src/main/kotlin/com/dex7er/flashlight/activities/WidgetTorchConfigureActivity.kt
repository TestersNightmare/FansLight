package com.dex7er.flashlight.activities

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dex7er.flashlight.R
import com.dex7er.flashlight.activities.viewmodel.WidgetConfigureViewModel
import com.dex7er.flashlight.extensions.config
import com.dex7er.flashlight.extensions.updateBrightDisplayWidget
import com.dex7er.flashlight.helpers.MyWidgetTorchProvider
import com.dex7er.flashlight.screens.WidgetConfigureScreen

// 确保常量与 SettingsActivity 一致
private const val IS_CUSTOMIZING_COLORS = "is_customizing_colors"

class WidgetTorchConfigureActivity : ComponentActivity() {
    private val viewModel by viewModels<WidgetConfigureViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. 使用官方方法开启沉浸式
        enableEdgeToEdge()
        setResult(Activity.RESULT_CANCELED)

        val isCustomizingColors = intent.extras?.getBoolean(IS_CUSTOMIZING_COLORS) ?: false
        val widgetId = intent.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID) ?: AppWidgetManager.INVALID_APPWIDGET_ID
        viewModel.setWidgetId(widgetId)

        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID && !isCustomizingColors) {
            finish()
        }

        setContent {
            // 2. 切换到标准 Material3 主题
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val widgetColor by viewModel.widgetColor.collectAsStateWithLifecycle()
                    val widgetAlpha by viewModel.widgetAlpha.collectAsStateWithLifecycle()

                    // 3. 管理简单的对话框显示状态
                    var showColorDialog by remember { mutableStateOf(false) }

                    WidgetConfigureScreen(
                        widgetDrawable = R.drawable.ic_flashlight_vector, // 注意：这里是手电筒图标
                        widgetColor = widgetColor,
                        widgetAlpha = widgetAlpha,
                        onSliderChanged = viewModel::changeAlpha,
                        onColorPressed = { showColorDialog = true },
                        onSavePressed = ::saveConfig
                    )

                    if (showColorDialog) {
                        SimpleColorPickerDialog(
                            initialColor = widgetColor,
                            onDismiss = { showColorDialog = false },
                            onConfirm = { color ->
                                viewModel.updateColor(color)
                                showColorDialog = false
                            }
                        )
                    }
                }
            }
        }
    }

    // 一个简易的替代对话框
    @Composable
    private fun SimpleColorPickerDialog(
        initialColor: Int,
        onDismiss: () -> Unit,
        onConfirm: (Int) -> Unit
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("小部件颜色") },
            text = { Text("颜色选择器需要自定义实现。点击确定应用当前选中颜色。") },
            confirmButton = {
                TextButton(onClick = { onConfirm(initialColor) }) { Text("确定") }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text("取消") }
            }
        )
    }

    private fun saveConfig() {
        // 4. 确保你的 Config.kt 已经按照之前的建议添加了 widgetBgColor 属性
        config.widgetBgColor = viewModel.widgetColor.value
        requestWidgetUpdate()

        val resultValue = Intent().apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, viewModel.widgetId.value)
        }
        setResult(Activity.RESULT_OK, resultValue)
        finish()
    }

    private fun requestWidgetUpdate() {
        // 更新手电筒小部件
        Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, this, MyWidgetTorchProvider::class.java).apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(viewModel.widgetId.value))
            sendBroadcast(this)
        }

        // 调用扩展函数更新亮度部件
        try {
            updateBrightDisplayWidget()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
