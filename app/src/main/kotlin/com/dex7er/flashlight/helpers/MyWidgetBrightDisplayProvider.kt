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
import com.dex7er.flashlight.activities.BrightDisplayActivity
import com.dex7er.flashlight.extensions.config

class MyWidgetBrightDisplayProvider : AppWidgetProvider() {
    private val OPEN_APP_INTENT_ID = 1

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val ids = appWidgetManager.getAppWidgetIds(getComponentName(context))
        ids.forEach { widgetId ->
            RemoteViews(context.packageName, R.layout.widget_bright_display).apply {
                setupAppOpenIntent(context, this)

                val selectedColor = context.config.widgetBgColor
                // 从保存的颜色值中提取 Alpha
                val alpha = Color.alpha(selectedColor)

                val bmp = getColoredIcon(context, selectedColor, alpha)
                setImageViewBitmap(R.id.bright_display_btn, bmp)

                appWidgetManager.updateAppWidget(widgetId, this)
            }
        }
    }

    private fun getComponentName(context: Context) = ComponentName(context, MyWidgetBrightDisplayProvider::class.java)

    private fun setupAppOpenIntent(context: Context, views: RemoteViews) {
        Intent(context, BrightDisplayActivity::class.java).apply {
            val pendingIntent = PendingIntent.getActivity(
                context,
                OPEN_APP_INTENT_ID,
                this,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.bright_display_btn, pendingIntent)
        }
    }

    // --- 核心修复：原生实现图标着色 ---
    private fun getColoredIcon(context: Context, color: Int, alpha: Int): Bitmap {
        val drawable = ContextCompat.getDrawable(context, R.drawable.ic_bright_display_vector)!!
        val wrappedDrawable = DrawableCompat.wrap(drawable.mutate())

        // 应用颜色和透明度
        DrawableCompat.setTint(wrappedDrawable, color)
        DrawableCompat.setTintMode(wrappedDrawable, PorterDuff.Mode.SRC_IN)
        wrappedDrawable.alpha = alpha

        return drawableToBitmap(wrappedDrawable)
    }

    // --- 辅助：将 Drawable 转换为 Bitmap ---
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
