

package com.jimmy.ml_firebase.ui.activities

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.jimmy.actions.Actions
import com.jimmy.ml_firebase.R
import com.jimmy.ml_firebase.databinding.ActivitySplashBinding
import com.jimmy.ml_firebase.ui.start
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)

        twitterSnap.setOnClickListener {

            MainActivity::class.start(this@SplashActivity, false)

        }

        deliciousFood.setOnClickListener {

            ImageRecognitionActivity::class.start(this@SplashActivity, false)
        }

        googleLensClone.setOnClickListener {
            startActivity(Intent(Actions.openGoogleLens(this@SplashActivity)))
        }

    }
}