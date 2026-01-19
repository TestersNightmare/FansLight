package com.dex7er.flashlight.helpers

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.dex7er.flashlight.R
import com.dex7er.flashlight.extensions.config

class MyWidgetTorchProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        performUpdate(context)
    }

    private fun performUpdate(context: Context) {
        val selectedColor = context.config.widgetBgColor
        val alpha = Color.alpha(selectedColor)

        // 初始状态通常显示为白色（关闭状态）
        val bmp = getColoredIcon(context, Color.WHITE, alpha)
        val intent = Intent(context, MyWidgetTorchProvider::class.java).apply {
            action = TOGGLE
        }

        val appWidgetManager = AppWidgetManager.getInstance(context) ?: return
        val componentName = getComponentName(context)

        appWidgetManager.getAppWidgetIds(componentName).forEach { appWidgetId ->
            val views = RemoteViews(context.packageName, R.layout.widget_torch)

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                appWidgetId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            views.setOnClickPendingIntent(R.id.flashlight_btn, pendingIntent)
            views.setImageViewBitmap(R.id.flashlight_btn, bmp)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    private fun getComponentName(context: Context) = ComponentName(context, MyWidgetTorchProvider::class.java)

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            TOGGLE -> toggleActualFlashlight(context)
            TOGGLE_WIDGET_UI -> updateWidgetUI(context, intent)
            else -> super.onReceive(context, intent)
        }
    }

    private fun toggleActualFlashlight(context: Context) {
        // 调用我们之前修复好的 MyCameraImpl 切换开关
        MyCameraImpl.newInstance(context).toggleFlashlight()
    }

    private fun updateWidgetUI(context: Context, intent: Intent) {
        val enable = intent.getBooleanExtra(IS_ENABLED, false)
        val widgetBgColor = context.config.widgetBgColor
        val alpha = Color.alpha(widgetBgColor)

        // 如果开启，则显示用户设置的颜色；如果关闭，显示白色
        val selectedColor = if (enable) widgetBgColor else Color.WHITE
        val bmp = getColoredIcon(context, selectedColor, alpha)

        val appWidgetManager = AppWidgetManager.getInstance(context) ?: return
        appWidgetManager.getAppWidgetIds(getComponentName(context)).forEach { appWidgetId ->
            val views = RemoteViews(context.packageName, R.layout.widget_torch)
            views.setImageViewBitmap(R.id.flashlight_btn, bmp)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    // --- 核心修复：原生图标着色 ---
    private fun getColoredIcon(context: Context, color: Int, alpha: Int): Bitmap {
        val drawable = ContextCompat.getDrawable(context, R.drawable.ic_flashlight_vector)!!
        val wrappedDrawable = DrawableCompat.wrap(drawable.mutate())

        DrawableCompat.setTint(wrappedDrawable, color)
        DrawableCompat.setTintMode(wrappedDrawable, PorterDuff.Mode.SRC_IN)
        wrappedDrawable.alpha = alpha

        return drawableToBitmap(wrappedDrawable)
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        val bitmap = Bitmap.createBitmap(
            if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 1,
            if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 1,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}
