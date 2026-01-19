package com.dex7er.flashlight.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
internal fun BrightDisplayScreen(
    backgroundColor: Int,
    contrastColor: Int,
    onChangeColorPress: () -> Unit,
) {
    // 1. 使用 Box 填充整个屏幕颜色
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(backgroundColor))
                        .clickable(
               
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onChangeColorPress
            )
            .safeDrawingPadding()
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = "", 
            color = Color(contrastColor).copy(alpha = 0.3f), 
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

// 辅助函数：简单的对比度计算
fun calculateContrastColor(backgroundColor: Int): Int {
    val r = android.graphics.Color.red(backgroundColor)
    val g = android.graphics.Color.green(backgroundColor)
    val b = android.graphics.Color.blue(backgroundColor)
    val y = (299 * r + 587 * g + 114 * b) / 1000
    return if (y >= 128) android.graphics.Color.BLACK else android.graphics.Color.WHITE
}

@Preview(showBackground = true)
@Composable
private fun BrightDisplayScreenPreview() {
    MaterialTheme {
        BrightDisplayScreen(
            backgroundColor = Color.White.toArgb(),
            contrastColor = Color.Black.toArgb(),
            onChangeColorPress = {},
        )
    }
}