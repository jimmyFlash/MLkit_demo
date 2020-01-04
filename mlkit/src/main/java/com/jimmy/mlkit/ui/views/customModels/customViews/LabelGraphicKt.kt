package com.jimmy.mlkit.ui.views.customModels.customViews

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

/** GraphicKt instance for rendering image labels. */
class LabelGraphicKt(overlay : GraphicOverlayKt,
                     val labels : ArrayList<String> ) : GraphicOverlayKt.GraphicKt(overlay ){

    private var textPaint: Paint = Paint()


    init {
        textPaint.color = Color.WHITE
        textPaint.textSize = 60.0f
    }

    override fun draw(canvas: Canvas) {
        val x = overlay.width / 4.0f
        var y = overlay.height / 4.0f

        for (label in labels) {
            canvas.drawText(label, x, y, textPaint)
            y -= 62.0f
        }
    }

}