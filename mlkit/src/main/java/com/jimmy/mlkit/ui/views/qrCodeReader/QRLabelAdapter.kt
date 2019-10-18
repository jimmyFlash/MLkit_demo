package com.jimmy.mlkit.ui.views.qrCodeReader

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.label.FirebaseVisionLabel
import com.jimmy.mlkit.R
import kotlinx.android.synthetic.main.item_row.view.*
import java.util.*

class QRLabelAdapter :
    ListAdapter<FirebaseVisionBarcode, QRLabelAdapter.ItemHolder>(QRDiffCallback()) {

    lateinit var context: Context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        context = parent.context
        return ItemHolder(LayoutInflater.from(context).inflate(R.layout.item_row, parent, false))
    }


    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bindDevice(currentItem )
    }

    inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindDevice(currentItem: FirebaseVisionBarcode) {

            var title :String? = null
            var data :String? = null
            when (currentItem.valueType) {
                //Handle the URL here
                FirebaseVisionBarcode.TYPE_URL -> {
                    title = currentItem.url?.title
                    data = currentItem.url?.url
                }
                // Handle the contact info here, i.e. address, name, phone, etc.
                FirebaseVisionBarcode.TYPE_CONTACT_INFO ->   {
                    title = "${currentItem.contactInfo?.title} , ${currentItem.contactInfo?.name}"
                    data = "${currentItem.contactInfo?.organization} ," +
                            "${currentItem.contactInfo?.emails} \n ${currentItem.contactInfo?.phones}"
                }
                // Handle the wifi here, i.e. firebaseBarcode.wifi.ssid, etc.
                FirebaseVisionBarcode.TYPE_WIFI -> {
                    title = "${currentItem.wifi?.ssid}"
                    data = "${currentItem.wifi?.password} "
                }
                FirebaseVisionBarcode.TYPE_CALENDAR_EVENT -> {
                    title = "${currentItem.calendarEvent?.description} @ ${currentItem.calendarEvent?.location}"
                    data = "${currentItem.calendarEvent?.start} - ${currentItem.calendarEvent?.end}"
                }
                FirebaseVisionBarcode.TYPE_PRODUCT -> {
                    title = "ProdcutID"
                    data = "${currentItem.displayValue}"
                }
                FirebaseVisionBarcode.TYPE_DRIVER_LICENSE -> {
                    title = "Driver license : ${currentItem.driverLicense?.firstName}  " +
                            "${currentItem.driverLicense?.lastName}"
                    data = "No#: ${currentItem.driverLicense?.licenseNumber} " +
                            "\n expires: ${currentItem.driverLicense?.expiryDate} issuer: ${currentItem.driverLicense?.issuingCountry}"
                }
                FirebaseVisionBarcode.TYPE_GEO -> {
                    title = "Location"
                    data = "Lat: ${currentItem.geoPoint?.lat} Lng: ${currentItem.geoPoint?.lng}"
                }
                FirebaseVisionBarcode.TYPE_ISBN -> {
                    title = "ISBN"
                    data = "${currentItem.displayValue}"
                }
                FirebaseVisionBarcode.TYPE_TEXT -> {
                    title = "Text"
                    data = "${currentItem.displayValue}"
                }
                FirebaseVisionBarcode.TYPE_EMAIL -> {
                    title = "Email"
                    data = "Address: ${currentItem.email?.address} \n Subject: ${currentItem.email?.subject} \n Body: ${currentItem.email?.body}"
                }
                FirebaseVisionBarcode.TYPE_PHONE -> {
                    title = "Phone#"
                    data = "no#: ${currentItem.phone?.number} type: ${currentItem.phone?.type}"
                }
                FirebaseVisionBarcode.TYPE_SMS -> {
                    title = "Sms#"
                    data = "msg: ${currentItem.sms?.message} \n from: ${currentItem.sms?.phoneNumber}"
                }
                //Handle more type of Barcodes
            }
            itemView.itemName.text = title ?: ""
            itemView.itemAccuracy.text = data ?: ""
        }

    }

}