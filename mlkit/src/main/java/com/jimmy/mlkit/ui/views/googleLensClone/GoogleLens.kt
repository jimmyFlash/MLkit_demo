package com.jimmy.mlkit.ui.views.googleLensClone


import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import com.jimmy.mlkit.R
import com.jimmy.mlkit.ui.utils.ExitWithAnimation
import com.jimmy.mlkit.ui.utils.exitCircularReveal
import com.jimmy.mlkit.ui.utils.findLocationOfCenterOnTheScreen
import com.jimmy.mlkit.ui.utils.open
import com.jimmy.mlkit.ui.views.BaseCameraActivity
import kotlinx.android.synthetic.main.activity_google_lens.*

class GoogleLens : BaseCameraActivity() {

    lateinit var mViewModel : GoogleLensViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_lens)
        setSupportActionBar(toolbar)
        mViewModel = ViewModelProviders.of(this).get(GoogleLensViewModel::class.java)

        val positions = container.findLocationOfCenterOnTheScreen()


        val myCounter = object : CountDownTimer( 1500, 1000){
            override fun onFinish() {

                supportFragmentManager.open {
                    replace(R.id.container, GoogleLensFrag.newInstance(positions))
                }
            }

            override fun onTick(millisUntilFinished: Long) {
                // do nothing here.
            }
        }.apply {
            start()
        }

    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(supportFragmentManager?.fragments!=null && supportFragmentManager?.fragments!!.size > 0)
            for (i in 0 until supportFragmentManager?.fragments!!.size) {
                val fragment= supportFragmentManager?.fragments!![i]
                fragment.onActivityResult(requestCode, resultCode, data)
            }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(supportFragmentManager?.fragments!=null && supportFragmentManager?.fragments!!.size > 0)
            for (i in 0 until supportFragmentManager?.fragments!!.size) {
                val fragment= supportFragmentManager?.fragments!![i]
                fragment.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }

    }

    override fun onBackPressed() {
        /*with(supportFragmentManager.findFragmentById(R.id.container)) {
            // Check if the current fragment implements the [ExitWithAnimation] interface or not
            // Also check if the [ExitWithAnimation.isToBeExitedWithAnimation] is `true` or not
            if ((this as? ExitWithAnimation)?.isToBeExitedWithAnimation() == true) {
                if (this.posX == null || this.posY == null) {
                    super.onBackPressed()
                } else {
                    this.view?.exitCircularReveal(this.posX!!, this.posY!!) {
                        super.onBackPressed()
                    } ?: super.onBackPressed()
                }
            } else {
                super.onBackPressed()
            }
        }*/
        super.onBackPressed()
    }
}
