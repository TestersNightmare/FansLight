package com.dex7er.flashlight.extensions

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import com.dex7er.flashlight.helpers.*

val Context.config: Config get() = Config.newInstance(applicationContext)

// 在 extensions 下的某个文件中
fun Context.updateWidgets(isEnabled: Boolean) {
    val intent = Intent(this, MyWidgetTorchProvider::class.java).apply {
        action = TOGGLE_WIDGET_UI
        putExtra(IS_ENABLED, isEnabled)
    }
    sendBroadcast(intent)
    updateBrightDisplayWidget()
}


fun Context.updateBrightDisplayWidget() {
    val widgetIDs = AppWidgetManager.getInstance(applicationContext)?.getAppWidgetIds(ComponentName(applicationContext, MyWidgetBrightDisplayProvider::class.java)) ?: return
    if (widgetIDs.isNotEmpty()) {
        Intent(applicationContext, MyWidgetBrightDisplayProvider::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIDs)
            sendBroadcast(this)
        }
    }
}

fun Context.drawableToBitmap(drawable: Drawable): Bitmap {
    val size = (60 * resources.displayMetrics.density).toInt()
    val mutableBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(mutableBitmap)
    drawable.setBounds(0, 0, size, size)
    drawable.draw(canvas)
    return mutableBitmap
}
