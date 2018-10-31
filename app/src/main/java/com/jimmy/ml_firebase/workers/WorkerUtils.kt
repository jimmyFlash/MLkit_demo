package com.jimmy.ml_firebase.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.util.Base64
import com.jimmy.ml_firebase.Constants
import com.jimmy.ml_firebase.R
import android.graphics.BitmapFactory
import java.nio.file.Files.exists
import java.util.UUID.randomUUID
import android.support.annotation.NonNull
import java.io.*
import java.util.*


object WorkerUtils {


    /**
     * Create a Notification that is shown as a heads-up notification if possible.
     *
     * For this codelab, this is used to show a notification so that you know when different steps
     * of the background work chain are starting
     *
     * @param message Message shown on the notification
     * @param context Context needed to create Toast
     */
    fun makeStatusNotification(message: String, context: Context) {

        // Make a channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            val name = Constants.VERBOSE_NOTIFICATION_CHANNEL_NAME
            val description = Constants.VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(Constants.CHANNEL_ID, name, importance)
            channel.description = description

            // Add the channel
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }

        // Create the notification
        val builder = NotificationCompat.Builder(context, Constants.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(Constants.NOTIFICATION_TITLE)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(LongArray(0))

        // Show the notification
        NotificationManagerCompat.from(context).notify(Constants.NOTIFICATION_ID, builder.build())
    }


    fun resizeImage(selectedImage: Uri, ctx : Context, viewHeight : Int, viewWidth : Int): Bitmap? {
        return getBitmapFromUri(selectedImage, ctx)?.let {
            val scaleFactor = Math.max(
                it.width.toFloat() / viewWidth.toFloat(),
                it.height.toFloat() / viewHeight.toFloat())
            Bitmap.createScaledBitmap(it,
                (it.width / scaleFactor).toInt(),
                (it.height / scaleFactor).toInt(),
                true)
        }
    }

    fun getBitmapFromUri(filePath: Uri, context : Context): Bitmap? {
        return MediaStore.Images.Media.getBitmap(context.contentResolver, filePath)
    }

    fun bitMapToString(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos as OutputStream?)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }


    fun stringToBitMap(encodedString: String): Bitmap? {
        try {
            val encodeByte = Base64.decode(encodedString, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
        } catch (e: Exception) {
            e.message
            return null
        }
    }

     fun getImageUri(context: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, inImage, "Title", "Resized image")
        return Uri.parse(path)
    }

    @Throws(FileNotFoundException::class)
    fun writeBitmapToFile(applicationContext: Context, bitmap: Bitmap): Uri {

        val name = String.format("resized-image-output%s.png", UUID.randomUUID().toString())
        val outputDir = File(applicationContext.filesDir, Constants.OUTPUT_PATH)
        if (!outputDir.exists()) {
            outputDir.mkdirs() // should succeed
        }
        val outputFile = File(outputDir, name)
        var out: FileOutputStream? = null
        try {
            out = FileOutputStream(outputFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 /* ignored for PNG */, out)
        } finally {
            if (out != null) {
                try {
                    out.close()
                } catch (ignore: IOException) {
                    //
                }

            }
        }
        return Uri.fromFile(outputFile)
    }

}