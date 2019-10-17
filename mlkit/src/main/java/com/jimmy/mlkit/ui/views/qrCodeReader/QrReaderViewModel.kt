package com.jimmy.mlkit.ui.views.qrCodeReader

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.graphics.Bitmap
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.label.FirebaseVisionLabel

class QrReaderViewModel : ViewModel() {

    val  TAG:String? = QrReaderViewModel::class.java.simpleName
    private var itemsList: ArrayList<FirebaseVisionBarcode> = ArrayList()
    var itemsSucces = MutableLiveData< List<FirebaseVisionBarcode> >()
    var itemsFail = MutableLiveData< Boolean >()

    val bitmapImg: MutableLiveData<Bitmap> by lazy {
        MutableLiveData<Bitmap>()
    }


    fun getQRCodeDetails(bitmap: Bitmap) {
        itemsList.clear()
        val options = FirebaseVisionBarcodeDetectorOptions.Builder()
            .setBarcodeFormats(
                FirebaseVisionBarcode.FORMAT_ALL_FORMATS)
            .build()
        val detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options)
        val image = FirebaseVisionImage.fromBitmap(bitmap)
        detector.detectInImage(image)
            .addOnSuccessListener {
                itemsList.addAll(it)
                itemsSucces.postValue(itemsList)
            }
            .addOnFailureListener {
                it.printStackTrace()
                itemsFail.postValue(true)
            }

    }

}
