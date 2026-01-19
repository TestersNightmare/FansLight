package com.dex7er.flashlight.screens

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Box


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dex7er.flashlight.R
import com.dex7er.flashlight.activities.BrightDisplayActivity
import com.dex7er.flashlight.activities.MainActivity


@Composable
private fun BottomBarIcon(
    painter: Painter,
    contentDescription: String,
    onClick: () -> Unit,
    tint: Color = Color.Unspecified,
    buttonSize: Dp = 64.dp,
    iconSize: Dp = 48.dp
) {
    Box(
        modifier = Modifier
            .size(buttonSize)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painter,
            contentDescription = contentDescription,
            modifier = Modifier.size(iconSize),
            tint = if (tint == Color.Unspecified) MaterialTheme.colorScheme.onSurface else tint
        )
    }
}

@Composable
internal fun MainScreen(
    flashlightButton: @Composable () -> Unit,
    slidersSection: @Composable () -> Unit,
    openSettings: () -> Unit,
    viewModel: MainActivity.MainViewModel
) {
    val flashlightActive by viewModel.flashlightOn.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    // 统一颜色逻辑
    val activeTint = if (flashlightActive) Color.White else MaterialTheme.colorScheme.primary
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (flashlightActive) Color.Black else MaterialTheme.colorScheme.surface)

    ) {
        Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                flashlightButton()
            }

            // 2. 调节条区域
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center) 
                    .padding(top = 260.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                slidersSection()
            }

            // 3. 底部导航栏
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding() 
                    .padding(bottom = 24.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomBarIcon(
                    painter = rememberVectorPainter(Icons.Default.Settings),
                    contentDescription = stringResource(R.string.settings),
                    tint = activeTint,
                    iconSize = 44.dp,
                    onClick = openSettings
                )

                BottomBarIcon(
                    painter = painterResource(R.drawable.ic_bright_display_vector),
                    contentDescription = stringResource(R.string.bright_display),
                    tint = activeTint,
                    iconSize = 40.dp,
                    onClick = {
                        context.startActivity(Intent(context, BrightDisplayActivity::class.java))
                    }
                )

                BottomBarIcon(
                    painter = painterResource(R.drawable.ic_sos_vector),
                    contentDescription = stringResource(R.string.sos),
                    tint = activeTint,
                    iconSize = 33.dp,
                    onClick = { viewModel.toggleSos() }
                )

                BottomBarIcon(
                    painter = painterResource(R.drawable.ic_stroboscope_vector),
                    contentDescription = stringResource(R.string.stroboscope),
                    tint = activeTint,
                    iconSize = 40.dp,
                    onClick = { viewModel.toggleStroboscope() }
                )
            }
        }
    }

@Composable
internal fun FlashlightButton(
    flashlightActive: Boolean,
    onFlashlightPress: () -> Unit,
) {
    Icon(
        imageVector = Icons.Default.PowerSettingsNew,
        contentDescription = stringResource(R.string.app_name),
        modifier = Modifier
            .size(180.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onFlashlightPress
            ),
        tint = if (flashlightActive) Color.White else MaterialTheme.colorScheme.primary
    )
}

@Composable
internal fun MainScreenSlidersSection(
    showBrightnessBar: Boolean,
    brightnessBarValue: Float,
    onBrightnessBarValueChange: (Float) -> Unit,
    showStroboscopeBar: Boolean,
    stroboscopeBarValue: Float,
    onStroboscopeBarValueChange: (Float) -> Unit,
) {
    val sliderModifier = Modifier
        .padding(horizontal = 32.dp)
        .padding(vertical = 8.dp)
        .fillMaxWidth()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (showBrightnessBar) {
            Text("", color = Color.Gray, modifier = Modifier.padding(start = 32.dp).align(Alignment.Start))
            Slider(
                modifier = sliderModifier,
                value = brightnessBarValue,
                onValueChange = onBrightnessBarValueChange
            )
        }

        if (showStroboscopeBar) {
            Text("", color = Color.Gray, modifier = Modifier.padding(start = 32.dp).align(Alignment.Start))
            Slider(
                modifier = sliderModifier,
                value = stroboscopeBarValue,
                onValueChange = onStroboscopeBarValueChange
            )
        }
    }
}
