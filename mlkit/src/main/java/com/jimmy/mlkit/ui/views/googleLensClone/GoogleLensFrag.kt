package com.jimmy.mlkit.ui.views.googleLensClone

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.jimmy.mlkit.R
import com.jimmy.mlkit.ui.utils.ExitWithAnimation
import com.jimmy.mlkit.ui.utils.startCircularReveal
import com.jimmy.mlkit.ui.views.BaseCameraActivity
import com.jimmy.mlkit.ui.views.BaseCameraActivity.Companion.CAMERA_REQUEST_CODE
import com.jimmy.mlkit.ui.views.BaseFragment
import com.mindorks.paracamera.Camera
import kotlinx.android.synthetic.main.google_lens_fragment.*
import kotlinx.android.synthetic.main.image_label_layout.*

class GoogleLensFrag : BaseFragment(), ExitWithAnimation {


    override var posX: Int? = null
    override var posY: Int? = null

    override fun isToBeExitedWithAnimation(): Boolean = true

    private lateinit var itemAdapter: ImageLabelAdapter
    private lateinit var camera: Camera

    companion object {
        fun newInstance(exit: IntArray? = null) = GoogleLensFrag().apply {
            if (exit != null && exit.size == 2) {
                posX = exit[0]
                posY = exit[1]
            }
        }
    }

    private lateinit var viewModel: GoogleLensFragViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.google_lens_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.startCircularReveal(false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(GoogleLensFragViewModel::class.java)
        fab_take_photo.setOnClickListener(this::takePicture)
        // initialize and configure the camera
        camera = Camera.Builder()
            .resetToCorrectOrientation(true)// Rotates the camera bitmap to the correct orientation from meta data.
            .setTakePhotoRequestCode(Camera.REQUEST_TAKE_PHOTO)// Sets the request code for your onActivityResult() method
            .setDirectory("pics")//Sets the directory in which your pictures will be saved.
            .setName("delicious_${System.currentTimeMillis()}")// Sets the name of each picture taken according to the system time.
            .setImageFormat(Camera.IMAGE_JPEG)//Sets the image format to JPEG
            .setCompression(75)//Sets a compression rate of 75% to use less system resources
            .build(this)

        setupBottomSheet(R.layout.image_label_layout)
        rvLabel.layoutManager = LinearLayoutManager(activity)
        viewModel.itemsFail.observe(this, Observer {

            Toast.makeText(activity,"Sorry, something went wrong!", Toast.LENGTH_SHORT).show()

        })

        viewModel.itemsSucces.observe(this, Observer {

            itemAdapter = ImageLabelAdapter(it!!, false)
            rvLabel.adapter = itemAdapter
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
        })
    }

    private fun takePicture(view: View) {

        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(!(activity as BaseCameraActivity).isCameraPermissionGranted()){
                (activity as BaseCameraActivity).requestCameraPermission()
            }else{
                try {
                    camera.takePicture()
                } catch (e: Exception) {
                    // Show a toast for exception
                    Toast.makeText(activity,"Sorry, something went wrong!", Toast.LENGTH_SHORT).show()
                }
            }
        }else{
            try {
                camera.takePicture()
            } catch (e: Exception) {
                // Show a toast for exception
                Toast.makeText(activity,"Sorry, something went wrong!", Toast.LENGTH_SHORT).show()
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
                    viewModel.getLabelsFromDevice(bitmap)
                } else {
                    Toast.makeText(activity, "Sorry, something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                try {
                    camera.takePicture()
                } catch (e: Exception) {
                    // Show a toast for exception
                    Toast.makeText(activity, "Sorry, something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

    }

}
