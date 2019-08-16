package com.jimmy.actions

import android.content.Context
import android.content.Intent

object Actions {

    const val GOOGLE_LENS_OPEN = "action.googlelens.open"
//      fun openGoogleLens() = Intent("action.googlelens.open")
      fun openGoogleLens(context: Context) = internalIntent(context, GOOGLE_LENS_OPEN)


    private fun internalIntent(context: Context, action: String) =
        Intent(action).setPackage(context.packageName)

}