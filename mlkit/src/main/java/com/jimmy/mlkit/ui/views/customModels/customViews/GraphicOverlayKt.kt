package com.jimmy.mlkit.ui.views.customModels.customViews

import android.content.Context
import android.graphics.Canvas
import android.hardware.camera2.CameraCharacteristics
import android.util.AttributeSet
import android.view.View
import java.util.HashSet

/**
 * A view which renders a series of custom graphics to be overlayed on top of an associated preview
 * (i.e., the camera preview). The creator can add graphics objects, update the objects, and remove
 * them, triggering the appropriate drawing and invalidation within the view.
 * <p>
 * <p>Supports scaling and mirroring of the graphics relative the camera's preview properties. The
 * idea is that detection items are expressed in terms of a preview size, but need to be scaled up
 * to the full view size, and also mirrored in the case of the front-facing camera.
 * <p>
 * <p>Associated {@link GraphicKt} items should use the following methods to convert to view
 * coordinates for the graphics that are drawn:
 * <p>
 * <ol>
 * <li>{@link GraphicKt#scaleX(float)} and {@link GraphicKt#scaleY(float)} adjust the size of the
 * supplied value from the preview scale to the view scale.
 * <li>{@link GraphicKt#translateX(float)} and {@link GraphicKt#translateY(float)} adjust the
 * coordinate from the preview's coordinate system to the view coordinate system.
 * </ol>
 */
open class GraphicOverlayKt(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val lock = Any()
    private var previewWidth: Int = 0
    internal var widthScaleFactor = 1.0f
    private var previewHeight: Int = 0
    internal var heightScaleFactor = 1.0f
    internal var facing = CameraCharacteristics.LENS_FACING_BACK
    private val graphics = HashSet<GraphicKt>()


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
    fun add(graphic: GraphicKt) {
        synchronized(lock) {
            graphics.add(graphic)
        }
        postInvalidate()
    }

    /**
     * Removes a graphic from the overlay.
     */
    fun remove(graphic: GraphicKt) {
        synchronized(lock) {
            graphics.remove(graphic)
        }
        postInvalidate()
    }

    /**
     * Sets the camera attributes for size and facing direction, which informs how to transform image
     * coordinates later.
     */
    fun setCameraInfo(previewWidth: Int, previewHeight: Int, facing: Int) {
        synchronized(lock) {
            this.previewWidth = previewWidth
            this.previewHeight = previewHeight
            this.facing = facing
        }
        postInvalidate()
    }


    /**
     * Draws the overlay with its associated graphic objects.
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        synchronized(lock) {
            if (previewWidth != 0 && previewHeight != 0) {
                widthScaleFactor = width.toFloat() / previewWidth.toFloat()
                heightScaleFactor = height.toFloat() / previewHeight.toFloat()
            }

            for (graphic in graphics) {
                graphic.draw(canvas)
            }
        }
    }

    /**
     * Base class for a custom graphics object to be rendered within the graphic overlay. Subclass
     * this and implement the {@link GraphicKt#draw(Canvas)} method to define the graphics element. Add
     * instances to the overlay using {@link GraphicOverlay#add(GraphicKt)}.
     */
    abstract class GraphicKt (val overlay : GraphicOverlayKt) {

        /**
         * Draw the graphic on the supplied canvas. Drawing should use the following methods to convert
         * to view coordinates for the graphics that are drawn:
         *
         *
         *
         *  1. [GraphicKt.scaleX] and [GraphicKt.scaleY] adjust the size of the
         * supplied value from the preview scale to the view scale.
         *  2. [GraphicKt.translateX] and [GraphicKt.translateY] adjust the
         * coordinate from the preview's coordinate system to the view coordinate system.
         *
         *
         * @param canvas drawing canvas
         */
        abstract fun draw(canvas: Canvas)

        /**
         * Adjusts a horizontal value of the supplied value from the preview scale to the view scale.
         */
        fun scaleX(horizontal: Float): Float {
            return horizontal * overlay.widthScaleFactor
        }

        /**
         * Adjusts a vertical value of the supplied value from the preview scale to the view scale.
         */
        fun scaleY(vertical: Float): Float {
            return vertical * overlay.heightScaleFactor
        }

        /**
         * Returns the application context of the app.
         */
        fun getApplicationContext(): Context {
            return overlay.context.applicationContext
        }

        /**
         * Adjusts the x coordinate from the preview's coordinate system to the view coordinate system.
         */
        fun translateX(x: Float): Float {
            return if (overlay.facing == CameraCharacteristics.LENS_FACING_FRONT) {
                overlay.width - scaleX(x)
            } else {
                scaleX(x)
            }
        }

        /**
         * Adjusts the y coordinate from the preview's coordinate system to the view coordinate system.
         */
        fun translateY(y: Float): Float {
            return scaleY(y)
        }

        fun postInvalidate() {
            overlay.postInvalidate()
        }


    }


}

