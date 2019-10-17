package com.jimmy.actions

import android.content.Context
import android.content.Intent
import java.io.Serializable

object Actions {

    const val GOOGLE_LENS_OPEN = "action.googlelens.open"
//      fun openGoogleLens() = Intent("action.googlelens.open")
      fun openGoogleLens(context: Context, extras :  Map<String, Any?>? = null) =
            internalIntent(context, GOOGLE_LENS_OPEN, extras)


    /**
     * relying on implicit Intents can cause chooser dialogs to pop up.
     * While a collision with a 3rd party app is unlikely,
     * it can easily happen for different build flavors.
     *
     * easily be avoided by restricting the intents to the current package
     */
    private fun internalIntent(context: Context, action: String, extras : Map<String, Any?>? = null) =
        Intent(action).apply {
            setPackage(context.packageName)
            if(extras != null){
                for ((key, value) in extras) {
                    println("$key = $value")
                    when(value) {
                        is Int ->  putExtra(key, value)
                        is String -> putExtra(key, value)
                        is Serializable -> putExtra(key, value)
                        is Boolean -> putExtra(key, value)
                    }
                }

            }
        }

}