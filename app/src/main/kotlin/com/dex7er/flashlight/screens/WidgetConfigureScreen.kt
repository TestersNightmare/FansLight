package com.dex7er.flashlight.screens

import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dex7er.flashlight.R
import com.dex7er.flashlight.helpers.AppDimensions

@Composable
internal fun WidgetConfigureScreen(
    @DrawableRes widgetDrawable: Int,
    @ColorInt widgetColor: Int,
    widgetAlpha: Float,
    onSliderChanged: (Float) -> Unit,
    onColorPressed: () -> Unit,
    onSavePressed: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        // 预览区域
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Icon(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(200.dp), // 使用具体数值或 AppDimensions
                painter = painterResource(id = widgetDrawable),
                contentDescription = null,
                // 手动处理 Alpha 透明度，替代 adjustAlpha
                tint = Color(widgetColor).copy(alpha = widgetAlpha)
            )
        }

        // 控制区域
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // 颜色选择按钮（圆形色块）
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(widgetColor))
                        .clickable { onColorPressed() }
                )

                // 透明度滑动条
                Slider(
                    value = widgetAlpha,
                    onValueChange = onSliderChanged,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp)
                        .background(
                            // 替代 com.dex7er.commons.R.color.md_grey_white
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 保存按钮
            Button(
                modifier = Modifier.align(Alignment.End),
                onClick = onSavePressed
            ) {
                // 修复点：改用本地 android.R.string.ok 或自定义字符串
                Text(text = stringResource(id = android.R.string.ok))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WidgetConfigureScreenPreview() {
    MaterialTheme {
        WidgetConfigureScreen(
            widgetDrawable = R.drawable.ic_bright_display_vector,
            widgetColor = android.graphics.Color.BLUE,
            widgetAlpha = 0.5f,
            onSliderChanged = {},
            onColorPressed = {},
            onSavePressed = {}
        )
    }
}
