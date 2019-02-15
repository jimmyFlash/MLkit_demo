package com.jimmy.ml_firebase.permissions

import android.content.pm.PackageManager
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.jimmy.ml_firebase.ui.action
import com.jimmy.ml_firebase.ui.snack


object  PermissionsManagerWExtion{


    /**
     * verify if the user has granted the permission
     */
    fun hasPermission(activity: AppCompatActivity, permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
    }



    /**
     * asks for the array of  permissions. If the user rejected those permissions previously,
     * it will display a nice snackbar with a message.
     */
    fun requestPermissions(activity : AppCompatActivity,
                           permissionsArray : Array<String>,
                           requestCode : Int,
                           view :View,
                           permissionMsg: String,
                           confirmMsg:String){
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionsArray[0])) {

            view.snack(permissionMsg, Snackbar.LENGTH_INDEFINITE) {
                action(confirmMsg) {
                    ActivityCompat.requestPermissions(activity, permissionsArray, requestCode)
                }
            }
        } else {
            ActivityCompat.requestPermissions(activity, permissionsArray,  requestCode)
        }
    }
}


