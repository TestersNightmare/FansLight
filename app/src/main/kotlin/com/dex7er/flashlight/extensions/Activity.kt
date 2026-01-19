package com.dex7er.flashlight.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.dex7er.flashlight.R
import androidx.appcompat.app.AlertDialog
import android.view.Gravity
import android.widget.TextView

import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.StyleSpan
import android.graphics.Typeface

internal fun Activity.startAboutActivity() {
    val versionName = try {
        val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            packageManager.getPackageInfo(packageName, 0)
        }
        packageInfo.versionName ?: "1.0.0"
    } catch (e: Exception) {
        "1.0.0"
    }
    val appName = getString(R.string.app_name)
    val copyright = "© 2026 Dex7er. All rights reserved."

    // 1. 拼接完整文本
    val fullText = "$appName\n\nVersion: $versionName\n\n\n\n$copyright"
    val spannable = SpannableString(fullText)

    spannable.setSpan(
        AbsoluteSizeSpan(24, true),
        0,
        appName.length,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    // 设置加粗
    spannable.setSpan(
        StyleSpan(Typeface.BOLD),
        0,
        appName.length,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    val messageView = TextView(this).apply {
        text = spannable
        gravity = Gravity.CENTER
        setPadding(40, 60, 40, 20)
        setTextColor(android.graphics.Color.BLACK)
    }

    // 4. 构建弹窗
    AlertDialog.Builder(this)
        .setView(messageView)
        .setPositiveButton(android.R.string.ok, null)
        .show()
}

// --- 2. 保持原样：自定义界面暂不可用 ---
internal fun Activity.startCustomizationActivity() {
    Toast.makeText(this, "Customization is currently unavailable", Toast.LENGTH_SHORT).show()
}

// --- 3. 辅助：语言设置 ---
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
internal fun Activity.launchChangeAppLanguageIntent() {
    try {
        val intent = Intent(Settings.ACTION_APP_LOCALE_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    } catch (e: Exception) {
        launchAppDetailsSettings()
    }
}

// --- 4. 辅助：打开应用详情页 ---
internal fun Activity.launchAppDetailsSettings() {
    try {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    } catch (e: Exception) {
        startActivity(Intent(Settings.ACTION_SETTINGS))
    }
}

// --- 5. 辅助：隐藏键盘 ---
internal fun Activity.hideKeyboard() {
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    currentFocus?.let {
        inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
    }
}

// --- 6. 图标与名称获取 ---
fun getAppIconIDs() = arrayListOf(
    R.mipmap.ic_launcher
)

fun Context.launcherName() = getString(R.string.app_launcher_name)
