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


abstract class BaseCameraActivity : AppCompatActivity(){

    companion object{
       const val CAMERA_REQUEST_CODE: Int= 1000
       const val MULTI_REQUEST_CODE: Int= 2000
    }

     fun isCameraPermissionGranted(): Boolean {
        val permission = ContextCompat.checkSelfPermission(this@BaseCameraActivity, Manifest.permission.CAMERA)
        return permission == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.M)
     fun requestCameraPermission() {
        requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun requestMultiPermission(permissions : Array<String>, requestCode : Int) {
        requestPermissions(permissions, requestCode)
    }




}