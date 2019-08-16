package com.jimmy.mlkit.ui.views.googleLensClone


import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import com.jimmy.mlkit.BuildConfig
import com.jimmy.mlkit.R
import com.jimmy.mlkit.databinding.ActivityGoogleLensBinding
import com.jimmy.mlkit.ui.views.BaseCameraActivity
import com.mindorks.paracamera.Camera
import kotlinx.android.synthetic.main.activity_google_lens.*
import kotlinx.android.synthetic.main.image_label_layout.*

class GoogleLens : BaseCameraActivity() {

//todo create custom intents to bind the module activity to main project
    private lateinit var itemAdapter: ImageLabelAdapter
    private lateinit var camera: Camera


    override fun onClick(v: View?) {
       //
    }

    lateinit var binding : ActivityGoogleLensBinding
    lateinit var mViewModel : GoogleLensViewModel
    var itemsList = mutableListOf<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_lens)
        setSupportActionBar(toolbar)
        mViewModel = ViewModelProviders.of(this).get(GoogleLensViewModel::class.java)
        fab_take_photo.setOnClickListener(this::takePicture)
        setupBottomSheet(R.layout.image_label_layout)
        rvLabel.layoutManager = LinearLayoutManager(this)


        mViewModel.itemsFail.observe(this, Observer {

            Toast.makeText(baseContext,"Sorry, something went wrong!", Toast.LENGTH_SHORT).show()

        })

        mViewModel.itemsSucces.observe(this, Observer {

            itemAdapter = ImageLabelAdapter(it!!, false)
            rvLabel.adapter = itemAdapter
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
        })

        // initialize and configure the camera
        camera = Camera.Builder()
            .resetToCorrectOrientation(true)// Rotates the camera bitmap to the correct orientation from meta data.
            .setTakePhotoRequestCode(Camera.REQUEST_TAKE_PHOTO)// Sets the request code for your onActivityResult() method
            .setDirectory("pics")//Sets the directory in which your pictures will be saved.
            .setName("delicious_${System.currentTimeMillis()}")// Sets the name of each picture taken according to the system time.
            .setImageFormat(Camera.IMAGE_JPEG)//Sets the image format to JPEG
            .setCompression(75)//Sets a compression rate of 75% to use less system resources
            .build(this)
    }



    fun takePicture(view: View) {

        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(!isCameraPermissionGranted()){
                requestCameraPermission()
            }else{
                try {
                    camera.takePicture()
                } catch (e: Exception) {
                    // Show a toast for exception
                    Toast.makeText(baseContext,"Sorry, something went wrong!", Toast.LENGTH_SHORT).show()
                }
            }
        }else{
            try {
                camera.takePicture()
            } catch (e: Exception) {
                // Show a toast for exception
                Toast.makeText(baseContext,"Sorry, something went wrong!", Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Camera.REQUEST_TAKE_PHOTO) {
                val bitmap = camera.cameraBitmap
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap)
                    mViewModel.getLabelsFromDevice(bitmap)
                } else {
                    Toast.makeText(baseContext,"Sorry, something went wrong!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                try {
                    camera.takePicture()
                } catch (e: Exception) {
                    // Show a toast for exception
                    Toast.makeText(baseContext,"Sorry, something went wrong!", Toast.LENGTH_SHORT).show()
                }
            } else {
               //
            }
        }
    }

}
