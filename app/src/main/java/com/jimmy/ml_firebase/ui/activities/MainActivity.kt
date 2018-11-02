package com.jimmy.ml_firebase.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.work.WorkStatus
import com.jimmy.ml_firebase.Constants
import com.jimmy.ml_firebase.PermissionManager
import com.jimmy.ml_firebase.R
import com.jimmy.ml_firebase.databinding.ActivityMainBinding
import com.jimmy.ml_firebase.uidataproviders.viewmodel.MainActivityViewModel


class MainActivity : AppCompatActivity(), MainActivityViewModel.View {

    override fun showNoTextMessage() {
        Toast.makeText(this, "No text detected", Toast.LENGTH_LONG).show()
    }

    override fun showHandle(text: String, boundingBox: Rect?) {
        binding.overlay.addText(text, boundingBox)
    }

    override fun showBox(boundingBox: Rect?) {
        binding.overlay.addBox(boundingBox)
    }

    @SuppressLint("RestrictedApi")
    override fun showProgress() {
        binding.progressBar.visibility = View.VISIBLE
        binding.fab.visibility = View.GONE
    }

    @SuppressLint("RestrictedApi")
    override fun hideProgress() {
        binding.progressBar.visibility = View.GONE
        binding.fab.visibility = View.VISIBLE
    }

    lateinit var binding : ActivityMainBinding
    lateinit var mViewModel : MainActivityViewModel

    private val MEDIA_PICK_CODE :Int = 1

    private val REQUEST_MULTI_PERMISSION = 10
    private var  pm: PermissionManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // initiate binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.title = getString(R.string.app_name)

        // Get the ViewModel
        mViewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)


        setUpNewImageListener()
       /*
           Evaluates the pending bindings, updating any Views that have expressions bound to modified
           variables. This must be run on the UI thread.
       */
        binding.executePendingBindings()
    }

    private fun setUpNewImageListener() {

              binding.fab.setOnClickListener { _ ->

                  pm = PermissionManager.PermissionBuilder(this, REQUEST_MULTI_PERMISSION)
                      .addPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                      .build()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, imageReturnedIntent: Intent?) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent)
        when (requestCode) {
            MEDIA_PICK_CODE -> if (resultCode == Activity.RESULT_OK) {

                imageReturnedIntent?.data.let {

                    mViewModel.setImageUri(it.toString())// call setter method in VM to set image data
                    if (mViewModel.getImageUri() != null) {

                        // resize image bitmap using VM method that calls for worker
                        val selectedImageBitmap = mViewModel.resizeimageWork(binding.imageView)
                    }

                }
            }
        }

        // Show work status for the tagged work request, through livedata observer
        mViewModel.getOutputStatus()?.observe(this, workStatusesObserver() )

    }


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

                hideProgress()
                //If the WorkStatus is finished, get the output data, using workStatus.getOutputData().
                val outputData = workStatus.outputData

                //get the output URI, remember that it's stored with the Constants.KEY_IMAGE_URI key.
                val outputImageUri = outputData.getString(Constants.KEY_IMAGE_EDITED_URI)
                // If there is an output file show "See File" button
                if (!TextUtils.isEmpty(outputImageUri)) {

                    val bitmapResized = MediaStore.Images.Media.getBitmap(this.contentResolver, Uri.parse(outputImageUri))
                    binding.imageView.setImageBitmap(bitmapResized)
                    setUpCloudSearch(bitmapResized)
                    binding.overlay.clear()
                    mViewModel.runTextRecognition(bitmapResized!!)
                }
            }else{
                showProgress()
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

    private fun setUpCloudSearch(selectedImageBitmap: Bitmap?) {
        binding.fab.setImageResource(R.drawable.ic_cloud_black_24dp)
        binding.fab.setBackgroundColor(getColor(R.color.colorPrimaryDark))
        binding.fab.setOnClickListener {
            binding.overlay.clear()
            mViewModel.runCloudTextRecognition(selectedImageBitmap!!)
        }
    }


}
