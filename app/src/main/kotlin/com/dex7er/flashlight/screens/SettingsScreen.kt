package com.dex7er.flashlight.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dex7er.flashlight.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsScreen(
    generalSection: @Composable () -> Unit,
    goBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = goBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // 只保留通用设置分组
            Text(
                text = stringResource(id = R.string.settings),
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            generalSection()
        }
    }
}

@Composable
internal fun GeneralSettingsSection(
    turnFlashlightOnStartupChecked: Boolean,
    forcePortraitModeChecked: Boolean,
    showBrightDisplayButtonChecked: Boolean,
    showSosButtonChecked: Boolean,
    showStroboscopeButtonChecked: Boolean,
    onTurnFlashlightOnStartupPress: (Boolean) -> Unit,
    onForcePortraitModePress: (Boolean) -> Unit,
    onShowBrightDisplayButtonPress: (Boolean) -> Unit,
    onShowSosButtonPress: (Boolean) -> Unit,
    onShowStroboscopeButtonPress: (Boolean) -> Unit,
    onAboutPress: () -> Unit,
) {
    // 1. 启动时开启手电筒
    SettingsSwitchItem(
        label = stringResource(id = R.string.turn_flashlight_on),
        checked = turnFlashlightOnStartupChecked,
        onCheckedChange = onTurnFlashlightOnStartupPress
    )

    // 2. 强制竖屏
    SettingsSwitchItem(
        label = stringResource(id = R.string.force_portrait_mode),
        checked = forcePortraitModeChecked,
        onCheckedChange = onForcePortraitModePress
    )

    // 3. 显示屏幕补光按钮
    SettingsSwitchItem(
        label = stringResource(id = R.string.show_bright_display),
        checked = showBrightDisplayButtonChecked,
        onCheckedChange = onShowBrightDisplayButtonPress
    )

    // 4. 显示 SOS 按钮
    SettingsSwitchItem(
        label = stringResource(id = R.string.show_sos),
        checked = showSosButtonChecked,
        onCheckedChange = onShowSosButtonPress
    )

    // 5. 显示频闪按钮
    SettingsSwitchItem(
        label = stringResource(id = R.string.show_stroboscope),
        checked = showStroboscopeButtonChecked,
        onCheckedChange = onShowStroboscopeButtonPress
    )

    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

    // 6. 关于
    ListItem(
        headlineContent = { Text(stringResource(id = R.string.about)) },
        modifier = Modifier.clickable { onAboutPress() }
    )
}

// 辅助组件：带开关的设置行
@Composable
private fun SettingsSwitchItem(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        headlineContent = { Text(label) },
        trailingContent = {
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        },
        modifier = Modifier.clickable { onCheckedChange(!checked) }
    )
}
