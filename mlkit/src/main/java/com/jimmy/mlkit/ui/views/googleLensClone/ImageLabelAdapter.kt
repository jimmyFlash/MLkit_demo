package com.jimmy.mlkit.ui.views.googleLensClone

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.ml.vision.cloud.label.FirebaseVisionCloudLabel
import com.google.firebase.ml.vision.label.FirebaseVisionLabel
import com.jimmy.mlkit.R
import kotlinx.android.synthetic.main.item_row.view.*
import java.util.*

class ImageLabelAdapter (private var firebaseVisionList: MutableList<Any>, private val isCloud: Boolean) :
    RecyclerView.Adapter<ImageLabelAdapter.ItemHolder>() {

    lateinit var context: Context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        context = parent.context
        return ItemHolder(LayoutInflater.from(context).inflate(R.layout.item_row, parent, false))
    }

    override fun getItemCount(): Int = firebaseVisionList.size

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val currentItem = firebaseVisionList[position]
        if (isCloud)
            holder.bindCloud(currentItem as FirebaseVisionCloudLabel)
        else
            holder.bindDevice(currentItem as FirebaseVisionLabel)
    }

    inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindCloud(currentItem: FirebaseVisionCloudLabel) {

            Log.e("bindCloud".toUpperCase(Locale.getDefault()), "${currentItem.confidence} ${currentItem.label}  ")
            when {
                currentItem.confidence > .70 -> itemView.itemAccuracy.setTextColor(
                    ContextCompat.getColor(context, R.color.green))
                currentItem.confidence < .30 -> itemView.itemAccuracy.setTextColor(
                    ContextCompat.getColor(context, R.color.red))
                else -> itemView.itemAccuracy.setTextColor(ContextCompat.getColor(context, R.color.orange))
            }
            itemView.itemName.text = currentItem.label
            itemView.itemAccuracy.text = "Probability : ${(currentItem.confidence * 100).toInt()}%"
        }

        fun bindDevice(currentItem: FirebaseVisionLabel) {

            Log.e("bindDevice".toUpperCase(Locale.getDefault()), "${currentItem.confidence} ${currentItem.label}  ")
            //Toast.makeText(context, "${currentItem.confidence} ${currentItem.label}" , Toast.LENGTH_SHORT).show()
            when {
                currentItem.confidence > .70 -> itemView.itemAccuracy.setTextColor(
                    ContextCompat.getColor(context, R.color.green))
                currentItem.confidence < .30 -> itemView.itemAccuracy.setTextColor(
                    ContextCompat.getColor(context, R.color.red))
                else -> itemView.itemAccuracy.setTextColor(ContextCompat.getColor(context, R.color.orange))
            }
            itemView.itemName.text = currentItem.label
            itemView.itemAccuracy.text = "Probability : ${(currentItem.confidence * 100).toInt()}%"
        }

    }

    fun updateAdapter(newItems : List<Any>){
        firebaseVisionList.clear()
        firebaseVisionList.addAll(newItems)
        notifyDataSetChanged()
    }

    /**
     * diffUtils implementation for comparator
     */
   /* companion object {
        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<Any>() {
            override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean =
                oldItem.fullName == newItem.fullName

            override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean =
                oldItem == newItem
        }
    }*/


}