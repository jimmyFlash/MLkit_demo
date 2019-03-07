package com.jimmy.ml_firebase.ui.customeviews

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
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

    private val wavePaint: Paint
    private val waveGap: Float

    private var maxRadius = 0f
    private var center = PointF(0f, 0f)
    private var initialRadius = 0f

    private var waveAnimator: ValueAnimator? = null

    init {
        val attrs_ = context.obtainStyledAttributes(attrs, R.styleable.WavesView, defStyleAttr, 0)

        //init paint with custom attrs
        wavePaint = Paint(ANTI_ALIAS_FLAG).apply {
            color = attrs_.getColor(R.styleable.WavesView_waveColor, 0)
            strokeWidth = attrs_.getDimension(R.styleable.WavesView_waveStrokeWidth, 0f)
            style = Paint.Style.STROKE
        }

        waveGap = attrs_.getDimension(R.styleable.WavesView_waveGap, 50f)
        attrs_.recycle()
    }

    private var waveRadiusOffset = 0f
        set(value) {
            field = value
            postInvalidateOnAnimation()
        }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

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
        waveAnimator?.cancel()
        super.onDetachedFromWindow()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        //set the center of all circles to be center of the view
        center.set(w / 2f, h / 2f)
        maxRadius = Math.hypot(center.x.toDouble(), center.y.toDouble()).toFloat()
        initialRadius = w / waveGap
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        //draw circles separated by a space the size of waveGap
        var currentRadius = initialRadius
        while (currentRadius < maxRadius) {
            canvas!!.drawCircle(center.x, center.y, currentRadius, wavePaint)
            currentRadius += waveGap
        }
    }
}