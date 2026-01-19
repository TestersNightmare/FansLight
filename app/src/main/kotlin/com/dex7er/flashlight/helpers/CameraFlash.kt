package com.dex7er.flashlight.helpers

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.dex7er.flashlight.extensions.config
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class CameraFlash(
    private val context: Context,
    private var cameraTorchListener: CameraTorchListener? = null,
) {
    private val manager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private val scope = CoroutineScope(Dispatchers.Default)

    // 定义常量，避免引用缺失
    companion object {
        private const val MIN_BRIGHTNESS_LEVEL = 1
        private const val DEFAULT_BRIGHTNESS_LEVEL = 100
    }

    private val torchCallback = object : CameraManager.TorchCallback() {
        override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
            // 确保只处理主摄像头的状态
            if (this@CameraFlash.cameraId == cameraId) {
                cameraTorchListener?.onTorchEnabled(enabled)
            }
        }

        override fun onTorchModeUnavailable(cameraId: String) {
            if (this@CameraFlash.cameraId == cameraId) {
                cameraTorchListener?.onTorchUnavailable()
            }
        }
    }

    private val cameraId: String = try {
        manager.cameraIdList.firstOrNull { id ->
            manager.getCameraCharacteristics(id).get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
        } ?: "0"
    } catch (e: Exception) {
        e.printStackTrace()
        "0" // 确保 catch 块也返回一个字符串
    }

    fun toggleFlashlight(enable: Boolean) {
        try {
            // Android 13+ 支持亮度调节，且开启时
            if (supportsBrightnessControl() && enable) {
                val brightnessLevel = getCurrentBrightnessLevel()
                changeTorchBrightness(brightnessLevel)
            } else {
                manager.setTorchMode(cameraId, enable)
            }
        } catch (e: Exception) {
            scope.launch {
                // 确保 MyCameraImpl 里的这个 Flow 是存在的
                MyCameraImpl.cameraError.emit(Unit)
            }
            Toast.makeText(context, "Camera error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun changeTorchBrightness(level: Int) {
        // 使用原生 Build 版本判断替代 isTiramisuPlus()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            try {
                manager.turnOnTorchWithStrengthLevel(cameraId, level)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getMaximumBrightnessLevel(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            try {
                val characteristics = manager.getCameraCharacteristics(cameraId)
                characteristics.get(CameraCharacteristics.FLASH_INFO_STRENGTH_MAXIMUM_LEVEL) ?: MIN_BRIGHTNESS_LEVEL
            } catch (e: Exception) {
                MIN_BRIGHTNESS_LEVEL
            }
        } else {
            MIN_BRIGHTNESS_LEVEL
        }
    }

    fun supportsBrightnessControl(): Boolean {
        return getMaximumBrightnessLevel() > MIN_BRIGHTNESS_LEVEL
    }

    fun getCurrentBrightnessLevel(): Int {
        var brightnessLevel = context.config.brightnessLevel
        // 如果是默认值或者是初次使用，尝试获取最大亮度
        if (brightnessLevel == DEFAULT_BRIGHTNESS_LEVEL || brightnessLevel <= 0) {
            brightnessLevel = getMaximumBrightnessLevel()
        }
        return brightnessLevel
    }

    fun initialize() {
        // 使用 Looper.getMainLooper() 确保回调在主线程
        manager.registerTorchCallback(torchCallback, Handler(Looper.getMainLooper()))
    }

    fun unregisterListeners() {
        manager.unregisterTorchCallback(torchCallback)
    }

    fun release() {
        cameraTorchListener = null
    }
}
