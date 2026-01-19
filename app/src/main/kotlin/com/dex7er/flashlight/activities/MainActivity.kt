package com.dex7er.flashlight.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.dex7er.flashlight.R
import com.dex7er.flashlight.extensions.config
import com.dex7er.flashlight.helpers.*
import com.dex7er.flashlight.screens.*
import com.google.android.material.math.MathUtils
import kotlinx.coroutines.flow.*
import com.dex7er.flashlight.extensions.startAboutActivity
import androidx.activity.enableEdgeToEdge
import android.view.WindowInsets
import android.view.WindowInsetsController

// --- 1. 外部工具函数 ---
fun isNougatPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
fun isNougatMR1Plus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1
fun Context.toast(message: String) { Toast.makeText(this, message, Toast.LENGTH_SHORT).show() }
fun Context.toast(@StringRes resId: Int) { Toast.makeText(this, resId, Toast.LENGTH_SHORT).show() }

fun Drawable.convertToBitmap(): Bitmap {
    val bitmap = Bitmap.createBitmap(if (intrinsicWidth > 0) intrinsicWidth else 1, if (intrinsicHeight > 0) intrinsicHeight else 1, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)
    return bitmap
}

// --- 2. Activity 主类 ---
class MainActivity : ComponentActivity() {
    companion object {
        private const val MAX_STROBO_DELAY = 2000L
        private const val MIN_STROBO_DELAY = 10L
    }

    private val shortcutManager: ShortcutManager?
        get() = if (isNougatMR1Plus()) getSystemService(ShortcutManager::class.java) else null

    private val preferences by lazy { config }
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge() 
    
