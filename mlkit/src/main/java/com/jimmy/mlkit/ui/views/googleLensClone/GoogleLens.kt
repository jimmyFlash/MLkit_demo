package com.jimmy.mlkit.ui.views.googleLensClone

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import com.jimmy.mlkit.R

import kotlinx.android.synthetic.main.activity_google_lens.*

class GoogleLens : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_lens)
        setSupportActionBar(toolbar)


    }

}
