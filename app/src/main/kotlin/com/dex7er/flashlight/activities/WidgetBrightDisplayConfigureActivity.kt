package com.dex7er.flashlight.activities

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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
import com.dex7er.flashlight.helpers.MyWidgetBrightDisplayProvider
import com.dex7er.flashlight.screens.WidgetConfigureScreen

// 定义缺失的常量


class WidgetBrightDisplayConfigureActivity : ComponentActivity() {
    private val viewModel by viewModels<WidgetConfigureViewModel>()
    private val IS_CUSTOMIZING_COLORS = "is_customizing_colors"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. 启用原生沉浸式状态栏
        enableEdgeToEdge()
        setResult(Activity.RESULT_CANCELED)

        val isCustomizingColors = intent.extras?.getBoolean(IS_CUSTOMIZING_COLORS) ?: false
        val widgetId = intent.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID) ?: AppWidgetManager.INVALID_APPWIDGET_ID
        viewModel.setWidgetId(widgetId)

        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID && !isCustomizingColors) {
            finish()
        }

        setContent {
            // 2. 使用标准 MaterialTheme 替换 AppTheme
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val widgetColor by viewModel.widgetColor.collectAsStateWithLifecycle()
                    val widgetAlpha by viewModel.widgetAlpha.collectAsStateWithLifecycle()

                    // 3. 简单的颜色选择逻辑（如果需要高级颜色选择器，建议集成第三方或自定义简易版）
                    var showColorDialog by remember { mutableStateOf(false) }

                    WidgetConfigureScreen(
                        widgetDrawable = R.drawable.ic_bright_display_vector,
                        widgetColor = widgetColor,
                        widgetAlpha = widgetAlpha,
                        onSliderChanged = viewModel::changeAlpha,
                        onColorPressed = {
                            // 原本调用 colorPickerDialogState.show()
                            // 暂时用 Toast 代替，除非你已经有了自定义的 ColorPicker
                            showColorDialog = true
                        },
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

    // 4. 一个简易的颜色确认对话框，替代缺失的 ColorPickerAlertDialog
    @Composable
    private fun SimpleColorPickerDialog(
        initialColor: Int,
        onDismiss: () -> Unit,
        onConfirm: (Int) -> Unit
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Choose Widget Color") },
            text = { Text("Color picking requires a custom implementation. Confirm to use primary theme color?") },
            confirmButton = {
                TextButton(onClick = { onConfirm(initialColor) }) { Text("Confirm") }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text("Cancel") }
            }
        )
    }

    private fun saveConfig() {
        // 修正：将选中的颜色保存到本地配置
        config.widgetBgColor = viewModel.widgetColor.value
        requestWidgetUpdate()

        val resultValue = Intent().apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, viewModel.widgetId.value)
        }
        setResult(Activity.RESULT_OK, resultValue)
        finish()
    }

    private fun requestWidgetUpdate() {
        val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, this, MyWidgetBrightDisplayProvider::class.java).apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(viewModel.widgetId.value))
        }
        sendBroadcast(intent)
    }
}
