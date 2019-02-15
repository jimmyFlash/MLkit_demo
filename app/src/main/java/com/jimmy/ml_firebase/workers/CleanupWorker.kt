package com.jimmy.ml_firebase.workers

import android.content.Context
import android.text.TextUtils
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.jimmy.ml_firebase.Constants
import java.io.File

data class CleanupWorker(val ctx : Context, val workP : WorkerParameters) : Worker(ctx, workP) {

    private val TAG by lazy { CleanupWorker::class.java.simpleName }

    override fun doWork(): Result {
        try {
            val outputDirectory = File(applicationContext.filesDir, Constants.OUTPUT_PATH)
            if (outputDirectory.exists()) {
                val entries = outputDirectory.listFiles()
                if (entries != null && entries.isNotEmpty()) {
                    for ( entry in  entries) {
                        val name = entry.name
                        if (!TextUtils.isEmpty(name) && (name.endsWith(".png") || name.endsWith(".jpg")) ) {
                            val deleted = entry.delete()
                            Log.e(TAG, String.format("Deleted %s - %s",  name, deleted))
                        }
                    }
                }
            }
            return Result.SUCCESS
        } catch ( exception : Exception) {
            Log.e(TAG, "Error cleaning up", exception)
            return Result.FAILURE
        }
    }

}