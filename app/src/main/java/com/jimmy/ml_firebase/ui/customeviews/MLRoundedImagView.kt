package com.jimmy.ml_firebase.ui.customeviews

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ImageView


class MLRoundedImagView (context: Context, attrs: AttributeSet) : ImageView(context, attrs) {

    var rad:Int = 0


    //todo scale bitmap if larger than the scale of the view
    init {
        attrs.let {
            val typedArray = context.obtainStyledAttributes(it, com.jimmy.ml_firebase.R.styleable.MLRoundedImagView,
                0, 0)

            rad = typedArray.getInt(com.jimmy.ml_firebase.R.styleable.MLRoundedImagView_circle_radius, 0)
            typedArray.recycle()
        }
    }

    override fun onDraw(canvas: Canvas) {

        val drawable = drawable ?: return
        if(width == 0 || height == 0) return


        Log.e("assets dim:", "width : $width and height: $height")

        var bitmap:Bitmap
        try {
            bitmap = (drawable as BitmapDrawable).bitmap

            Log.e("type", "bitmap")

            drawonCanvas(canvas, bitmap)

        } catch (e: ClassCastException) {
            bitmap =  getBitmap(drawable)

            Log.e("type", "drawable or vectordrawable  ${bitmap.width} and ${bitmap.height}")

            drawonCanvas(canvas, bitmap)
        }

    }


    private fun drawonCanvas(canvas: Canvas, bitmap : Bitmap){
        val bitmapCopy = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        val wid = width
        val hi = height

        // set the radius to image width if radius is 0 or larger than image width
        if(rad == 0 || rad > wid) rad = wid

        val roundBmp = getCroppedBitmap(bitmapCopy, rad, this)
//        val roundBmp = getCroppedBitmap(bitmapCopy, rad, null)
        canvas.drawBitmap(roundBmp, 0f, 0f, null)
    }

    companion object {
         fun getCroppedBitmap(bitmap : Bitmap, radius : Int, view : View  ) : Bitmap{


             var sbmp = bitmap

             var desiredW = bitmap.width
             var desiredH = bitmap.height


             //when the view is given an intrinsic width and height
             if(view != null){
                 desiredW = view.width
                 desiredH = view.height

                 if(bitmap.width != desiredW){
                     val smallest: Int = Math.min(bitmap.width, view.width)
                     val factor = smallest.toFloat() / bitmap.width.toFloat()
                     Log.e("ratio view/bmp: ", " = ${view.width} / ${bitmap.width}")
                     Log.e(">>>>>>>>>>>", "smallest: $smallest  / factor: $factor")
                     sbmp = Bitmap.createScaledBitmap(bitmap, (bitmap.width * factor).toInt()
                         , (bitmap.width * factor).toInt(), false)
                 }
             }
             val output:Bitmap = Bitmap.createBitmap(view.width, view.width, Bitmap.Config.ARGB_8888)

             val canvas = Canvas(output)

             val paint = Paint()
             var rect = Rect(0,0, sbmp.width, sbmp.width)
             var rect_ = Rect(0,0, view.width, view.width)

             paint.isAntiAlias = true
             paint.isFilterBitmap = true
             paint.isDither = true
             paint.color = Color.parseColor("#ffcc00")

             // E/GGGGGG: wv/wb = 525 / 648

             if(radius < desiredW){

                 rect = Rect(0,0, desiredW, desiredW)
                 canvas.drawRoundRect(0f, 0f, desiredW + 0.7f, desiredW + 0.7f,
                     radius + 0.7f, radius + 0.7f, paint)
             }else {
                 canvas.drawCircle(
                     radius / 2 + 0.7f,
                     radius / 2 + 0.7f,
                     radius / 2 + 0.7f,
                     paint
                 )
             }
             paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
             canvas.drawBitmap(sbmp, rect, rect_, paint)

             return output

        }
    }

    fun getBitmapFromVectorDrawable(context: Context, drawableId : Int): Bitmap {

        var drawable = ContextCompat.getDrawable(context, drawableId)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = DrawableCompat.wrap(drawable!!).mutate()
        }

        val bitmap = Bitmap.createBitmap( drawable!!.intrinsicWidth,
            drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)


        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)


        return bitmap
    }


    private fun getBitmap(drawable: Drawable): Bitmap {

        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }


}