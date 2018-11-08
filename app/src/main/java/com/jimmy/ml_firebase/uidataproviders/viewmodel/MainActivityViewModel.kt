package com.jimmy.ml_firebase.uidataproviders.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import android.text.TextUtils
import android.util.SparseArray
import android.widget.ImageView
import android.widget.Toast
import androidx.work.*
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.cloud.FirebaseVisionCloudDetectorOptions
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentTextRecognizer
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer
import com.jimmy.ml_firebase.Constants.IMAGE_MANIPULATION_WORK_NAME
import com.jimmy.ml_firebase.Constants.KEY_IMAGE_URI
import com.jimmy.ml_firebase.Constants.KEY_IMAGE_VIEW_H
import com.jimmy.ml_firebase.Constants.KEY_IMAGE_VIEW_W
import com.jimmy.ml_firebase.Constants.TAG_OUTPUT
import com.jimmy.ml_firebase.workers.CleanupWorker
import com.jimmy.ml_firebase.workers.ImageResizeWorker

/*
    AndroidViewModel from Lifecycle-aware components library that has context.
    That context is context of the application, not of an Activity

 */
class MainActivityViewModel(application : Application) : AndroidViewModel(application) {

    val ctx = this.getApplication<Application>()

    private var mImageUri: Uri? = null// image uri to load

    private val mWorkManager: WorkManager = WorkManager.getInstance() //  instance holder of the work manager

    // New instance variable for the WorkStatus to be observed with live data
    internal var mSavedWorkStatus: LiveData<List<WorkStatus>>

    private var mSavedSingleWorkStatus: LiveData<WorkStatus>? = null

     var mtextBlocks: MutableLiveData<FirebaseVisionText> = MutableLiveData()

    val isLoading = ObservableField(false)

    // New instance variable for the WorkStatus
    private val mOutputUri: Uri? = null

    fun runTextRecognition(selectedImage: Bitmap) {
        val image = FirebaseVisionImage.fromBitmap(selectedImage)
        val detector : FirebaseVisionTextRecognizer = FirebaseVision.getInstance().onDeviceTextRecognizer

        detector.processImage(image)
            .addOnSuccessListener { texts ->
                processTextRecognitionResult(texts)
            }
            .addOnFailureListener{ it ->
                it.printStackTrace()
            }
    }

    private fun processTextRecognitionResult(texts: FirebaseVisionText) {
        isLoading.set(false)
        mtextBlocks?.value = texts
    }

    fun resetBlocks(){
        mtextBlocks?.value = null
    }

    fun runCloudTextRecognition(selectedImage: Bitmap) {
        // cloud ML requires paid Blaze plan not implemented now ( using spark free plan )
        isLoading.set(true)

        // 1
        val options = FirebaseVisionCloudTextRecognizerOptions.Builder()
            .setModelType(FirebaseVisionCloudDetectorOptions.LATEST_MODEL)
            .build()
        val image = FirebaseVisionImage.fromBitmap(selectedImage)

        // 2
        val detector = FirebaseVision.getInstance().getCloudTextRecognizer(options)
        detector.processImage(image).addOnSuccessListener { texts ->
                processCloudTextRecognitionResult(texts)
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    private fun processCloudTextRecognitionResult(texts: FirebaseVisionText?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun looksLikeHandle(text: String) = text.matches(Regex("@(\\w+)"))

    class WordPair(val word: String, val handle: FirebaseVisionDocumentTextRecognizer)



    init{

        /*
           tagging the WorkRequest, so that you can get it using getStatusesByTag.
           You'll use a tag to label your work instead of using the WorkManager ID,
           because if your user creates multiple work, all of the WorkRequests will have
           the same tag but not the same ID. Also you are able to pick the tag.

           You would not use getStatusesForUniqueWork because that would return the WorkStatus for all of the
            WorkRequests
        */

        // This transformation makes sure that whenever the current work Id changes the WorkStatus
        // the UI is listening to changes
        mSavedWorkStatus = mWorkManager.getStatusesByTagLiveData(TAG_OUTPUT)

//        mSavedSingleWorkStatus = mWorkManager?.getStatusByIdLiveData(UUID.fromString("00002415-0000-1000-8000-00805F9B34FB"))

    }

    /**
     * Setters, set the uri of loaded image
     */
    fun setImageUri(uri: String) {
        mImageUri = uriOrNull(uri)
    }

    /**
     * Getters, get selected image URI
     */
    fun getImageUri(): Uri? {
        return mImageUri
    }

    // helper method to check if the uri string is not empty, and parses it to return  uri.
    private fun uriOrNull(uriString: String): Uri? {
        return if (!TextUtils.isEmpty(uriString)) {
            Uri.parse(uriString)
        } else null
    }

    /**
     * Creates the input data bundle which includes the Uri to operate on
     * @return Data which contains the Image Uri as a String
     *
     *
     * input and output is passed in and out via Data objects. Data objects are lightweight containers (bundles)
     * for key/value pairs. They are meant to store a small amount of data that might
     * pass into and out from WorkRequests.
     */
    private fun createInputDataForUri(imageView : ImageView): Data {
        val builder = Data.Builder()
        if (mImageUri != null) {
            builder.putString(KEY_IMAGE_URI, mImageUri.toString())
            builder.putInt(KEY_IMAGE_VIEW_W, imageView.width)
            builder.putInt(KEY_IMAGE_VIEW_H, imageView.height)
        }
        return builder.build()
    }

    // Add a getter method for mSavedWorkStatus
    fun getOutputStatus(): LiveData<List<WorkStatus>>? {
        return mSavedWorkStatus
    }
  /*  // Add a getter method for mSavedWorkStatus
    fun getOutputStatusSingleWork(): LiveData<WorkStatus>? {
        return mSavedSingleWorkStatus
    }*/

    /**
     * Cancel work using the work's unique name
     */
    fun cancelWork() {
        mWorkManager.cancelUniqueWork(IMAGE_MANIPULATION_WORK_NAME)
    }

    fun resizeimageWork(imageView: ImageView) {
        /*
            OneTimeWorkRequest: A WorkRequest that will only execute once.
            PeriodicWorkRequest: A WorkRequest that will repeat on a cycle.
         */

        /*
           Sometimes you only want one chain of work to run at a time. For example, perhaps you have
           a work chain that syncs your local data with the server - you probably want to let the first
           data sync finish before starting a new one. To do this, you would use beginUniqueWork
           instead of beginWith; and you provide a unique String name. This names the entire chain
           of work requests so that you can refer to and query them together.
           You'll also need to pass in a ExisitingWorkPolicy. Your options are REPLACE, KEEP or APPEND.

           use REPLACE because if the user decides another before the current one is finished,
            we want to stop the current one and start the new
        */


        //to start a series of work requests
      var continuation = mWorkManager?.beginUniqueWork(
            IMAGE_MANIPULATION_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequest.from(CleanupWorker::class.java)
        )


        // single worker request implementation
        val resizeImgWork =  OneTimeWorkRequestBuilder<ImageResizeWorker>()
        .setInputData(createInputDataForUri(imageView))
        .addTag(TAG_OUTPUT)
        .build()

        // for using single work process request
        // mWorkManager?.enqueue(resizeImgWork)

       //for  a series of work requests
        continuation = continuation.then(resizeImgWork)

      /*
      // Create charging constraint
        val constraints = Constraints.Builder()
            .setRequiresCharging(true)
            .build()
*/
        // Actually start the work
        continuation.enqueue()
    }
}