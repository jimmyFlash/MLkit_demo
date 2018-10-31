package com.jimmy.ml_firebase.workers

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.jimmy.ml_firebase.Constants
import com.jimmy.ml_firebase.Constants.KEY_IMAGE_EDITED_URI
import com.jimmy.ml_firebase.workers.WorkerUtils.resizeImage
import com.jimmy.ml_firebase.workers.WorkerUtils.writeBitmapToFile


data class ImageResizeWorker(val ctx : Context, val workP : WorkerParameters) : Worker(ctx, workP) {

    private val TAG = ImageResizeWorker::class.java!!.simpleName

    // private val TAG by lazy { ImageResizeWorker::class.java.simpleName }



    override fun doWork(): Result {
        val applicationContext = applicationContext

        // get the image uri from the data object passed as input
        val resourceUri = workP.inputData
        var imgUri = resourceUri.getString(Constants.KEY_IMAGE_URI)
        var imgW = resourceUri.getInt(Constants.KEY_IMAGE_VIEW_W, 0)
        var imgH = resourceUri.getInt(Constants.KEY_IMAGE_VIEW_H, 0)

        try{
            if (TextUtils.isEmpty(imgUri)) {
                Log.e(TAG, "Invalid input uri")
//                throw  IllegalArgumentException("Invalid input uri")
                return Result.FAILURE
            }

            val resolver = ctx.contentResolver

            val bitmap = resizeImage(Uri.parse(imgUri), applicationContext, imgH, imgW)

            val writeBmpTfile = writeBitmapToFile(ctx, bitmap!!)

            // called this method to pass an Data object to Worker that is dependent on this one
            outputData = Data.Builder()
                .putString(KEY_IMAGE_EDITED_URI, writeBmpTfile.toString())
                .build()

            return Result.SUCCESS
        }catch (throwable : Throwable ) {

            // Technically WorkManager will return WorkerResult.FAILURE
            // but it's best to be explicit about it.
            // Thus if there were errors, we're return FAILURE
            Log.e(TAG, "Error applying blur", throwable)

            return Result.FAILURE
        }


    }


}