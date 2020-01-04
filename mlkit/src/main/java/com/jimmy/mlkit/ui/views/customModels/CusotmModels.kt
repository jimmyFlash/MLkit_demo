package com.jimmy.mlkit.ui.views.customModels

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.firebase.ml.custom.FirebaseModelDataType
import com.google.firebase.ml.custom.FirebaseModelInputOutputOptions
import com.google.firebase.ml.custom.FirebaseModelInterpreter

import com.jimmy.mlkit.R
import kotlinx.android.synthetic.main.cusotm_models_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.max
import kotlin.math.min
import kotlin.experimental.and

class CusotmModels : Fragment(), AdapterView.OnItemSelectedListener {

    override fun onNothingSelected(parent: AdapterView<*>?) = Unit

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        graphic_overlay.clear()
        selectedImage = decodeBitmapAsset(context!!, imagePaths[position])
        if (selectedImage != null) {
            // Get the dimensions of the View
            val targetedSize = targetedWidthHeight

            val targetWidth = targetedSize.first
            val maxHeight = targetedSize.second

            // Determine how much to scale down the image
            val scaleFactor = max(
                selectedImage!!.width.toFloat() / targetWidth.toFloat(),
                selectedImage!!.height.toFloat() / maxHeight.toFloat())

            val resizedBitmap = Bitmap.createScaledBitmap(
                selectedImage!!,
                (selectedImage!!.width / scaleFactor).toInt(),
                (selectedImage!!.height / scaleFactor).toInt(),
                true)

            image_view.setImageBitmap(resizedBitmap)
            selectedImage = resizedBitmap
        }
    }

    /** Data structure holding pairs of <label, confidence> for each inference result */
    data class LabelConfidence(val label: String, val confidence: Float)

    /** Current image being displayed in our app's screen */
    private var selectedImage: Bitmap? = null

    /** List of JPG files in our assets folder */
    private val imagePaths by lazy {
        resources.assets.list("")!!.filter { it.endsWith(".jpg") }
    }

    /** Labels corresponding to the output of the vision model. */
    private val labelList by lazy {
        BufferedReader(InputStreamReader(resources.assets.open(LABEL_PATH))).lineSequence().toList()
    }

    /** Preallocated buffers for storing image data. */
    private val imageBuffer = IntArray(DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y)

    // Gets the targeted width / height.
    private val targetedWidthHeight: Pair<Int, Int>
        get() {
            val targetWidth: Int
            val targetHeight: Int
            val maxWidthForPortraitMode = image_view.width
            val maxHeightForPortraitMode = image_view.height
            targetWidth = maxWidthForPortraitMode
            targetHeight = maxHeightForPortraitMode
            return Pair(targetWidth, targetHeight)
        }

    /** Input options used for our Firebase model interpreter */
    private val modelInputOutputOptions by lazy {
        val inputDims = arrayOf(DIM_BATCH_SIZE, DIM_IMG_SIZE_X, DIM_IMG_SIZE_Y, DIM_PIXEL_SIZE)
        val outputDims = arrayOf(DIM_BATCH_SIZE, labelList.size)
        FirebaseModelInputOutputOptions.Builder()
            .setInputFormat(0, FirebaseModelDataType.BYTE, inputDims.toIntArray())
            .setOutputFormat(0, FirebaseModelDataType.BYTE, outputDims.toIntArray())
            .build()
    }

    /** Firebase model interpreter used for the local model from assets */
    private lateinit var modelInterpreter: FirebaseModelInterpreter

    /** Initialize a local model interpreter from assets file */
    private fun createLocalModelInterpreter(): FirebaseModelInterpreter {
        throw NotImplementedError("TODO: complete this section")
    }

    /** Initialize a remote model interpreter from Firebase server */
    private suspend fun createRemoteModelInterpreter(): FirebaseModelInterpreter {
        throw NotImplementedError("TODO: complete this section")
    }


    private lateinit var viewModel: CusotmModelsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.cusotm_models_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CusotmModelsViewModel::class.java)
        val adapter = ArrayAdapter(
            activity,
            android.R.layout.simple_spinner_dropdown_item,
            imagePaths.mapIndexed { idx, _ -> "Image ${idx + 1}" })

        spinner.adapter = adapter
        spinner.onItemSelectedListener = this

        button_run.setOnClickListener {
            runModelInference()
        }

        // Disable the inference button until model is loaded
        button_run.isEnabled = false

        val uiScope = CoroutineScope(Dispatchers.Main)

        uiScope.launch {
            // ui thread
            val result = withContext(Dispatchers.IO) {// background thread
                // blocking call
                //modelInterpreter = createLocalModelInterpreter()
                //modelInterpreter = createRemoteModelInterpreter()
            }
            // ui thread
            button_run.isEnabled = true
        }
    }

    /** Uses model to make predictions and interpret output into likely labels. */
    private fun runModelInference() = selectedImage?.let { image ->
        throw NotImplementedError("TODO: complete this section")
    }

    /** Gets the top labels in the results. */
    @Synchronized
    private fun getTopLabels(inferenceOutput: Array<ByteArray>): List<String> {
        // Since we ran inference on a single image, inference output will have a single row.
        val imageInference = inferenceOutput.first()

        // The columns of the image inference correspond to the confidence for each label.
        return labelList.mapIndexed { idx, label ->
            LabelConfidence(label, (imageInference[idx] and 0xFF.toByte()) / 255.0f)

            // Sort the results in decreasing order of confidence and return only top 3.
        }.sortedBy { it.confidence }.reversed().map { "${it.label}:${it.confidence}" }
            .subList(0, min(labelList.size, RESULTS_TO_SHOW))
    }

    /** Writes Image data into a `ByteBuffer`. */
    @Synchronized
    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val imgData = ByteBuffer.allocateDirect(
            DIM_BATCH_SIZE * DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y * DIM_PIXEL_SIZE).apply {
            order(ByteOrder.nativeOrder())
            rewind()
        }
        val scaledBitmap =  Bitmap.createScaledBitmap(bitmap, DIM_IMG_SIZE_X, DIM_IMG_SIZE_Y,
            true)
        scaledBitmap.getPixels(imageBuffer, 0, scaledBitmap.width, 0, 0,
            scaledBitmap.width, scaledBitmap.height)
        // Convert the image to int points.
        var pixel = 0
        for (i in 0 until DIM_IMG_SIZE_X) {
            for (j in 0 until DIM_IMG_SIZE_Y) {
                val `val` = imageBuffer[pixel++]
                imgData.put((`val` shr 16 and 0xFF).toByte())
                imgData.put((`val` shr 8 and 0xFF).toByte())
                imgData.put((`val` and 0xFF).toByte())
            }
        }
        return imgData
    }

    companion object {
        fun newInstance() = CusotmModels()

        /** Name of the label file stored in Assets. */
        private const val LABEL_PATH = "labels.txt"

        /** Name of the remote model in Firebase. */
        private const val REMOTE_MODEL_NAME = "mobilenet_v1_224_quant"

        /** Number of results to show in the UI. */
        private const val RESULTS_TO_SHOW = 3

        /** Dimensions of inputs. */
        private const val DIM_BATCH_SIZE = 1
        private const val DIM_PIXEL_SIZE = 3
        private const val DIM_IMG_SIZE_X = 224
        private const val DIM_IMG_SIZE_Y = 224

        /** Utility function for loading and resizing images from app asset folder. */
        fun decodeBitmapAsset(context: Context, filePath: String): Bitmap =
            context.assets.open(filePath).let {
                BitmapFactory.decodeStream(it)
            }


    }

}
