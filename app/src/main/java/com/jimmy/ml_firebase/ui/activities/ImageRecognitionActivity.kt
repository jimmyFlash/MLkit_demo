package com.jimmy.ml_firebase.ui.activities

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.jimmy.ml_firebase.Constants.PERMISSION_REQUEST_CODE
import com.jimmy.ml_firebase.R
import com.jimmy.ml_firebase.databinding.ActivityImageRecogBinding
import com.jimmy.ml_firebase.permissions.PermissionManager
import com.jimmy.ml_firebase.permissions.PermissionsManagerWExtion
import com.jimmy.ml_firebase.permissions.PermissionsManagerWExtion.hasPermission
import com.jimmy.ml_firebase.ui.showToast
import com.jimmy.ml_firebase.uidataproviders.viewmodel.ImageRecogActivityViewmodel
import com.mindorks.paracamera.Camera
import kotlinx.android.synthetic.main.activity_image_recog.*

class ImageRecognitionActivity : AppCompatActivity() {

    lateinit var binding : ActivityImageRecogBinding
    lateinit var mViewModel : ImageRecogActivityViewmodel
    private lateinit var camera: Camera
    private var  pm: PermissionManager? = null// permission manager ref.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // initiate binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_image_recog)
        // Get the ViewModel
        mViewModel = ViewModelProviders.of(this).get(ImageRecogActivityViewmodel::class.java)

//        initialize and configure the camera
        camera = Camera.Builder()
            .resetToCorrectOrientation(true)// Rotates the camera bitmap to the correct orientation from meta data.
            .setTakePhotoRequestCode(Camera.REQUEST_TAKE_PHOTO)// Sets the request code for your onActivityResult() method
            .setDirectory("pics")//Sets the directory in which your pictures will be saved.
            .setName("delicious_${System.currentTimeMillis()}")// Sets the name of each picture taken according to the system time.
            .setImageFormat(Camera.IMAGE_JPEG)//Sets the image format to JPEG
            .setCompression(75)//Sets a compression rate of 75% to use less system resources
            .build(this)

        mViewModel.setCamera(camera)

        binding.viewModel = mViewModel

        // observer the state of the responseCardViewState
        mViewModel.responseCardViewState.observe(this, Observer {

            // if food iis detected control the color and title widgets
            if (it!!) {
                responseCardView.setCardBackgroundColor(Color.GREEN)
                responseTextView.text = getString(R.string.delicious_food)
            } else {
                responseCardView.setCardBackgroundColor(Color.RED)
                responseTextView.text = getString(R.string.not_delicious_food)
            }

        })
        /**
         * observe errors for processing the bitmap
         */
        mViewModel.error.observe(this, Observer {

            if(it != 0){
                this.applicationContext.showToast(getString(R.string.picture_not_taken))
            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Camera.REQUEST_TAKE_PHOTO) {
                val bitmap = camera.cameraBitmap
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap)
                    mViewModel.detectDeliciousFoodOnDevice(bitmap)
                } else {
                    this.applicationContext.showToast(getString(R.string.picture_not_taken))
                }
            }
        }
    }




    fun takePicture(view: View) {
        if (!hasPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
            !hasPermission(this, android.Manifest.permission.CAMERA)) {

            PermissionsManagerWExtion.requestPermissions(this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA),
                PERMISSION_REQUEST_CODE,
                mainLayout,
                getString(R.string.permission_message),
                getString(R.string.OK)
            )

        } else {
            // else all permissions granted, go ahead and take a picture using camera
            try {
                camera.takePicture()
            } catch (e: Exception) {
                // Show a toast for exception
                this.applicationContext.showToast(getString(R.string.error_taking_picture))
            }
        }
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    try {
                        camera.takePicture()
                    } catch (e: Exception) {
                        this.applicationContext.showToast(getString(R.string.error_taking_picture))
                    }
                }
            }
        }
    }
}