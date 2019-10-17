package com.jimmy.mlkit.ui.views.qrCodeReader

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode

import com.jimmy.mlkit.R
import com.jimmy.mlkit.ui.utils.ExitWithAnimation
import com.jimmy.mlkit.ui.utils.startCircularReveal
import com.jimmy.mlkit.ui.views.BaseCameraActivity
import com.jimmy.mlkit.ui.views.BaseFragment
import com.mindorks.paracamera.Camera
import kotlinx.android.synthetic.main.google_lens_fragment.*
import kotlinx.android.synthetic.main.image_label_layout.*

class QrReaderFrag : BaseFragment(), ExitWithAnimation {

    override var posX: Int? = null
    override var posY: Int? = null

    private lateinit var camera: Camera
    override fun isToBeExitedWithAnimation(): Boolean = false

    //todo handel the activity result delegation to fragment

    companion object {
        fun newInstance(exit: IntArray? = null) = QrReaderFrag().apply {
            // exit animation coordinates if given
            if (exit != null && exit.size == 2) {
                posX = exit[0]
                posY = exit[1]
            }
        }
    }

    private lateinit var viewModel: QrReaderViewModel
    private lateinit var qrAdapter: QRLabelAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.google_lens_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // apply circular reveal animation mask from the bottom left on 1st instantiation
        if(savedInstanceState == null)view.startCircularReveal(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(QrReaderViewModel::class.java)

        fab_take_photo.setOnClickListener(this::takePicture)// add click handler for fab

        // initialize and configure the camera lib instance
        camera = Camera.Builder()
            .resetToCorrectOrientation(true)// Rotates the camera bitmap to the correct orientation from meta data.
            .setTakePhotoRequestCode(Camera.REQUEST_TAKE_PHOTO)// Sets the request code for your onActivityResult() method
            .setDirectory("pics")//Sets the directory in which your pictures will be saved.
            .setName("QR_${System.currentTimeMillis()}")// Sets the name of each picture taken according to the system time.
            .setImageFormat(Camera.IMAGE_JPEG)//Sets the image format to JPEG
            .setCompression(75)//Sets a compression rate of 75% to use less system resources
            .build(this)

        setupBottomSheet(R.layout.image_label_layout)
        rvLabel.layoutManager = LinearLayoutManager(activity)
        qrAdapter = QRLabelAdapter()
        rvLabel.adapter = qrAdapter
//        qrAdapter.submitList()

        val bmpObserver = Observer<Bitmap> { bmp ->
            // Update the UI
            imageView.setImageBitmap(bmp)
            viewModel.getQRCodeDetails(bmp!!)
        }

        viewModel.bitmapImg.observe(this, bmpObserver)

        viewModel.itemsFail.observe(this, Observer {

            Toast.makeText(activity,"Sorry, something went wrong!", Toast.LENGTH_SHORT).show()
        })

        viewModel.itemsSucces.observe(this, Observer {

//            Log.e("FB-device", "items detected ${it?.size}, ${(it?.get(0) as FirebaseVisionBarcode).valueType}")
            Toast.makeText(activity,"Items detected ${it?.size}", Toast.LENGTH_SHORT).show()
            if(sheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED){
                sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }

            qrAdapter.submitList(it)// update adapter data
            rvLabel.smoothScrollToPosition(0)

        })

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Camera.REQUEST_TAKE_PHOTO) {
                val bitmap = camera.cameraBitmap
                if (bitmap != null) {
                    viewModel.bitmapImg.value = bitmap

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
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == BaseCameraActivity.CAMERA_REQUEST_CODE) {
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

    /**
     * handles the click of fab to take picture
     * checks for camera permission
     */
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

}
