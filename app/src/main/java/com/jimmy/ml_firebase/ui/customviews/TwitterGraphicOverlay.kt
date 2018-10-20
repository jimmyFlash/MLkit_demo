

package com.jimmy.ml_firebase.ui.customviews

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import com.jimmy.ml_firebase.ui.graphics.TextGraphic
import java.util.*

/**
 * Adapted from the ML Kit Google code lab
 * https://codelabs.developers.google.com/codelabs/mlkit-android/#0
 *
 *  custom view which extends the View class
 */

class TwitterGraphicOverlay(context: Context, attrs: AttributeSet) : View(context, attrs) {

  /**
   * Any()
   *
   * The root of the Kotlin class hierarchy. Every Kotlin class has Any as a superclass, just like Object is the
   * base class in any  OOP class
   */
  private val lock = Any()
  private val graphics = HashSet<Graphic>()
  private val handles = mutableListOf<Handle>()

  // static initializer
  init {
    //Register a callback to be invoked when a touch event is sent to this view.
    setOnTouchListener { _, event ->
      openTwitterIfProfileClicked(event.x, event.y)
    }
  }

  /**
   * handler for figuring out the x,y of touch event
   */
  private fun openTwitterIfProfileClicked(x: Float, y: Float): Boolean {
    /*
     search the list of handles for one that has bounding box containing the coordinates of the touch event
     returns false if no match found, otherwise invokes the helper method to open twitter handle
      */
    return handles.find { it.boundingBox?.contains(x.toInt(), y.toInt()) ?: false }?.let {
      openTwitterProfile(it.text) // call helpere method to open twitter uri and return true
      true } ?: run {
      false // call the default value return of false if not found
    }
  }


  /**
   * helper method to open twitter in browser or app if found
   */
  private fun openTwitterProfile(handle: String) {
    val url = "https://twitter.com/" + handle.trim().removePrefix("@")
    val browserIntent = Intent(Intent.ACTION_VIEW,
        Uri.parse(url))
    context.startActivity(browserIntent)
  }

  /**
   * Base class for a custom graphics object to be rendered within the graphic overlay. Subclass
   * this and implement the [Graphic.draw] method to define the graphics element. Add
   * instances to the overlay using [TwitterGraphicOverlay.add].
   */
  abstract class Graphic(private val overlay: TwitterGraphicOverlay) {

    /**
     * Draw the graphic on the supplied canvas. Drawing should use the following methods to convert
     * to view coordinates for the graphics that are drawn:
     *
     * @param canvas drawing canvas
     */
    abstract fun draw(canvas: Canvas)

    fun postInvalidate() {
      overlay.postInvalidate()
    }
  }

  /**
   * Removes all graphics from the overlay.
   */
  fun clear() {
    synchronized(lock) {
      graphics.clear()
    }
    postInvalidate()
  }

  /**
   * Adds a graphic to the overlay.
   */
  private fun add(graphic: Graphic) {
    synchronized(lock) {
      graphics.add(graphic)
    }
    postInvalidate()
  }

  // inner inline class
  class Handle(val text: String, val boundingBox: Rect?)

  fun addText(text: String, boundingBox: Rect?) {
    add(TextGraphic(this, boundingBox))
    handles.add(Handle(text, boundingBox))
  }

  fun addBox(boundingBox: Rect?) {
    add(TextGraphic(this, boundingBox, Color.RED))
  }

  /**
   * Draws the overlay with its associated graphic objects.
   */
  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)

    synchronized(lock) {
      for (graphic in graphics) {
        graphic.draw(canvas)
      }
    }
  }


}