        // 2. 彻底隐藏状态栏（如果你希望完全看不见时间电量）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.attributes.layoutInDisplayCutoutMode = 
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }
        
        setContent {
            MaterialTheme {
                // 设置沉浸式系统状态栏（可选，视你的配置而定）
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    // 权限请求启动器
                    val sosPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { if (it) cameraPermissionGranted(true) else toast(R.string.camera_permission) }
                    val stroboscopePermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { if (it) cameraPermissionGranted(false) else toast(R.string.camera_permission) }

                    // 核心修复：调用简化后的 MainScreen
                    MainScreen(
                        flashlightButton = {
                            val active by viewModel.flashlightOn.collectAsStateWithLifecycle(false)
                            FlashlightButton(
                                onFlashlightPress = { viewModel.toggleFlashlight() },
                                flashlightActive = active
                            )
                        },
                        slidersSection = {
                            val bVisible by viewModel.brightnessBarVisible.collectAsStateWithLifecycle()
                            val bValue by viewModel.brightnessBarValue.collectAsStateWithLifecycle()
                            val sVisible by viewModel.stroboscopeBarVisible.collectAsStateWithLifecycle()
                            val sValue by viewModel.stroboscopeBarValue.collectAsStateWithLifecycle()

                            MainScreenSlidersSection(
                                showBrightnessBar = bVisible,
                                brightnessBarValue = bValue,
                                onBrightnessBarValueChange = viewModel::updateBrightnessBarValue,
                                showStroboscopeBar = sVisible,
                                stroboscopeBarValue = sValue,
                                onStroboscopeBarValueChange = viewModel::updateStroboscopeBarValue,
                            )
                        },
                        openSettings = { this.startAboutActivity() },
                        viewModel = viewModel // 传入 viewModel，MainScreen 内部会自己处理 SOS 和频闪按钮
                    )
                }
            }
        }
    }

    private fun toggleStroboscope(isSOS: Boolean, launcher: ManagedActivityResultLauncher<String, Boolean>) {
        val perm = Manifest.permission.CAMERA
        if (isNougatPlus() || ContextCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_GRANTED) cameraPermissionGranted(isSOS) else launcher.launch(perm)
    }

    private fun cameraPermissionGranted(isSOS: Boolean) = if (isSOS) viewModel.toggleSos() else viewModel.toggleStroboscope()

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
        requestedOrientation = if (preferences.forcePortraitMode) ActivityInfo.SCREEN_ORIENTATION_PORTRAIT else ActivityInfo.SCREEN_ORIENTATION_SENSOR
        checkShortcuts()
    }

    @SuppressLint("NewApi")
    private fun checkShortcuts() {
        if (isNougatMR1Plus()) {
            val color = ContextCompat.getColor(this, R.color.color_primary)
            try { shortcutManager?.dynamicShortcuts = listOf(getBrightDisplayShortcut(color)) } catch (e: Exception) {}
        }
    }

    @SuppressLint("NewApi")
    private fun getBrightDisplayShortcut(color: Int): ShortcutInfo {
        val drawable = ContextCompat.getDrawable(this, R.drawable.shortcut_bright_display)!!
        if (drawable is LayerDrawable) drawable.findDrawableByLayerId(R.id.shortcut_bright_display_background)?.setTint(color)
        val intent = Intent(this, BrightDisplayActivity::class.java).apply { action = Intent.ACTION_VIEW }
        return ShortcutInfo.Builder(this, "bright_display").setShortLabel(getString(R.string.bright_display)).setIcon(Icon.createWithBitmap(drawable.convertToBitmap())).setIntent(intent).build()
    }

    // --- 3. ViewModel ---
    internal class MainViewModel(application: Application) : AndroidViewModel(application) {
        private val prefs = application.config
        private lateinit var camera: MyCameraImpl

        private val _sosActive = MutableStateFlow(false)
        val sosActive = _sosActive.asStateFlow()

        private val _brightnessBarValue = MutableStateFlow(1f)
        val brightnessBarValue = _brightnessBarValue.asStateFlow()

        private val _stroboscopeActive = MutableStateFlow(false)
        val stroboscopeActive = _stroboscopeActive.asStateFlow()

        // 核心修复点：定义 stroboscopeBarVisible 为 StateFlow
        val stroboscopeBarVisible = _stroboscopeActive.asStateFlow()

        private val _stroboscopeBarValue = MutableStateFlow(0.5f)
        val stroboscopeBarValue = _stroboscopeBarValue.asStateFlow()

        val flashlightOn by lazy { camera.flashlightOnFlow }
        val brightnessBarVisible by lazy { flashlightOn.map { it && camera.supportsBrightnessControl() }.stateIn(viewModelScope, SharingStarted.Lazily, false) }

        init {
            camera = MyCameraImpl.newInstance(application, object : CameraTorchListener {
                override fun onTorchEnabled(isEnabled: Boolean) {
                    if (isEnabled && ::camera.isInitialized && camera.supportsBrightnessControl()) {
                        _brightnessBarValue.value = camera.getCurrentBrightnessLevel().toFloat() / camera.getMaximumBrightnessLevel()
                    }
                }
                override fun onTorchUnavailable() {}
            })
            _stroboscopeBarValue.value = (1f - (prefs.stroboscopeFrequency.toFloat() / MAX_STROBO_DELAY)).coerceIn(0f, 1f)
            camera.stroboscopeDisabled.onEach { _stroboscopeActive.value = false }.launchIn(viewModelScope)
            camera.sosDisabled.onEach { _sosActive.value = false }.launchIn(viewModelScope)
            if (prefs.turnFlashlightOn) camera.enableFlashlight()
        }

        fun toggleFlashlight() = camera.toggleFlashlight()
        fun toggleSos() {
            _sosActive.value = camera.toggleSOS()
        }

        // 将 enableStroboscope 修改为 toggleStroboscope
        fun toggleStroboscope() {
            _stroboscopeActive.value = camera.toggleStroboscope()
        }

        fun updateBrightnessBarValue(v: Float) { _brightnessBarValue.value = v; if (::camera.isInitialized) { val level = MathUtils.lerp(1f, camera.getMaximumBrightnessLevel().toFloat(), v).toInt(); camera.updateBrightnessLevel(level); prefs.brightnessLevel = level } }
        fun updateStroboscopeBarValue(v: Float) { _stroboscopeBarValue.value = v; if (::camera.isInitialized) { val freq = MathUtils.lerp(MIN_STROBO_DELAY.toFloat(), MAX_STROBO_DELAY.toFloat(), 1f - v).toLong(); camera.stroboFrequency = freq; prefs.stroboscopeFrequency = freq } }
        fun onResume() { if (::camera.isInitialized) camera.handleCameraSetup() }
        override fun onCleared() { super.onCleared(); if (::camera.isInitialized) camera.releaseCamera() }
    }
}
