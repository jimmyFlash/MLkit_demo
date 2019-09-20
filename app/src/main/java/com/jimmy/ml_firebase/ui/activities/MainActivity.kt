package com.jimmy.ml_firebase.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.res.Configuration
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.provider.MediaStore
import android.support.constraint.motion.MotionLayout
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.work.WorkStatus
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.jimmy.actions.uiutils.hidesupportActionBar
import com.jimmy.actions.uiutils.makeFullScreen
import com.jimmy.ml_firebase.Constants
import com.jimmy.ml_firebase.permissions.PermissionManager
import com.jimmy.ml_firebase.R
import com.jimmy.ml_firebase.databinding.ActivityMainBinding
import com.jimmy.ml_firebase.uidataproviders.viewmodel.MainActivityViewModel


class MainActivity : AppCompatActivity() {

    // draw rect around text block and enable click handler
    fun showHandle(text: String, boundingBox: Rect?) {
        binding.overlay.addText(text, boundingBox)
    }

    // draws a bounding box defined by the rect boundaries
    fun showBox(boundingBox: Rect?) {
        binding.overlay.addBox(boundingBox)
    }

    lateinit var binding : ActivityMainBinding
    lateinit var mViewModel : MainActivityViewModel

    private val MEDIA_PICK_CODE :Int = 1
    private val  recalTimeout : Long = 500

    private val REQUEST_MULTI_PERMISSION = 10// permission for external storage
    private var  pm: PermissionManager? = null// permission manager ref.

    private var fVT : FirebaseVisionText? = null


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.title = getString(R.string.app_name)


