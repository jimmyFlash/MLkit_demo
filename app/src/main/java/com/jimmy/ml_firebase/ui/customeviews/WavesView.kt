package com.jimmy.ml_firebase.ui.customeviews

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Path
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import com.jimmy.ml_firebase.R

/**
 *
 * Initialize the paint object with the custom attributes
 * Define the initial and max radius of the smallest/largest circle
 * Define the position where to start drawing the circles
 * Draw all the circles with a radius of initialRadius to maxRadius with each separated by a space of waveGap
 */

class WavesView @JvmOverloads constructor(context: Context,
                 attrs: AttributeSet? = null,
                 defStyleAttr: Int = R.attr.wavesViewStyle) : View(context, attrs, defStyleAttr) {


    // color
    private val wavePaint: Paint

    // gap between shapes
    private val waveGap: Float

    // positioning and radius
    private var maxRadius = 0f
    private var center = PointF(0f, 0f)
    private var initialRadius = 0f

    // to animate the circles/stars
    private var waveAnimator: ValueAnimator? = null

    //to draw the star shape
    private val wavePath = Path()

    init {
        val attrs_ = context.obtainStyledAttributes(attrs, R.styleable.WavesView, defStyleAttr, 0)

        //init paint with custom attrs
        wavePaint = Paint(ANTI_ALIAS_FLAG).apply {
            color = attrs_.getColor(R.styleable.WavesView_waveColor, 0)
            strokeWidth = attrs_.getDimension(R.styleable.WavesView_waveStrokeWidth, 10f)
            style = Paint.Style.STROKE
        }

        waveGap = attrs_.getDimension(R.styleable.WavesView_waveGap, 50f)
        attrs_.recycle()
    }

    // define setter for the radius offeset
    private var waveRadiusOffset = 0f
        set(value) {
            field = value
//            call postInvalidateOnAnimation() in the setter to redraw our view’s next frame, makes the animation work
            postInvalidateOnAnimation()
        }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        /*
        creating a value animator that runs for 1.5 seconds, repeating in an endless loop.
        On every animation frame, the waveRadiusOffset will be updated — waveRadiusOffset is the value that tracks the
        circle expansion from its original position
         */

        waveAnimator = ValueAnimator.ofFloat(0f, waveGap).apply {
            addUpdateListener {
                waveRadiusOffset = it.animatedValue as Float
            }
            duration = 1500L
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            start()
        }
    }

    override fun onDetachedFromWindow() {
        waveAnimator?.cancel()// stop animation
        super.onDetachedFromWindow()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        //set the center of all circles to be center of the view
        center.set(w / 2f, h / 2f)
        maxRadius = Math.hypot(center.x.toDouble(), center.y.toDouble()).toFloat()
        initialRadius = w / waveGap
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //draw circles separated by a space the size of waveGap (not animated)
       /*
        var currentRadius = initialRadius
        while (currentRadius < maxRadius) {
            canvas.drawCircle(center.x, center.y, currentRadius, wavePaint)
            currentRadius += waveGap
        }
        */

        //draw circles separated by a space the size of waveGap
//        onDraw runs using the new offset in order to simulate the animation.
        var currentRadius = initialRadius + waveRadiusOffset
        while (currentRadius < maxRadius) {
            // draw circles
//            canvas.drawCircle(center.x, center.y, currentRadius, wavePaint)

            // drw star shape using path
            val path = createStarPath(currentRadius, wavePath)
            canvas.drawPath(path, wavePaint)

            // update radius
            currentRadius += waveGap
        }

    }

    /**
     * draws a star shape
     */
    private fun createStarPath( radius: Float, path: Path = Path(), points: Int = 20 ): Path {
        path.reset()
        val pointDelta = 0.7f // difference between the "far" and "close" points from the center
        val angleInRadians = 2.0 * Math.PI / points // essentially 360/20 or 18 degrees, angle each line should be drawn
        val startAngleInRadians = 0.0 //starting to draw star at 0 degrees

        //move pointer to 0 degrees relative to the center of the screen
        path.moveTo(
            center.x + (radius * pointDelta * Math.cos(startAngleInRadians)).toFloat(),
            center.y + (radius * pointDelta * Math.sin(startAngleInRadians)).toFloat()
        )

        //create a line between all the points in the star
        for (i in 1 until points) {
            val hypotenuse = if (i % 2 == 0) {
                //by reducing the distance from the circle every other points, we create the "dip" in the star
                pointDelta * radius
            } else {
                radius
            }

            val nextPointX = center.x + (hypotenuse * Math.cos(startAngleInRadians - angleInRadians * i)).toFloat()
            val nextPointY = center.y + (hypotenuse * Math.sin(startAngleInRadians - angleInRadians * i)).toFloat()
            path.lineTo(nextPointX, nextPointY)
        }

        path.close()
        return path
    }
}