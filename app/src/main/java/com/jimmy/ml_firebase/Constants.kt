package com.jimmy.ml_firebase

object Constants {

    // Notification Channel constants

    // Name of Notification Channel for verbose notifications of background work
    const val VERBOSE_NOTIFICATION_CHANNEL_NAME = "Verbose WorkManager Notifications"
    const val VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION = "Shows notifications whenever work starts"
    const val NOTIFICATION_TITLE = "WorkRequest Starting"
    const val CHANNEL_ID = "VERBOSE_NOTIFICATION"
    const val NOTIFICATION_ID = 1
    const val KEY_IMAGE_URI: String = "imageURI"
    const val KEY_IMAGE_VIEW_W: String = "imageViewW"
    const val KEY_IMAGE_VIEW_H: String = "imageViewH"
    // The name of the image manipulation work
    val IMAGE_MANIPULATION_WORK_NAME = "image_manipulation_work"
    const val KEY_IMAGE_EDITED_URI: String = "imageEdited"
    val TAG_OUTPUT = "OUTPUT"
    val TAG_OUTPUT_ID = "jimmyjojoojoijoijoi"
    val OUTPUT_PATH = "blur_filter_outputs"

    val PERMISSION_REQUEST_CODE = 1


}// Ensures this class is never instantiated