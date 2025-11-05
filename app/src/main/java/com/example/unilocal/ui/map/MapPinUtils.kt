package com.example.unilocal.ui.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Bitmap.Config
import android.graphics.Color as AndroidColor
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable

/**
 * Utilitario para crear un Drawable tipo pin usado en varias pantallas.
 * color: entero ARGB
 * sizeDp: tamaño aproximado en dp
 */
fun createPlacePinDrawable(context: Context, color: Int, sizeDp: Int = 44): Drawable {
    val scale = context.resources.displayMetrics.density
    val w = (sizeDp * scale).toInt()
    val h = (sizeDp * 1.2f * scale).toInt()
    val bitmap = Bitmap.createBitmap(w, h, Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    paint.style = Paint.Style.FILL
    paint.color = color

    // circle (head)
    val cx = (w / 2).toFloat()
    val cy = (w * 0.36f)
    val radius = (w * 0.28f)
    canvas.drawCircle(cx, cy, radius, paint)

    // triangle tail
    val path = Path()
    path.moveTo(cx - radius * 0.6f, cy + radius * 0.4f)
    path.lineTo(cx + radius * 0.6f, cy + radius * 0.4f)
    path.lineTo(cx, h.toFloat())
    path.close()
    canvas.drawPath(path, paint)

    // white border
    val stroke = Paint(Paint.ANTI_ALIAS_FLAG)
    stroke.style = Paint.Style.STROKE
    stroke.color = AndroidColor.WHITE
    stroke.strokeWidth = (2 * scale)
    canvas.drawCircle(cx, cy, radius, stroke)
    canvas.drawPath(path, stroke)

    return BitmapDrawable(context.resources, bitmap)
}
