package com.jimmy.actions.uiutils


import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.view.WindowManager

fun AppCompatActivity.makeFullScreen(){
    // Remove Title
    requestWindowFeature(Window.FEATURE_NO_TITLE)

    // Make Fullscreen
    window.setFlags(
        WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN)

}

fun AppCompatActivity.hidesupportActionBar(){

    supportActionBar?.hide()

}



