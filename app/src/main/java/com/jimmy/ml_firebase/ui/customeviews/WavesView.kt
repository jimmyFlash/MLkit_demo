package com.jimmy.ml_firebase.ui.customeviews

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import com.jimmy.ml_firebase.R
import com.jimmy.ml_firebase.helpers.TiltListener
import com.jimmy.ml_firebase.helpers.TiltRollSensor
import kotlin.math.cos
import kotlin.math.sin


/**
 *
 * Initialize the paint object with the custom attributes
 * Define the initial and max radius of the smallest/largest circle
 * Define the position where to start drawing the circles
 * Draw all the circles with a radius of initialRadius to maxRadius with each separated by a space of waveGap
 */

class WavesView @JvmOverloads constructor(context: Context,
                 attrs: AttributeSet? = null,
                 defStyleAttr: Int = R.attr.wavesViewStyle) : View(context, attrs, defStyleAttr) , TiltListener{

    // lazy initialization
    val tiltSensor by lazy {
        TiltRollSensor(context)
    }

    override fun onTilt(pitchRollRad: Pair<Double, Double>) {
        val pitchRad = pitchRollRad.first
        val rollRad = pitchRollRad.second

        // Use half view height/width to calculate offset instead of full view/device measurement
        val maxYOffset = center.y.toDouble()
        val maxXOffset = center.x.toDouble()

        val yOffset = (Math.sin(pitchRad) * maxYOffset)
        val xOffset = (Math.sin(rollRad) * maxXOffset)

        updateGradient(xOffset.toFloat() + center.x, yOffset.toFloat() + center.y)

    }


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

    private val gradientMatrix = Matrix()

    private val gradientPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        // Highlight only the areas already touched on the canvas
        xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    }

    // gradient colors
    private val green = Color.GREEN
    private val white = Color.WHITE
    private val grey = Color.GRAY
    private val primaryColor = context.getColor(R.color.colorPrimary)
    private val primaryDarkColor = context.getColor(R.color.colorPrimaryDark)
    // solid green in the center, transparent green at the edges
    private val gradientColors =
        intArrayOf(white, grey,  primaryColor)

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
        creating a value animator that runs for 4 seconds, repeating in an endless loop.
        On every animation frame, the waveRadiusOffset will be updated — waveRadiusOffset is the value that tracks the
        circle expansion from its original position
         */

        waveAnimator = ValueAnimator.ofFloat(0f, waveGap).apply {
            addUpdateListener {
                waveRadiusOffset = it.animatedValue as Float
            }
            duration = 4000L
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            start()
        }

        tiltSensor.addListener(this)
        tiltSensor.register()
    }

    override fun onDetachedFromWindow() {
        waveAnimator?.cancel()// stop animation
        tiltSensor.unregister()
        super.onDetachedFromWindow()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        //set the center of all circles to be center of the view
        center.set(w / 2f, h / 2f)
        maxRadius = Math.hypot(center.x.toDouble(), center.y.toDouble()).toFloat()
        initialRadius = w / waveGap

        //Create gradient after getting sizing information
        gradientPaint.shader = RadialGradient(
            center.x, center.y, maxRadius,
            gradientColors, null, Shader.TileMode.CLAMP
        )
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //draw circles separated by a space the size of waveGap (not animated)
        var currentRadius = initialRadius + waveRadiusOffset
       /* while (currentRadius < maxRadius) {
            canvas.drawCircle(center.x, center.y, currentRadius, wavePaint)
            canvas.drawPaint(gradientPaint)
            currentRadius += waveGap
        }*/

        //draw circles separated by a space the size of waveGap
//        onDraw runs using the new offset in order to simulate the animation.
        while (currentRadius < maxRadius) {
            // draw circles
//            canvas.drawCircle(center.x, center.y, currentRadius, wavePaint)

            // drw star shape using path
            val path = createStarPath(currentRadius, wavePath)
            canvas.drawPath(path, wavePaint)
            canvas.drawPaint(gradientPaint)

            // update radius
            currentRadius += waveGap
        }

    }

    /**
     * draws a star shape
     */
    private fun createStarPath( radius: Float, path: Path = Path(), points: Int = 40 ): Path {
        path.reset()
        val pointDelta = 0.7f // difference between the "far" and "close" points from the center
        val angleInRadians = 2.0 * Math.PI / points // essentially 360/20 or 18 degrees, angle each line should be drawn
        val startAngleInRadians = 0.0 //starting to draw star at 0 degrees

        //move pointer to 0 degrees relative to the center of the screen
        path.moveTo(
            center.x + (radius * pointDelta * cos(startAngleInRadians)).toFloat(),
            center.y + (radius * pointDelta * sin(startAngleInRadians)).toFloat()
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


    private fun modifyAlpha(color: Int, alpha: Float): Int {
        return color and 0x00ffffff or ((alpha * 255).toInt() shl 24)
    }


    /**
     * To move the gradient on the canvas, we’ll translate the gradient paint shader’s local matrix by a calculated amount.
     */
    private fun updateGradient(x: Float, y: Float) {
        gradientMatrix.setTranslate(x - center.x, y - center.y)
        gradientPaint.shader.setLocalMatrix(gradientMatrix)
        postInvalidateOnAnimation()
    }
}