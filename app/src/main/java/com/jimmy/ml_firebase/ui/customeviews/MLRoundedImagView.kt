package com.jimmy.ml_firebase.ui.customviews

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


        Log.e("KKKKKK", "width : $width and height: $height")

        var bitmap:Bitmap
        try {
            bitmap = (drawable as BitmapDrawable).bitmap

            Log.e("bitmap", "bitmap")

            drawonCanvas(canvas, bitmap)

        } catch (e: ClassCastException) {
            bitmap =  getBitmap(drawable)

            Log.e("drawable/VD", "drawable or vectordrawable  ${bitmap.width} and ${bitmap.height}")

            drawonCanvas(canvas, bitmap)
        }

    }


    private fun drawonCanvas(canvas: Canvas, bitmap : Bitmap){
        val bitmapCopy = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        val wid = width
        val hi = height

        if(rad == 0 || rad > wid) rad = wid

        val roundBmp = getCroppedBitmap(bitmapCopy, rad, this)
        canvas.drawBitmap(roundBmp, 0f, 0f, null)
    }

    companion object {
         fun getCroppedBitmap(bitmap : Bitmap, radius : Int, view : View?  ) : Bitmap{


             val sbmp = bitmap

            /* val sbmp = if(bitmap.width != radius || bitmap.height != radius){
                 val smallest: Int = Math.min(bitmap.width, bitmap.height)
                 val factor = smallest / radius
                 Bitmap.createScaledBitmap(bitmap, bitmap.width / factor
                     , bitmap.height / factor, false)
             }else{
                 bitmap
             }*/

             var bmpW = bitmap.width
             var bmpH = bitmap.height

             if(view != null){
                 bmpW = view.width
                 bmpH = view.height
             }
             var output:Bitmap = Bitmap.createBitmap(bmpW, bmpW, Bitmap.Config.ARGB_8888)

             val canvas = Canvas(output)

             val paint = Paint()
             var rect = Rect(0,0, radius, radius)

             paint.isAntiAlias = true
             paint.isFilterBitmap = true
             paint.isDither = true
             paint.color = Color.parseColor("#ffcc00")

             // E/GGGGGG: wv/wb = 525 / 648
             Log.e("GGGGGG", "wv/wb = $bmpW / ${bitmap.width}")
             if(radius < bmpW){

                 rect = Rect(0,0, bmpW, bmpW)
                 canvas.drawRoundRect(0f, 0f, bmpW + 0.7f, bmpW + 0.7f,
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
             canvas.drawBitmap(sbmp, rect, rect, paint)

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