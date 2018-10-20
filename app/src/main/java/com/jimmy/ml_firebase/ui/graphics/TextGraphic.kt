

package com.jimmy.ml_firebase.ui.graphics


import android.graphics.*
import com.jimmy.ml_firebase.ui.customviews.TwitterGraphicOverlay

/**
 * Graphic instance for rendering TextBlock position, size, and ID within an associated graphic
 * overlay view.
 *
 * Adapted from the ML Kit Google code lab
 * https://codelabs.developers.google.com/codelabs/mlkit-android/#0
 */
class TextGraphic internal constructor(overlay: TwitterGraphicOverlay,
  private val boundingBox: Rect?,
  private val color: Int = Color.BLUE) : TwitterGraphicOverlay.Graphic(overlay) {

  private val rectPaint: Paint = Paint()

  //static initializer
  init {
    // define properties of the bounding box
    rectPaint.color = Color.WHITE
    rectPaint.style = Paint.Style.STROKE
    rectPaint.strokeWidth = STROKE_WIDTH + 2

    // Redraw the overlay, as this graphic has been added.
    postInvalidate()
  }

  /**
   * Draws the text block annotations for position, size, and raw value on the supplied canvas.
   */
  override fun draw(canvas: Canvas) {
    // Draws the bounding box around the TextBlock.
    val rect = RectF(boundingBox)
//    canvas.drawRect(rect, rectPaint)
    rectPaint.color = color
    rectPaint.style = Paint.Style.STROKE
    rectPaint.strokeWidth = STROKE_WIDTH
    canvas.drawRect(rect, rectPaint)
  }

  // static class members are defined here
  companion object {

    private const val STROKE_WIDTH = 4.0f
  }
}
