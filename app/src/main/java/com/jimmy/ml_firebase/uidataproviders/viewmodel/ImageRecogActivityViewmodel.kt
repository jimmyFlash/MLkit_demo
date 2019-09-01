package com.jimmy.ml_firebase.uidataproviders.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.ml.vision.cloud.label.FirebaseVisionCloudLabel
import com.google.firebase.ml.vision.label.FirebaseVisionLabel
import com.jimmy.ml_firebase.R
import com.jimmy.ml_firebase.logicrepository.RepositoryManager
import com.mindorks.paracamera.Camera

class ImageRecogActivityViewmodel(application : Application) : AndroidViewModel(application), RepositoryManager.StatusCallBack {
    

    val  TAG:String? = ImageRecogActivityViewmodel::class.java.simpleName


    val repositoryManager:RepositoryManager = RepositoryManager()

    override fun successCloud(detectedList: List<FirebaseVisionCloudLabel>) {
        successHandler()
        responseCardViewState.value = hasDeliciousFood(detectedList.map { it.label.toString() })
    }

    override fun success(detectedList: List<FirebaseVisionLabel>) {
        successHandler()
        responseCardViewState.value = hasDeliciousFood(detectedList.map { it.label.toString() })
    }

    override fun fail(exception: Exception) {
        isLoading.set(false)
        error.value = R.string.error
    }

    private val ctx = getApplication<Application>()

    private val  camera : MutableLiveData<Camera> = MutableLiveData()// camera object tracker
    val error : MutableLiveData<Int> = MutableLiveData()// error code / string resource id
    val responseCardViewState: MutableLiveData<Boolean> = MutableLiveData()// display state of the

//    val contentId = ObservableInt()

    val isLoading = ObservableField(false)// loading state bounded to xml

    val hasDetectedImage = ObservableField(false) //


    init {
        responseCardViewState.value = false
    }

    // setter and getter for the camera object to track camera updates
    fun setCamera (cam :Camera){
        camera.value  =  cam
    }

    fun getCamera() :MutableLiveData<Camera> {
        return camera
    }


    /**
     * handlers image recognition and processing of the image bitmap taken with camera
     * supply the FirebaseVisionLabelDetector with a picture,
     * and it will return an array of FirebaseVisionLabels with the objects found
     */
    fun detectDeliciousFoodOnDevice(bitmap: Bitmap) {
        hasDetectedImage.set(false)
        isLoading.set(true)
        repositoryManager.detectDeliciousFoodOnDevice(bitmap, this@ImageRecogActivityViewmodel)
    }


    fun detectDeliciousFoodOnCloud(bitmap: Bitmap) {
        hasDetectedImage.set(false)
        isLoading.set(true)
        repositoryManager.detectDeliciousFoodOnCloud(bitmap, this@ImageRecogActivityViewmodel)
    }


    private fun successHandler(){
        isLoading.set(false)
        error.value = 0
        hasDetectedImage.set(true)
    }

    /**
     * helper method to return flag if Food is detected in the image
     *  receives an array of Strings and returns true if it finds the word “Food.”
     */
    private fun hasDeliciousFood(items: List<String>): Boolean {
        for (result in items) {

            if (result.contains("Food", true)){
                Log.e(TAG, "Food found is $result")
                return true
            }
        }
        return false
    }


}