package com.jimmy.mlkit.ui.views.googleLensClone

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.graphics.Bitmap
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage

class GoogleLensFragViewModel (application : Application) : AndroidViewModel(application) {

    val  TAG:String? = GoogleLensViewModel::class.java.simpleName
    private var itemsList: ArrayList<Any> = ArrayList()
    var itemsSucces = MutableLiveData< List<Any> >()
    var itemsFail = MutableLiveData< Boolean >()

    val bitmapImg: MutableLiveData<Bitmap> by lazy {
        MutableLiveData<Bitmap>()
    }


    fun getLabelsFromDevice(bitmap: Bitmap) {

        val image = FirebaseVisionImage.fromBitmap(bitmap)
        val detector = FirebaseVision.getInstance().visionLabelDetector

        itemsList.clear()
        detector.detectInImage(image)
            .addOnSuccessListener {
                itemsList.addAll(it)
                itemsSucces.postValue(itemsList)
            }
            .addOnFailureListener {
                itemsFail.postValue(true)
            }
    }

    fun getLabelsFromClod(bitmap: Bitmap) {

        val image = FirebaseVisionImage.fromBitmap(bitmap)
        val detector = FirebaseVision.getInstance()
            .visionCloudLabelDetector
        itemsList.clear()
        detector.detectInImage(image)
            .addOnSuccessListener {
                // Task completed successfully
                itemsList.addAll(it)
                itemsSucces.postValue(itemsList)
            }
            .addOnFailureListener {
                // Task failed with an exception
                itemsFail.postValue(true)
            }

    }
}
