package com.jimmy.mlkit.ui.views


import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.support.annotation.LayoutRes
import android.support.annotation.RequiresApi
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_google_lens.*


abstract class BaseCameraActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var sheetBehavior: BottomSheetBehavior<*>

    companion object{
       const val CAMERA_REQUEST_CODE: Int= 1000
    }

    fun setupBottomSheet(@LayoutRes id : Int){
        //Using a ViewStub since changing the layout of an <include> tag dynamically wasn't possible
        stubView.layoutResource = id
        val inflatedView = stubView.inflate()
        //Set layout parameters for the inflated bottomsheet
        val lparam = inflatedView.layoutParams as CoordinatorLayout.LayoutParams
        lparam.behavior = BottomSheetBehavior<View>()
        inflatedView.layoutParams = lparam
        sheetBehavior = BottomSheetBehavior.from(inflatedView)
        sheetBehavior.peekHeight = 180

        //Hide the fab as bottomSheet is expanded
        sheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {}
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                fab_take_photo.animate().scaleX(1 - slideOffset)
                                        .scaleY(1 - slideOffset)
                                        .setDuration(0).start()
            }
        })
    }


     fun isCameraPermissionGranted(): Boolean {
        val permission = ContextCompat.checkSelfPermission(this@BaseCameraActivity, Manifest.permission.CAMERA)
        return permission == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.M)
     fun requestCameraPermission() {
        requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
    }


}