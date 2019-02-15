package com.jimmy.ml_firebase

import android.app.Application
import com.google.firebase.FirebaseApp

class ApplicationClass : Application() {

    override fun onCreate() {
        super.onCreate()
//        This ensures Firebase is initialized when your app is launched.
        FirebaseApp.initializeApp(this)
    }
}