        // Get the ViewModel
        mViewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)

        // introduce  viewmodel instance to binding variable (viewmodel)
        binding.viewmodel = mViewModel

        binding.imageView.setImageDrawable(getDrawable(R.drawable.ic_launcher_foreground))
        // set up click listener for the FAB button
        setUpNewImageListener()

        /*
         handling device orientation change,
         need to resize the original image to fit new screen dimensions & and reinitialize observers
         */
        if(mViewModel.getImageUri() != null){
            Log.e("configuration change", mViewModel.getImageUri().toString())


            val countDownTimer = object : CountDownTimer(recalTimeout, 100){
                override fun onFinish() {
                    Log.e("image view dimensions",
                            ", width: " + binding.imageView.width +
                            ", height: " + binding.imageView.height )

                    mViewModel.resizeimageWork(binding.imageView)// resize the image using workmanager
                }

                override fun onTick(millisUntilFinished: Long) {
                 //
                }

            }

            countDownTimer.start()

        }


        binding.motionLayout.setTransitionListener(object: MotionLayout.TransitionListener{
            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
              //
            }

            @SuppressLint("RestrictedApi")
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
                if(mViewModel.getImageUri() != null){
                    binding.fab.visibility = View.INVISIBLE
                }
            }

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, progress: Float) {
                //Print each floating-point number to Logcat//
                Log.d("TAG", "Progress:$progress")
            }

            @SuppressLint("RestrictedApi")
            override fun onTransitionCompleted(p0: MotionLayout?, currentId: Int) {
                //If our button is in the ending_set position...//

                if(currentId == R.id.end) {

                    if(mViewModel.getImageUri() != null){
                        binding.fab.visibility = View.VISIBLE
                    }

                    //...then move it back to the starting position//
//                    binding.motionLayout.transitionToStart()
                }
            }

        })


        /*
            Evaluates the pending bindings, updating any Views that have expressions bound to modified
            variables. This must be run on the UI thread.
        */
        binding.executePendingBindings()

        // register observers everytime activity is created/recreated
        addObservers()
    }

    /**
     * click handler for the FAB
     */
    private fun setUpNewImageListener() {

        binding.fab.setOnClickListener {

            // request permission for external storage
            pm = PermissionManager.PermissionBuilder(this, REQUEST_MULTI_PERMISSION)
                .addPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .build()
        }
    }


    /**
     * activity result handler
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, imageReturnedIntent: Intent?) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent)
        when (requestCode) {
            MEDIA_PICK_CODE -> if (resultCode == Activity.RESULT_OK) {

                resetObservables()

                // get loaded image data from mdia select intent
                imageReturnedIntent?.data.let {

                    mViewModel.setImageUri(it.toString())// call setter method in VM to set image Uri
                    if (mViewModel.getImageUri() != null) {// validate the Uri
                        Log.e("image uri", mViewModel.getImageUri().toString()  +
                                ", width: " + binding.imageView.width +
                                ", height: " + binding.imageView.height )
                        // resize image bitmap using VM method that calls for worker
                        mViewModel.resizeimageWork(binding.imageView)
                    }
                }
            }
        }

//        addObservers()
    }

    /**
     * helper method
     * clear all observables
     * clear/ reset image view to default image
     * reset viewmodel data
     */
    fun resetObservables(){
        binding.overlay.clear()
        binding.imageView.setImageBitmap(null)
        binding.imageView.setImageDrawable(getDrawable(R.drawable.ic_launcher_foreground))
        mViewModel.resetBlocks()
        mViewModel.clearUri()
    }



    private fun addObservers(){


        /*
            Prunes all eligible finished work from the internal database. Eligible work must be finished
            (State.SUCCEEDED, State.FAILED, or State.CANCELLED), with zero unfinished dependents.
            Use this method with caution; by invoking it, you
            (and any modules and libraries in your codebase) will no longer be able to observe
            the WorkStatus of the pruned work
         */
        mViewModel.getWrkmanagerIns().pruneWork()

        Log.e("outputstatus", "${mViewModel.getOutputStatus()}")
        // Show work status for the tagged work request, through livedata observer
        mViewModel.getOutputStatus()?.observe(this, workStatusesObserver() )

        //observe the changes to the state of the read text vision object
        mViewModel.mtextBlocks.observe(this,traceTwitterHandles())

        // Observer changes to the mtextCloud object
        mViewModel.mtextCloud.observe(this, traceDocumentTextInCloudHnalder())
    }

    private fun removeObservers(){

        Log.e("outputstatus", "${mViewModel.getOutputStatus()}")
        // Show work status for the tagged work request, through livedata observer
        mViewModel.getOutputStatus()?.removeObserver(workStatusesObserver() )

        //observe the changes to the state of the read text vision object
        mViewModel.mtextBlocks.removeObserver(traceTwitterHandles())

        // Observer changes to the mtextCloud object
        mViewModel.mtextCloud.removeObserver(traceDocumentTextInCloudHnalder())
    }

    // on device ML text vision state observer
    private fun traceTwitterHandles(): Observer<FirebaseVisionText>{
        return Observer{texts ->

            Log.e("-----------", "${texts?.textBlocks?.size}  &  ${fVT == texts}")

            fVT = texts

            val blocks = texts?.textBlocks
            if (blocks?.size == 0) {
                Toast.makeText(this, "No text detected", Toast.LENGTH_LONG).show()
                return@Observer
            }
            blocks?.forEach { block ->
                block.lines.forEach { line ->
                    line.elements.forEach { element ->

                        // to show borders around all detected text blocks
                        //showBox(element.boundingBox)

                        // look for filter text blocks for regex matching thr twitter pattern
                        if (mViewModel.looksLikeHandle(element.text)) {
                            // draw handles around matching text blocks
                            showHandle(element.text, element.boundingBox)
                        }
                    }
                }
            }
        }
    }


    // Cloud ML observer handler
    private fun traceDocumentTextInCloudHnalder(): Observer<FirebaseVisionDocumentText>{

        return Observer { texts ->
            if (texts == null) {
                Toast.makeText(this, "No text detected", Toast.LENGTH_LONG).show()
                return@Observer
            }
            val resultText = texts.text
            texts.blocks.forEach{ block ->
                block.paragraphs.forEach{
                    it.words.zipWithNext{a, b->
                        // 1
                        val word = mViewModel.wordToString(a) + mViewModel.wordToString(b)
                        // 2
                        MainActivityViewModel.WordPair(word, b)
                    }
                        .filter { wordPair -> mViewModel.looksLikeHandle(wordPair.word) }
                        .forEach { pair ->
                            // 3
                            showHandle(pair.word, pair.handle.boundingBox)
                        }
                }
            }

        }

    }

    /**
     * observer handler for workmanager status of image resizing process
     */
    fun workStatusesObserver(): Observer<List<WorkStatus>> {
        return Observer { listOfWorkStatuses ->

            // Note that these next few lines grab a single WorkStatus if it exists
            // This code could be in a Transformation in the ViewModel; they are included here
            // so that the entire process of displaying a WorkStatus is in one location.

            // If there are no matching work statuses, do nothing
            if (listOfWorkStatuses == null || listOfWorkStatuses.isEmpty()) {
                return@Observer
            }

            // We only care about the one output status.
            // Every continuation has only one worker tagged TAG_OUTPUT
            val workStatus = listOfWorkStatuses[0]
            val finished = workStatus.state.isFinished

            if (finished) {
                mViewModel.isLoading.set(false)// set the loading boolean observable to false to hide binded loader
                //If the WorkStatus is finished, get the output data, using workStatus.getOutputData().
                val outputData = workStatus.outputData

                //get the output URI, remember that it's stored with the Constants.KEY_IMAGE_URI key.
                val outputImageUri = outputData.getString(Constants.KEY_IMAGE_EDITED_URI)
                // If there is an output file show "See File" button
                if (!TextUtils.isEmpty(outputImageUri)) {

                    val bitmapResized = MediaStore.Images.Media.getBitmap(this.contentResolver, Uri.parse(outputImageUri))
                    binding.imageView.setImageBitmap(bitmapResized)
                    // set up firebase cloud ML listener
//                    setUpCloudSearch(bitmapResized)
                    binding.overlay.clear()
                    // run on device ML text recognition
                    mViewModel.runTextRecognition(bitmapResized!!)
                }
            }else{
                mViewModel.isLoading.set(true)// set observable property to true to show progress indicator
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * permission request result handler
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        val result = when (requestCode) {
            REQUEST_MULTI_PERMISSION-> {

                val allSet = pm!!.multiplePermissionProcessor(permissions, grantResults)
                if (allSet) {

                    Log.e("PERMISSIOOOON", "ALL GRANTED")
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(intent, MEDIA_PICK_CODE)
                } else {
                    val deniedPerm = pm!!.deniedPermissionsList
                    println("PERMISSIOOOON DENIED ${deniedPerm.size} deniedPerm.toString()")
                    // can check if a certain permission you're instrested in exits is denied, if not you proceed or halt
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }

    }

    // use Google's cloud firebase ML
    private fun setUpCloudSearch(selectedImageBitmap: Bitmap?) {
        binding.fab.setImageResource(R.drawable.ic_cloud_black_24dp)
        binding.fab.setBackgroundColor(getColor(R.color.colorPrimaryDark))
        binding.fab.setOnClickListener {
            binding.overlay.clear()
            mViewModel.runCloudTextRecognition(selectedImageBitmap!!)
        }
    }


    override fun onResume() {
        super.onResume()
        binding.overlay.clear()
        binding.imageView.setImageBitmap(null)
        binding.imageView.setImageDrawable(getDrawable(R.drawable.ic_launcher_foreground))
        object : CountDownTimer(recalTimeout, 100) {
            override fun onTick(millisUntilFinished: Long) {

            }
            override fun onFinish() {
                //Animate to the end ConstraintSet//
                 binding.motionLayout.transitionToEnd()
            }
        }.start()
    }


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
    }


}
