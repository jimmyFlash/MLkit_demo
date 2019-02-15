package com.jimmy.ml_firebase.logicrepository

import android.graphics.Bitmap
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.cloud.FirebaseVisionCloudDetectorOptions
import com.google.firebase.ml.vision.cloud.label.FirebaseVisionCloudLabel
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.label.FirebaseVisionLabel
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetectorOptions

class RepositoryManager{

    fun detectDeliciousFoodOnDevice(bitmap: Bitmap, callback : StatusCallBack) {
        //1 FirebaseVisionLabelDetector and FirebaseVisionImage objects.
        // The threshold represents the minimum level of confidence that you will accept for the results
        val image = FirebaseVisionImage.fromBitmap(bitmap)

        val options = FirebaseVisionLabelDetectorOptions.Builder()
            .setConfidenceThreshold(0.8f)
            .build()
        /*
            FirebaseVisionLabel is an object that contains a String for an associated image and the confidence for the result.
            FirebaseVisionLabelDetector is an object that receives a Bitmap and returns an array of FirebaseVisionLabels.
         */
        val detector = FirebaseVision.getInstance().getVisionLabelDetector(options)

        //2 detectInImage() method of the FirebaseVisionLabelDetector object and add an onSuccessListener and onFailureListener
        detector.detectInImage(image)
            //3 If successful, you call the hasDeliciousFood() method with the array of FirebaseVisionLabel objects
            // mapped to an array of Strings to check if there is food on the picture,
            // and then display a message accordingly.
            .addOnSuccessListener { it ->
                callback.success(it)
            }
            //4 failure, you display a toast with the error message from activity
            .addOnFailureListener {
                callback.fail(it)
            }
    }

    fun detectDeliciousFoodOnCloud(bitmap: Bitmap, callback : StatusCallBack) {
        //1 FirebaseVisionLabelDetector and FirebaseVisionImage objects.
        // The threshold represents the minimum level of confidence that you will accept for the results
        val image = FirebaseVisionImage.fromBitmap(bitmap)

        val options = FirebaseVisionCloudDetectorOptions.Builder()
            //since the number of results could be very high, you should set a limit using the setMaxResults() method. In our case, it’s been set to 10.
            .setMaxResults(10)
            .build()
        val detector = FirebaseVision.getInstance().getVisionCloudLabelDetector(options)

        detector.detectInImage(image)
            //3 If successful, you call the hasDeliciousFood() method with the array of FirebaseVisionLabel objects
            // mapped to an array of Strings to check if there is food on the picture,
            // and then display a message accordingly.
            .addOnSuccessListener { it ->
                callback.successCloud(it)
            }
            //4 failure, you display a toast with the error message from activity
            .addOnFailureListener {
                callback.fail(it)
            }
    }

    interface StatusCallBack{
        fun success (detectedList : List<FirebaseVisionLabel>  )
        fun successCloud (detectedList : List<FirebaseVisionCloudLabel>  )
        fun fail(exception: Exception)
    }
}