package com.jimmy.mlkit.ui.views.qrCodeReader

import android.support.v7.util.DiffUtil
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.label.FirebaseVisionLabel

class QRDiffCallback : DiffUtil.ItemCallback<FirebaseVisionBarcode>() {

    override fun areItemsTheSame(oldItem: FirebaseVisionBarcode, newItem: FirebaseVisionBarcode): Boolean {
        return oldItem.rawValue == newItem.rawValue
    }

    override fun areContentsTheSame(oldItem: FirebaseVisionBarcode, newItem: FirebaseVisionBarcode): Boolean {
        return oldItem.hashCode() == newItem.hashCode()
    }
}